package eu.unifiedviews.plugins.transformer.relational;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.relational.RelationalDataUnit;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import eu.unifiedviews.helpers.dataunit.relationalhelper.RelationalHelper;

public class RelationalTest {

    private static final String TABLE_NAME_USERS = "USER_DATA";

    private static final String TABLE_NAME_ADDRESS = "ADDRESS";

    private static final String SELECT_JOIN_TABLES_QUERY = "SELECT name, surname, city FROM " + TABLE_NAME_USERS + " JOIN " + TABLE_NAME_ADDRESS
            + " ON " + TABLE_NAME_USERS + ".id = " + TABLE_NAME_ADDRESS + ".user_id";

    private static final String[] TABLE_USER_COLUMNS = new String[] { "id", "name", "surname", "age" };

    private static final String[] TABLE_USER_COLUMN_TYPES = new String[] { "INTEGER", "VARCHAR(255)", "VARCHAR(255)", "INTEGER" };

    private static final String[] TABLE_ADDRESS_COLUMNS = new String[] { "id", "user_id", "street", "city", "number" };

    private static final String[] TABLE_ADDRESS_COLUMN_TYPES = new String[] { "INTEGER", "INTEGER", "VARCHAR(255)", "VARCHAR(255)", "INTEGER" };

    private static final String TARGET_TABLE_NAME = "INTERNAL_TABLE";

    private static final String[] JOINED_TABLE_COLUMNS = new String[] { "name", "surname", "city" };

    private static final String[] JOINED_TABLE_COLUMN_TYPES = new String[] { "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)" };

    private Relational dpu;

    private WritableRelationalDataUnit relationalInput;

    private WritableRelationalDataUnit relationalOutput;

    private TestEnvironment testEnv;

    @Before
    public void before() throws Exception {
        this.dpu = new Relational();

        this.testEnv = new TestEnvironment();
        this.relationalInput = this.testEnv.createRelationalInput("inputTables");
        this.relationalOutput = this.testEnv.createRelationalOutput("outputTable");

        Connection conn = null;
        try {
            conn = this.relationalInput.getDatabaseConnection();
            createAndFillUsersSourceTable(conn);
            createAndFillAddressSourceTable(conn);
            this.relationalInput.addExistingDatabaseTable(TABLE_NAME_USERS, TABLE_NAME_USERS);
            this.relationalInput.addExistingDatabaseTable(TABLE_NAME_ADDRESS, TABLE_NAME_ADDRESS);

        } finally {
            DatabaseHelper.tryCloseConnection(conn);
        }

    }

    @After
    public void after() throws Exception {
        this.testEnv.release();
    }

    @Test
    public void checkOutputTableAndColumnsTest() throws Exception {
        this.dpu.configureDirectly(createDpuConfig(SELECT_JOIN_TABLES_QUERY));
        this.testEnv.run(this.dpu);

        Connection dataUnitConnection = null;
        try {
            Set<RelationalDataUnit.Entry> dbTables = RelationalHelper.getTables(this.relationalOutput);
            Assert.assertEquals(false, dbTables.isEmpty());
            Assert.assertEquals(1, dbTables.size());
            String tableName = dbTables.iterator().next().getTableName();

            Assert.assertEquals(TARGET_TABLE_NAME, tableName);

            dataUnitConnection = this.relationalOutput.getDatabaseConnection();
            Assert.assertEquals(true, DatabaseHelper.checkTableExists(dataUnitConnection, tableName));
            Assert.assertEquals(true, checkColumns(dataUnitConnection, tableName, JOINED_TABLE_COLUMNS, JOINED_TABLE_COLUMN_TYPES));
        } finally {
            DatabaseHelper.tryCloseConnection(dataUnitConnection);
        }
    }

