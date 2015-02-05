package eu.unifiedviews.plugins.extractor.relationalfromsql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationalFromSqlHelper {

    private static final Logger LOG = LoggerFactory.getLogger(RelationalFromSqlHelper.class);

    public static String getInternalTableName(ResultSetMetaData meta) throws SQLException {
        int columnsCount = meta.getColumnCount();
        StringBuilder tableName = new StringBuilder();
        Set<String> tables = new HashSet<String>();
        for (int i = 1; i <= columnsCount; i++) {
            tables.add(meta.getTableName(i));
        }
        for (String table : tables) {
            tableName.append(table);
            tableName.append("_");
        }
        tableName.setLength(tableName.length() - 1);

        return tableName.toString();
    }
    
    public static String getSourceTableName(String selectQuery) throws SQLException {
        String query = selectQuery.replaceAll(";", "");
        String[] tokens = query.split("\\s");
        StringBuilder tableName = new StringBuilder();
        Set<String> tables = new HashSet<String>();
        for (int i=1; i<tokens.length; i++) {
            if (tokens[i-1].equalsIgnoreCase("FROM") || tokens[i-1].equalsIgnoreCase("JOIN")) {
                tables.add(tokens[i]);
            }
        }
        
        for (String table : tables) {
            tableName.append(table);
            tableName.append("_");
        }
        tableName.setLength(tableName.length() - 1);
        
        return tableName.toString();
    }

    public static void tryCloseDbResources(Connection conn, Statement stmnt, ResultSet rs) {
        tryCloseResultSet(rs);
        tryCloseStatement(stmnt);
        tryCloseConnection(conn);
    }

    public static void tryRollbackConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                LOG.warn("Error occurred during rollback of connection", e);
            }
        }
    }

    public static void tryCloseStatement(Statement stmnt) {
        try {
            if (stmnt != null) {
                stmnt.close();
            }
        } catch (SQLException e) {
            LOG.warn("Error occurred during closing statement", e);
        }
    }

    public static void tryCloseResultSet(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            LOG.warn("Error occurred during closing result set", e);
        }
    }

    public static void tryCloseConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOG.warn("Error occurred during closing result set", e);
            }
        }
    }
    
    public static boolean testDatabaseConnection(RelationalFromSqlConfig_V1 config) {
        boolean bConnectResult = true;
        
        try {
            Class.forName(config.getJdbcDriverName());
        } catch (ClassNotFoundException e) {
            LOG.error("Failed to load driver for the database", e);
            bConnectResult = false;
            return bConnectResult;
        }
        
        Connection conn = null;
        try {
            conn = createConnection(config);
        } catch (Exception e) {
            LOG.error("Failed to establish connection to the database", e);
            bConnectResult = false;
        } finally {
            tryCloseConnection(conn);
        }
        
        return bConnectResult;
    }
    
    public static Connection createConnection(RelationalFromSqlConfig_V1 config) throws SQLException {
        Connection connection = null;
        final Properties connectionProperties = new Properties();
        connectionProperties.setProperty("user", config.getUserName());
        connectionProperties.setProperty("password", config.getUserPassword());
        // TODO: Do we need to validate client / server certificates?
        if (config.isUseSSL()) {
            connectionProperties.setProperty("ssl", "true");
            // TODO: SSL support for other databases generically
            connectionProperties.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
        }
        connection = DriverManager.getConnection(config.getDatabaseURL(), connectionProperties);
        connection.setAutoCommit(false);

        return connection;
    }

    

}
