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
public class DBFParserTest {

    private TabularToRelational dpu;

    private TestEnvironment env;

    private WritableFilesDataUnit input;

    private WritableRelationalDataUnit output;

    public static final String DBF_FILE = "sample_accents.dbf";

    public static final int DBF_FILE_ROW_COUNT = 137;

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
    public void sampleDBFPasses() throws Exception {
        TabularToRelationalConfig_V2 config = new TabularToRelationalConfig_V2();
        config.setParserType(ParserType.DBF);
        config.setEncoding("windows-1250");
        config.setDataBegginningRow(2);
        config.setTableName("dbf_test_table");
        List<ColumnMappingEntry> list = new ArrayList<>();
        list.add(new ColumnMappingEntry("obec", "VARCHAR", false));
        list.add(new ColumnMappingEntry("okres", "VARCHAR", false));
        list.add(new ColumnMappingEntry("kraj", "VARCHAR", false));
        list.add(new ColumnMappingEntry("obyvatelstvo", "VARCHAR", false));
        config.setColumnMapping(list);

        Connection conn = null;
        Statement stmnt = null;
        ResultSet rs = null;
        try {
            dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

            addFileToInput(DBF_FILE);
            env.run(dpu);

            conn = output.getDatabaseConnection();
            stmnt = conn.createStatement();

            rs = stmnt.executeQuery("SELECT * FROM dbf_test_table LIMIT 1");
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
                    sb.append(rs.getString(i) + ", ");
                }
            }
            assertEquals("Ábelová                     , Lučenec           , Banskobystrický, 231.0, ", sb.toString());
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
