package eu.unifiedviews.plugins.transformer.tabulartorelational.parser;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.exec.ExecContext;
import eu.unifiedviews.helpers.dpu.exec.UserExecContext;
import eu.unifiedviews.helpers.dpu.test.config.ConfigurationBuilder;
import eu.unifiedviews.plugins.transformer.tabulartorelational.TabularToRelational;
import eu.unifiedviews.plugins.transformer.tabulartorelational.TabularToRelationalConfig_V2;
import eu.unifiedviews.plugins.transformer.tabulartorelational.model.ColumnMappingEntry;
import eu.unifiedviews.plugins.transformer.tabulartorelational.model.ParserType;
import eu.unifiedviews.plugins.transformer.tabulartorelational.util.DatabaseHelper;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class CSVParserTest {

    private TabularToRelational dpu;

    private TestEnvironment env;

    private WritableFilesDataUnit input;

    private WritableRelationalDataUnit output;

    public static final String CSV_FILE = "sample.csv";

    public static final String CSV_ENCODING_FILE = "sample_encoding.csv";

    public static final String CSV_WITH_HEADER_FILE = "sample_encoding.csv";

    public static final int CSV_FILE_ROW_COUNT = 11;

    public static final int CSV_FILE_WITH_HEADER_ROW_COUNT = 104;

    @Before
    public void init() throws Exception {
        // prepare DPU
        TabularToRelationalConfig_V2 config = new TabularToRelationalConfig_V2();

        dpu = new TabularToRelational();
        dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        env = new TestEnvironment();
        input = env.createFilesInput("input");
        output = env.createRelationalOutput("output");
    }

    @After
    public void after() throws Exception {
        env.release();
    }

    @Test
    public void sampleCSVWithEncodingPasses() throws Exception {
        TabularToRelationalConfig_V2 config = new TabularToRelationalConfig_V2();
        config.setParserType(ParserType.CSV);
        config.setFieldSeparator(";");
        config.setFieldDelimiter("\"");
        config.setEncoding("windows-1250");
        config.setTableName("test");
        List<ColumnMappingEntry> list = new ArrayList<>();
        list.add(new ColumnMappingEntry("nazov", "VARCHAR", false));
        list.add(new ColumnMappingEntry("predmet", "VARCHAR", false));
        list.add(new ColumnMappingEntry("hyperlink", "VARCHAR", false));
        list.add(new ColumnMappingEntry("datum", "VARCHAR", false));
        config.setColumnMapping(list);

        Connection conn = null;
        Statement stmnt = null;
        ResultSet rs = null;
        try {
            dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

            addFileToInput(CSV_ENCODING_FILE);
            env.run(dpu);

            conn = output.getDatabaseConnection();
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery("SELECT * FROM test LIMIT 1");

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
                    sb.append(rs.getString(i) + ", ");
                }
            }
            assertEquals("Názov obstarávateľa, Predmet, Hyperlink, Dátum, ", sb.toString());
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
    public void sampleCsvFilePasses() throws Exception {
        TabularToRelationalConfig_V2 config = new TabularToRelationalConfig_V2();
        config.setParserType(ParserType.CSV);
        config.setTableName("daka_tabulka");
        List<ColumnMappingEntry> list = new ArrayList<>();
        list.add(new ColumnMappingEntry("id", "VARCHAR", true));
        list.add(new ColumnMappingEntry("neznamy_kod", "VARCHAR", false));
        list.add(new ColumnMappingEntry("kod_krajiny", "VARCHAR", false));
        config.setColumnMapping(list);

        Connection conn = null;
        Statement stmnt = null;
        ResultSet rs = null;
        try {
            dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

            addFileToInput(CSV_FILE);
            env.run(dpu);

            conn = output.getDatabaseConnection();
            stmnt = conn.createStatement();

            rs = stmnt.executeQuery("SELECT COUNT(*) FROM daka_tabulka");
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
    public void sampleCsvWithHeaderPasses() throws Exception {
        TabularToRelationalConfig_V2 config = new TabularToRelationalConfig_V2();
        config.setParserType(ParserType.CSV);
        config.setDataBegginningRow(2);
        config.setTableName("test");
        List<ColumnMappingEntry> list = new ArrayList<>();
        list.add(new ColumnMappingEntry("street", "VARCHAR", false));
        list.add(new ColumnMappingEntry("city", "VARCHAR", false));
        list.add(new ColumnMappingEntry("zip", "VARCHAR", false));
        list.add(new ColumnMappingEntry("state", "VARCHAR", false));
        list.add(new ColumnMappingEntry("beds", "VARCHAR", false));
        list.add(new ColumnMappingEntry("baths", "VARCHAR", false));
        list.add(new ColumnMappingEntry("sq__ft", "VARCHAR", false));
        list.add(new ColumnMappingEntry("type", "VARCHAR", false));
        list.add(new ColumnMappingEntry("sale_date", "VARCHAR", false));
        list.add(new ColumnMappingEntry("price", "VARCHAR", false));
        list.add(new ColumnMappingEntry("latitude", "VARCHAR", false));
        list.add(new ColumnMappingEntry("longitude", "VARCHAR", false));
        // add more columns that the file really contains
        list.add(new ColumnMappingEntry("boo", "VARCHAR", false));
        list.add(new ColumnMappingEntry("foo", "VARCHAR", false));
        config.setColumnMapping(list);

        Connection conn = null;
        Statement stmnt = null;
        ResultSet rs = null;
        try {
            dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

            addFileToInput(CSV_WITH_HEADER_FILE);
            env.run(dpu);

            conn = output.getDatabaseConnection();
            stmnt = conn.createStatement();

            rs = stmnt.executeQuery("SELECT COUNT(*) FROM test");
            rs.next();
            int rowCount = rs.getInt(1);
            assertEquals("Sample CSV should contain 11 entries!", CSV_FILE_WITH_HEADER_ROW_COUNT, rowCount);
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
    public void processCsvOptionsTest() throws DPUConfigException {
        TabularToRelationalConfig_V2 config = new TabularToRelationalConfig_V2();
        UserExecContext ctx = new UserExecContext(new ExecContext(dpu));
        CSVParser parser = new CSVParser(ctx, config, output);
        assertEquals("charset=UTF-8 fieldDelimiter=\" fieldSeparator=,", parser.processCsvOptions());

        config.setEncoding("UTF-16");
        config.setFieldSeparator("|");
        config.setFieldDelimiter("'");
        assertEquals("charset=UTF-16 fieldDelimiter=' fieldSeparator=|", parser.processCsvOptions());

        config.setEncoding(null);
        config.setFieldSeparator(null);
        config.setFieldDelimiter(null);
        assertEquals("", parser.processCsvOptions());
    }

    @Test
    public void prepareCreateTableQueryTest() {
        TabularToRelationalConfig_V2 config = new TabularToRelationalConfig_V2();
        UserExecContext ctx = new UserExecContext(new ExecContext(dpu));
        CSVParser parser = new CSVParser(ctx, config, output);
        assertNotNull(parser.buildCreateTableQuery(config.getColumnMapping()));

        config.setTableName("test_table");
        config.setFieldSeparator("\"");
        config.setFieldDelimiter(",");
        config.setEncoding("UTF-8");
        List<ColumnMappingEntry> list = new ArrayList<>();
        list.add(new ColumnMappingEntry("id", "INT", true));
        list.add(new ColumnMappingEntry("name", "VARCHAR(255)", true));
        list.add(new ColumnMappingEntry("surname", "VARCHAR(255)", false));
        config.setColumnMapping(list);

        assertEquals("CREATE TABLE test_table (id INT, name VARCHAR(255), surname VARCHAR(255), PRIMARY KEY (id, name));", parser.buildCreateTableQuery(config.getColumnMapping()));
    }

    private void addFileToInput(final String filename) throws Exception {
        input.addExistingFile(filename, this.getClass().getClassLoader().getResource(filename).toString());
    }
}
