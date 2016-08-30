package eu.unifiedviews.plugins.loader.relationaltosql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import eu.unifiedviews.plugins.loader.relationaltosql.SqlDatabase.DatabaseType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationalToSqlHelper {

    private static final Logger LOG = LoggerFactory.getLogger(RelationalToSqlHelper.class);

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

    public static boolean testDatabaseConnection(RelationalToSqlConfig_V1 config) {
        boolean bConnectResult = true;
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

    private static boolean checkTrustStoreConfigExists(RelationalToSqlConfig_V1 config) {
        boolean bExists = true;
        if (config.getTruststoreLocation() == null || config.getTruststoreLocation().isEmpty()) {
            bExists = false;
        }
        if (config.getTruststorePassword() == null || config.getTruststorePassword().isEmpty()) {
            bExists = false;
        }
        return bExists;
    }

    /**
     * Creates JDBC connection URL based on {@link RelationalToSqlConfig_V1} configuration
     *
     * @param config
     *            Database configuration
     * @return JDBC connection URL
     */
    private static String createDatabaseUrlFromConfig(RelationalToSqlConfig_V1 config) {
        if (config.getDatabaseType() == DatabaseType.ORACLE) {
            String protocol = "tcp";
            if (config.isUseSSL()) {
                protocol = "tcps";
            }
            String url = String.format(SqlDatabase.ORACLE_URL, protocol, config.getDatabaseHost(), config.getDatabasePort(),
                    config.getDatabaseName());
            return url;
        }

        StringBuilder url = new StringBuilder();
        url.append(SqlDatabase.getDatabaseInfo(config.getDatabaseType()).getJdbcPrefix());
        url.append(config.getDatabaseHost());
        if (config.getDatabasePort() != 0) {
            url.append(":");
            url.append(config.getDatabasePort());
        }
        if (config.getDatabaseType() == DatabaseType.H2_MEM) {
            url.append(":");
        } else if (config.getDatabaseType() == DatabaseType.MSSQL) {
            url.append(";databaseName=");
        } else {
            url.append("/");
        }
        url.append(config.getDatabaseName());

        if (config.getDatabaseType() == DatabaseType.MSSQL && config.getInstanceName() != null) {
            url.append(";instanceName=");
            url.append(config.getInstanceName());
        }
        if (config.isUseSSL() && config.getDatabaseType() == DatabaseType.POSTGRES) {
            url.append("?ssl=true");
        }

        return url.toString();
    }

    public static Connection createConnection(RelationalToSqlConfig_V1 config) throws SQLException {
        try {
            Class.forName(SqlDatabase.getDatabaseInfo(config.getDatabaseType()).getJdbcDriverName());
        } catch (ClassNotFoundException e) {
            throw new SQLException("Failed to load JDBC driver", e);
        }

        Connection connection = null;
        String jdbcURL = createDatabaseUrlFromConfig(config);
        final Properties connectionProperties = new Properties();
        connectionProperties.setProperty("user", config.getUserName());
        connectionProperties.setProperty("password", config.getUserPassword());
        boolean bConfigSslTrustStore = false;
        if (config.isUseSSL()) {
            bConfigSslTrustStore = checkTrustStoreConfigExists(config);
            if (bConfigSslTrustStore) {
                LOG.debug("Using custom truststore {} to verify trusted servers", config.getTruststoreLocation());
                System.setProperty("javax.net.ssl.trustStore", config.getTruststoreLocation());
                System.setProperty("javax.net.ssl.trustStorePassword", config.getTruststorePassword());
            } else {
                LOG.warn("Using default JAVA truststore to verify trusted servers. This can cause problems with servers certificates signed by unknown CAs");
            }

            if (config.getDatabaseType() == DatabaseType.MYSQL) {
                connectionProperties.setProperty("useSSL", "true");
                connectionProperties.setProperty("requireSSL", "true");
            } else if (config.getDatabaseType() == DatabaseType.ORACLE) {
                connectionProperties.setProperty("javax.net.ssl.trustStoreType", "JKS");
                connectionProperties.setProperty("oracle.net.authentication_services", "(TCPS)");
                if (bConfigSslTrustStore) {
                    connectionProperties.setProperty("javax.net.ssl.trustStore", "resources/trusted");
                    connectionProperties.setProperty("javax.net.ssl.trustStorePassword", "changeit");
                }
            } else if (config.getDatabaseType() == DatabaseType.MSSQL) {
                connectionProperties.setProperty("integratedSecurity", "true");
                connectionProperties.setProperty("encrypt", "true");
                connectionProperties.setProperty("trustServerCertificate", "false");
                if (bConfigSslTrustStore) {
                    connectionProperties.setProperty("trustStore", "resources/trusted");
                    connectionProperties.setProperty("trustStorePassword", "changeit");
                }
            } else if (config.getDatabaseType() == DatabaseType.POSTGRES && bConfigSslTrustStore) {
                connectionProperties.setProperty("sslfactory",
                        "eu.unifiedviews.plugins.extractor.relationalfromsql.SSLPostgresValidationFactory");
            }
        }
        connection = DriverManager.getConnection(jdbcURL, connectionProperties);
        connection.setAutoCommit(false);

        return connection;
    }

}
