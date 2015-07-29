package eu.unifiedviews.plugins.transformer.tabulartorelational.parser;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
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

@RunWith(JUnit4.class)
public class XLSParserTest {

    private TabularToRelational dpu;

    private TestEnvironment env;

    private WritableFilesDataUnit input;

    private WritableRelationalDataUnit output;

    public static final String XLS_FILE = "sample.xls";

    public static final String XLSX_FILE = "sample.xlsx";

    public static final String XLS_FILE_WITH_ACCENTS = "sample_accents.xls";

    public static final int XLS_FILE_ROW_COUNT = 137;

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
    public void sampleXLSPasses() throws Exception {
        TabularToRelationalConfig_V2 config = new TabularToRelationalConfig_V2();
        config.setParserType(ParserType.XLS);
        config.setDataBegginningRow(2);
        config.setTableName("xls_test_table");
        List<ColumnMappingEntry> list = new ArrayList<>();
        list.add(new ColumnMappingEntry("ID", "VARCHAR", false));
        list.add(new ColumnMappingEntry("Point", "VARCHAR", false));
        list.add(new ColumnMappingEntry("Strain", "VARCHAR", false));
        list.add(new ColumnMappingEntry("sex", "VARCHAR", false));
        list.add(new ColumnMappingEntry("sex_code", "VARCHAR", false));
        list.add(new ColumnMappingEntry("age", "VARCHAR", false));
        list.add(new ColumnMappingEntry("bodywt", "VARCHAR", false));
        list.add(new ColumnMappingEntry("brainwt", "VARCHAR", false));
        list.add(new ColumnMappingEntry("MedUNshOB", "VARCHAR", false));
        list.add(new ColumnMappingEntry("Res1_sex", "VARCHAR", false));
        list.add(new ColumnMappingEntry("Res2_sex_age", "VARCHAR", false));
        list.add(new ColumnMappingEntry("Res3_sex_age_bw", "VARCHAR", false));
        list.add(new ColumnMappingEntry("Res4_sex_age_bw_brnw", "VARCHAR", false));
        config.setColumnMapping(list);

        Connection conn = null;
        Statement stmnt = null;
        ResultSet rs = null;
        try {
            dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

            addFileToInput(XLS_FILE);
            env.run(dpu);

            conn = output.getDatabaseConnection();
            stmnt = conn.createStatement();

            rs = stmnt.executeQuery("SELECT COUNT(*) FROM xls_test_table");
            rs.next();
            int rowCount = rs.getInt(1);
            assertEquals("Sample XLS should contain 137 entries!", XLS_FILE_ROW_COUNT, rowCount);
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
    public void sampleXLSWithAccentsPasses() throws Exception {
        TabularToRelationalConfig_V2 config = new TabularToRelationalConfig_V2();
        config.setParserType(ParserType.XLS);
        config.setDataBegginningRow(2);
        config.setTableName("xls_test_table_with_accents");
        List<ColumnMappingEntry> list = new ArrayList<>();
        list.add(new ColumnMappingEntry("obec", "VARCHAR", false));
        list.add(new ColumnMappingEntry("okres", "VARCHAR", false));
        list.add(new ColumnMappingEntry("kraj", "VARCHAR", false));
        list.add(new ColumnMappingEntry("obyv", "VARCHAR", false));
        config.setColumnMapping(list);

        Connection conn = null;
        Statement stmnt = null;
        ResultSet rs = null;
        try {
            dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

            addFileToInput(XLS_FILE_WITH_ACCENTS);
            env.run(dpu);

            conn = output.getDatabaseConnection();
            stmnt = conn.createStatement();
            rs = stmnt.executeQuery("SELECT * FROM xls_test_table_with_accents LIMIT 1");

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
                    sb.append(rs.getString(i) + ", ");
                }
            }
            assertEquals("Result of query should be as expected!", "Ábelová, Lučenec, Banskobystrický, 231, ", sb.toString());
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
    public void sampleXLSXPasses() throws Exception {
        TabularToRelationalConfig_V2 config = new TabularToRelationalConfig_V2();
        config.setParserType(ParserType.XLS);
        config.setDataBegginningRow(7);
        config.setTableName("xlsx_test_table");
        List<ColumnMappingEntry> list = new ArrayList<>();
        list.add(new ColumnMappingEntry("ID", "VARCHAR", false));
        list.add(new ColumnMappingEntry("nazov", "VARCHAR", false));
        list.add(new ColumnMappingEntry("kredit", "VARCHAR", false));
        list.add(new ColumnMappingEntry("debet", "VARCHAR", false));
        list.add(new ColumnMappingEntry("saldo", "VARCHAR", false));
        config.setColumnMapping(list);

        Connection conn = null;
        Statement stmnt = null;
        ResultSet rs = null;
        try {
            dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

            addFileToInput(XLSX_FILE);
            env.run(dpu);

            conn = output.getDatabaseConnection();
            stmnt = conn.createStatement();

            rs = stmnt.executeQuery("SELECT * FROM xlsx_test_table LIMIT 1");
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
                    sb.append(rs.getString(i) + ", ");
                }
            }
            assertEquals("Result of query should be as expected!", "1., Bežný účet, 18,694.1, 18,373.3, 320.9, ", sb.toString());
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

    private void addFileToInput(final String filename) throws Exception {
        input.addExistingFile(filename, this.getClass().getClassLoader().getResource(filename).toString());
    }
}
