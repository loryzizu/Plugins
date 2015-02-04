package eu.unifiedviews.plugins.loader.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseHelper.class);

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

    public static boolean testDatabaseConnection(DatabaseConfig_V1 config) {
        boolean bConnectResult = true;

        try {
            Class.forName(config.getJDBCDriverName());
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

    public static Connection createConnection(DatabaseConfig_V1 config) throws SQLException {
        Connection connection = null;
        final Properties connectionProperties = new Properties();
        connectionProperties.setProperty("user", config.getUserName());
        connectionProperties.setProperty("password", config.getUserPassword());
        // TODO: Do we need to validate client / server certificates?
        // TODO: non validating for other databases
        if (config.isUseSSL()) {
            connectionProperties.setProperty("ssl", "true");
            connectionProperties.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
        }
        connection = DriverManager.getConnection(config.getDatabaseURL(), connectionProperties);
        connection.setAutoCommit(false);

        return connection;
    }

}
