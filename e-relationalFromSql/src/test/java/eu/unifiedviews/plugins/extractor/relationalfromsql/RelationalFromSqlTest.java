package eu.unifiedviews.plugins.extractor.relationalfromsql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.relational.RelationalDataUnit;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import eu.unifiedviews.helpers.dataunit.relationalhelper.RelationalHelper;
import eu.unifiedviews.plugins.extractor.relationalfromsql.SqlDatabase.DatabaseType;

public class RelationalFromSqlTest {

    private TestEnvironment testEnv;

    private WritableRelationalDataUnit relationalOutput;

    private RelationalFromSql dpu;

    private static final String DATABASE_URL = "jdbc:h2:mem:db1";

    private static final String JDBC_DRIVER = "org.h2.Driver";

    private static final String DB_USER = "user1";

    private static final String DB_PASSWORD = "pwd1";

    private static final String TABLE_NAME_USERS = "USER_DATA";

    private static final String TABLE_NAME_ADDRESS = "ADDRESS";

    private static final String SELECT_ONE_TABLE_QUERY = "SELECT * FROM " + TABLE_NAME_USERS;

    private static final String SELECT_JOIN_TABLES_QUERY = "SELECT name, surname, city FROM " + TABLE_NAME_USERS + " JOIN " + TABLE_NAME_ADDRESS
            + " ON " + TABLE_NAME_USERS + ".id = " + TABLE_NAME_ADDRESS + ".user_id";

    private static final String[] TABLE_USER_COLUMNS = new String[] { "id", "name", "surname", "age" };

    private static final String[] TABLE_USER_COLUMN_TYPES = new String[] { "INTEGER", "VARCHAR(255)", "VARCHAR(255)", "INTEGER" };

    private static final String[] TABLE_ADDRESS_COLUMNS = new String[] { "id", "user_id", "street", "city", "number" };

    private static final String[] TABLE_ADDRESS_COLUMN_TYPES = new String[] { "INTEGER", "INTEGER", "VARCHAR(255)", "VARCHAR(255)", "INTEGER" };

    private static final String TARGET_TABLE_NAME = "INTERNAL_TABLE";

    private static final String[] JOINED_TABLE_COLUMNS = new String[] { "name", "surname", "city" };

    private static final String[] JOINED_TABLE_COLUMN_TYPES = new String[] { "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)" };

    @Before
    public void before() throws Exception {
        this.dpu = new RelationalFromSql();

        this.testEnv = new TestEnvironment();
        this.relationalOutput = this.testEnv.createRelationalOutput("outputTables");
    }

    @After
    public void after() throws Exception {
        this.testEnv.release();
    }

    @Test
    public void selectSingleTableCheckTargetTableTest() throws Exception {
        RelationalFromSqlConfig_V2 config = createConfig(SELECT_ONE_TABLE_QUERY);
        this.dpu.configureDirectly(config);
        Connection sourceConnection = null;
        Connection dataUnitConnection = null;
        try {
            sourceConnection = createConnection();
            createAndFillUsersSourceTable(sourceConnection);

            this.testEnv.run(this.dpu);

            Set<RelationalDataUnit.Entry> dbTables = RelationalHelper.getTables(this.relationalOutput);
            Assert.assertEquals(false, dbTables.isEmpty());
            Assert.assertEquals(1, dbTables.size());
            String tableName = dbTables.iterator().next().getTableName();

            Assert.assertEquals(TARGET_TABLE_NAME, tableName);

            dataUnitConnection = this.relationalOutput.getDatabaseConnection();
            Assert.assertEquals(true, checkTableExists(dataUnitConnection, tableName));
            Assert.assertEquals(true, checkColumns(dataUnitConnection, tableName, TABLE_USER_COLUMNS, TABLE_USER_COLUMN_TYPES));
        } finally {
            tryCloseConnection(sourceConnection);
            tryCloseConnection(dataUnitConnection);
        }
    }

