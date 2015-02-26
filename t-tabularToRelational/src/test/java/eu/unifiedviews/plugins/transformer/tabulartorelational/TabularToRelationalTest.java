package eu.unifiedviews.plugins.transformer.tabulartorelational;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TabularToRelationalTest {



    @Test
    public void joinColumnNamesTest() {
        List<ColumnMappingEntry> list = new ArrayList<>();
        list.add(new ColumnMappingEntry("col1", "", false));
        list.add(new ColumnMappingEntry("col2", "", false));
        list.add(new ColumnMappingEntry("col3", "", false));
        list.add(new ColumnMappingEntry("col4", "", false));
        list.add(new ColumnMappingEntry("col5", "", false));

        assertEquals("", TabularToRelational.joinColumnNames(new ArrayList<ColumnMappingEntry>(), ", "));
        assertEquals("COL1, COL2, COL3, COL4, COL5", TabularToRelational.joinColumnNames(list, ", "));
        assertEquals("COL1;COL2;COL3;COL4;COL5", TabularToRelational.joinColumnNames(list, ";"));
    }

    @Test
    public void processStringTest() {
        String testString = "NAME";
        assertEquals("NAME", TabularToRelational.processString(testString));
        testString = " NAME ";
        assertEquals("NAME", TabularToRelational.processString(testString));
        testString = " table_name ";
        assertEquals("TABLE_NAME", TabularToRelational.processString(testString));
        testString = "";
        assertEquals("", TabularToRelational.processString(testString));
        testString = null;
        assertEquals("", TabularToRelational.processString(testString));
    }

    @Test
    public void processCsvOptionsTest() {
        TabularToRelationalConfig_V1 config = new TabularToRelationalConfig_V1();
        assertEquals("'charset=UTF-8 fieldDelimiter=\" fieldSeparator=,'", TabularToRelational.processCsvOptions(config));

        config.setEncoding("UTF-16");
        config.setFieldSeparator("|");
        config.setFieldDelimiter("'");
        assertEquals("'charset=UTF-16 fieldDelimiter=' fieldSeparator=|'", TabularToRelational.processCsvOptions(config));

        config.setEncoding(null);
        config.setFieldSeparator(null);
        config.setFieldDelimiter(null);
        assertEquals("''", TabularToRelational.processCsvOptions(config));
    }

    @Test
    public void prepareCreateTableQueryTest() {
        TabularToRelationalConfig_V1 config = new TabularToRelationalConfig_V1();
        assertNotNull(TabularToRelational.prepareCreateTableQuery(config));

        config.setTableName("test_table");
        config.setFieldSeparator("\"");
        config.setFieldDelimiter(",");
        config.setEncoding("UTF-8");
        List<ColumnMappingEntry> list = new ArrayList<>();
        list.add(new ColumnMappingEntry("id", "INT", true));
        list.add(new ColumnMappingEntry("name", "VARCHAR(255)", true));
        list.add(new ColumnMappingEntry("surname", "VARCHAR(255)", false));
        config.setColumnMapping(list);

        assertEquals("CREATE TABLE TEST_TABLE(ID INT, NAME VARCHAR(255), SURNAME VARCHAR(255),  PRIMARY KEY (ID, NAME))", TabularToRelational.prepareCreateTableQuery(config));
    }



}