    @Test
    public void checkOutputTableContent() throws Exception {
        this.dpu.configureDirectly(createDpuConfig(SELECT_JOIN_TABLES_QUERY));
        this.testEnv.run(this.dpu);

        Connection dataUnitConnection = null;
        ResultSet rs = null;
        Statement stmnt = null;
        try {
            dataUnitConnection = this.relationalOutput.getDatabaseConnection();
            stmnt = dataUnitConnection.createStatement();
            rs = stmnt.executeQuery("SELECT * FROM " + TARGET_TABLE_NAME);
            int rowCount = 0;
            rs.next();
            rowCount++;
            Assert.assertEquals("Tom", rs.getString(1));
            Assert.assertEquals("Black", rs.getString(2));
            Assert.assertEquals("London", rs.getString(3));

            while (rs.next()) {
                rowCount++;
            }
            Assert.assertEquals(2, rowCount);
        } finally {
            DatabaseHelper.tryCloseDbResources(dataUnitConnection, stmnt, rs);
        }

    }

    private RelationalConfig_V1 createDpuConfig(String query) {
        RelationalConfig_V1 config = new RelationalConfig_V1();
        config.setTargetTableName(TARGET_TABLE_NAME);
        config.setSqlQuery(query);

        return config;
    }

    private static final void createTable(Connection conn, String tableName, String[] columnNames, String[] columnTypes) throws SQLException {
        StringBuilder query = new StringBuilder("CREATE TABLE ");
        query.append(tableName);
        query.append(" (");
        for (int i = 0; i < columnNames.length; i++) {
            query.append(columnNames[i]);
            query.append(" ");
            query.append(columnTypes[i]);
            query.append(", ");
        }
        query.setLength(query.length() - 2);
        query.append(")");

        Statement stmnt = null;
        try {
            stmnt = conn.createStatement();
            stmnt.execute(query.toString());
            conn.commit();
        } finally {
            try {
                if (stmnt != null) {
                    stmnt.close();
                }
            } catch (SQLException ignore) {
            }
        }
    }

    private void createAndFillAddressSourceTable(Connection conn) throws SQLException {
        createTable(conn, TABLE_NAME_ADDRESS, TABLE_ADDRESS_COLUMNS, TABLE_ADDRESS_COLUMN_TYPES);
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("INSERT INTO " + TABLE_NAME_ADDRESS + " (id, user_id, street, city, number) VALUES (?, ?, ?, ?, ?)");

            ps.setInt(1, 1);
            ps.setInt(2, 1);
            ps.setString(3, "Regent street");
            ps.setString(4, "London");
            ps.setInt(5, 25);
            ps.execute();

            ps.setInt(1, 2);
            ps.setInt(2, 2);
            ps.setString(3, "Evergreen terrace");
            ps.setString(4, "Springfield");
            ps.setInt(5, 637);
            ps.execute();

            conn.commit();

        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ignore) {
            }
        }
    }

    private void createAndFillUsersSourceTable(Connection conn) throws SQLException {
        createTable(conn, TABLE_NAME_USERS, TABLE_USER_COLUMNS, TABLE_USER_COLUMN_TYPES);
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("INSERT INTO " + TABLE_NAME_USERS + " (id, name, surname, age) VALUES (?, ?, ?, ?)");

            ps.setInt(1, 1);
            ps.setString(2, "Tom");
            ps.setString(3, "Black");
            ps.setInt(4, 25);
            ps.execute();

            ps.setInt(1, 2);
            ps.setString(2, "Jim");
            ps.setString(3, "Carey");
            ps.setInt(4, 33);
            ps.execute();

            conn.commit();

        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ignore) {
            }
        }
    }

    private boolean checkColumns(Connection conn, String tableName, String[] columns, String[] columnTypes) throws SQLException {
        boolean bColumnsEqual = true;
        Set<String> columnSet = new HashSet<>();
        ResultSet rs = null;
        Statement statement = null;

        ResultSetMetaData meta = null;
        for (String column : columns) {
            columnSet.add(column.toUpperCase());
        }

        try {
            statement = conn.createStatement();
            rs = statement.executeQuery("SELECT * FROM " + tableName);
            meta = rs.getMetaData();

            if (columns.length != meta.getColumnCount()) {
                return false;
            }
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                if (!columnSet.contains(meta.getColumnName(i).toUpperCase())) {
                    bColumnsEqual = false;
                }
            }
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }

            } catch (Exception ignore) {
            }
        }

        return bColumnsEqual;
    }

}
