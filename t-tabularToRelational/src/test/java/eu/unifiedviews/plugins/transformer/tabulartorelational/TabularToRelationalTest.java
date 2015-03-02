package eu.unifiedviews.plugins.transformer.tabulartorelational;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class TabularToRelationalTest {

    private TabularToRelational dpu;

    private TestEnvironment env;

    private WritableFilesDataUnit input;

    private WritableRelationalDataUnit output;

    public static final String CSV_FILE = "sample.csv";

    public static final int CSV_FILE_ROW_COUNT = 11;

    @Before
    public void init() throws Exception {
        // prepare DPU
        TabularToRelationalConfig_V1 config = new TabularToRelationalConfig_V1();

        dpu = new TabularToRelational();
        dpu.configureDirectly(config);

        env = new TestEnvironment();
        input = env.createFilesInput("input");
        output = env.createRelationalOutput("output");
    }

    @After
    public void after() throws Exception {
        env.release();
    }

    @Test
    public void sampleCsvFilePasses() throws Exception {
        TabularToRelationalConfig_V1 config = new TabularToRelationalConfig_V1();
        config.setTableName("TEST_TABLE");
        List<ColumnMappingEntry> list = new ArrayList<>();
        list.add(new ColumnMappingEntry("id", "INT", true));
        list.add(new ColumnMappingEntry("code", "VARCHAR(255)", false));
        list.add(new ColumnMappingEntry("county", "VARCHAR(255)", false));
        config.setColumnMapping(list);

        Connection conn = null;
        Statement stmnt = null;
        ResultSet rs = null;
        try {
            dpu.configureDirectly(config);

            addFileToInput(CSV_FILE);
            env.run(dpu);

            conn = output.getDatabaseConnection();
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery("SELECT COUNT(*) FROM TEST_TABLE");
            rs.next();
            int rowCount = rs.getInt(1);
            assertEquals("Sample CSV should contain 11 entries!", CSV_FILE_ROW_COUNT, rowCount);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmnt != null) {
                    stmnt.close();
                }
            } catch (SQLException ignore) {
            }
            DatabaseHelper.tryCloseConnection(conn);
        }
    }

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

    private void addFileToInput(final String filename) throws Exception {
        input.addExistingFile(filename, this.getClass().getClassLoader().getResource(filename).toString());
    }
}