    @Test
    public void selectSingleTableCheckTargetTableContent() throws Exception {
        RelationalFromSqlConfig_V2 config = createConfig(SELECT_ONE_TABLE_QUERY);
        this.dpu.configureDirectly(config);
        Connection sourceConnection = null;
        Connection dataUnitConnection = null;
        ResultSet rs = null;
        Statement stmnt = null;
        try {
            sourceConnection = createConnection();
            createAndFillUsersSourceTable(sourceConnection);

            this.testEnv.run(this.dpu);
            dataUnitConnection = this.relationalOutput.getDatabaseConnection();
            stmnt = dataUnitConnection.createStatement();
            rs = stmnt.executeQuery("SELECT * FROM " + TARGET_TABLE_NAME);
            int rowCount = 0;
            rs.next();
            rowCount++;
            Assert.assertEquals(1, rs.getInt(1));
            Assert.assertEquals("Tom", rs.getString(2));
            Assert.assertEquals("Black", rs.getString(3));
            Assert.assertEquals(25, rs.getInt(4));

            while (rs.next()) {
                rowCount++;
            }
            Assert.assertEquals(2, rowCount);
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
            tryCloseConnection(sourceConnection);
            tryCloseConnection(dataUnitConnection);
        }
    }

    @Test
    public void selectJoinTablesCheckTargetTableTest() throws Exception {
        RelationalFromSqlConfig_V2 config = createConfig(SELECT_JOIN_TABLES_QUERY);
        this.dpu.configureDirectly(config);
        Connection sourceConnection = null;
        Connection dataUnitConnection = null;
        try {
            sourceConnection = createConnection();
            createAndFillUsersSourceTable(sourceConnection);
            createAndFillAddressSourceTable(sourceConnection);

            this.testEnv.run(this.dpu);

            Set<RelationalDataUnit.Entry> dbTables = RelationalHelper.getTables(this.relationalOutput);
            Assert.assertEquals(false, dbTables.isEmpty());
            Assert.assertEquals(1, dbTables.size());
            String tableName = dbTables.iterator().next().getTableName();

            Assert.assertEquals(TARGET_TABLE_NAME, tableName);

            dataUnitConnection = this.relationalOutput.getDatabaseConnection();
            Assert.assertEquals(true, checkTableExists(dataUnitConnection, tableName));
            Assert.assertEquals(true, checkColumns(dataUnitConnection, tableName, JOINED_TABLE_COLUMNS, JOINED_TABLE_COLUMN_TYPES));
        } finally {
            tryCloseConnection(sourceConnection);
            tryCloseConnection(dataUnitConnection);
        }
    }

    @Test
    public void selectJoinTablesCheckTargetTableContent() throws Exception {
        RelationalFromSqlConfig_V2 config = createConfig(SELECT_JOIN_TABLES_QUERY);
        this.dpu.configureDirectly(config);
        Connection sourceConnection = null;
        Connection dataUnitConnection = null;
        ResultSet rs = null;
        Statement stmnt = null;
        try {
            sourceConnection = createConnection();
            createAndFillUsersSourceTable(sourceConnection);
            createAndFillAddressSourceTable(sourceConnection);

            this.testEnv.run(this.dpu);
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
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmnt != null) {
                    stmnt.close();
                }
            } catch (SQLException ignore) {
            }
            tryCloseConnection(sourceConnection);
            tryCloseConnection(dataUnitConnection);
        }
    }

    private static void tryCloseConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ignore) {

        }
    }

    private RelationalFromSqlConfig_V2 createConfig(String sqlQuery) {
        RelationalFromSqlConfig_V2 config = new RelationalFromSqlConfig_V2();
        config.setDatabaseHost("mem");
        config.setDatabaseName("db1");
        config.setUserName(DB_USER);
        config.setUserPassword(DB_PASSWORD);
        config.setUseSSL(false);
        config.setTargetTableName(TARGET_TABLE_NAME);
        config.setSqlQuery(sqlQuery);
        config.setDatabaseType(DatabaseType.H2_MEM);

        return config;
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

    private Connection createConnection() throws SQLException {
        Connection conn = null;
        final Properties connectionProperties = new Properties();
        connectionProperties.setProperty("user", DB_USER);
        connectionProperties.setProperty("password", DB_PASSWORD);
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Failed to load JDBC driver", e);
        }

        conn = DriverManager.getConnection(DATABASE_URL, connectionProperties);
        conn.setAutoCommit(false);

        return conn;

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

    private boolean checkTableExists(Connection connection, String dbTableName) throws SQLException {
        boolean bTableExists = false;
        DatabaseMetaData dbm = null;
        ResultSet tables = null;
        try {
            dbm = connection.getMetaData();
            tables = dbm.getTables(null, null, dbTableName, null);
            if (tables.next()) {
                bTableExists = true;
            }
        } finally {
            try {
                if (tables != null) {
                    tables.close();
                }
            } catch (SQLException ignore) {
            }
        }
        return bTableExists;
    }

}
