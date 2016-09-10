package eu.unifiedviews.plugins.loader.relationaltosql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * This class contains database engines specific configuration parameters
 */
public class DatabaseConfig {

    private static Logger LOG = LoggerFactory.getLogger(DatabaseConfig.class);

    private static String SUPPORTED_DB_PROPERTY_FORMAT = "db.type.%s.enabled";

    // PostgreSQL parameters
    private static final String POSTGRES_NAME = "PostgreSQL";

    private static final String POSTGRES_JDBC_DRIVER = "org.postgresql.Driver";

    private static final String POSTGRES_JDBC_URL_PREFIX = "jdbc:postgresql://";

    private static final int POSTGRES_DEFAULT_PORT = 5432;

    // MySQL parameters
    private static final String MYSQL_NAME = "MySQL";

    private static final String MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";

    private static final String MYSQL_JDBC_URL_PREFIX = "jdbc:mysql://";

    private static final int MYSQL_DEFAULT_PORT = 3306;

    // MS SQL parameters
    private static final String MSSQL_NAME = "MSSQL";

    private static final String MSSQL_JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    private static final String MSSQL_JDBC_URL_PREFIX = "jdbc:sqlserver://";

    private static final int MSSQL_DEFAULT_PORT = 1433;

    // Oracle parameters
    public static final String ORACLE_URL = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=%s)(HOST=%s)(PORT=%s))(CONNECT_DATA=(SERVICE_NAME=%s)))";

    private static final String ORACLE_NAME = "ORACLE";

    private static final String ORACLE_JDBC_DRIVER = "oracle.jdbc.OracleDriver";

    private static final String ORACLE_JDBC_URL_PREFIX = "jdbc:oracle:thin:@";

    private static final int ORACLE_DEFAULT_PORT = 1521;

    public enum DatabaseType {
        POSTGRES, MSSQL, MYSQL, ORACLE, H2_MEM;
    }

    private static DatabaseInfo postgresInfo = new DatabaseInfo(POSTGRES_NAME, POSTGRES_JDBC_DRIVER, POSTGRES_JDBC_URL_PREFIX,
            POSTGRES_DEFAULT_PORT, DatabaseType.POSTGRES);

    private static DatabaseInfo mysqlInfo = new DatabaseInfo(MYSQL_NAME, MYSQL_JDBC_DRIVER, MYSQL_JDBC_URL_PREFIX, MYSQL_DEFAULT_PORT,
            DatabaseType.MYSQL);

    private static DatabaseInfo oracleInfo = new DatabaseInfo(ORACLE_NAME, ORACLE_JDBC_DRIVER, ORACLE_JDBC_URL_PREFIX, ORACLE_DEFAULT_PORT,
            DatabaseType.ORACLE);

    private static DatabaseInfo mssqlInfo = new DatabaseInfo(MSSQL_NAME, MSSQL_JDBC_DRIVER, MSSQL_JDBC_URL_PREFIX, MSSQL_DEFAULT_PORT,
            DatabaseType.MSSQL);

    private static List<DatabaseInfo> allDatabases;

    private static Map<DatabaseType, DatabaseInfo> supportedDatabases;

    static {
        allDatabases = Arrays.asList(new DatabaseInfo[] { postgresInfo, mysqlInfo, oracleInfo, mssqlInfo });
        initSupportedDatabases();
    }

    private static void initSupportedDatabases() {
        supportedDatabases = new HashMap<>();
        Properties dbProperties = getProperties();
        for (DatabaseInfo dbInfo : allDatabases) {
            String dbProperty = String.format(SUPPORTED_DB_PROPERTY_FORMAT, dbInfo.getDatabaseName());
            if (dbProperties.getProperty(dbProperty) != null && Boolean.parseBoolean(dbProperties.getProperty(dbProperty))) {
                supportedDatabases.put(dbInfo.getDatabaseType(), dbInfo);
            }
        }
    }

    public static DatabaseInfo getDatabaseInfo(DatabaseType databaseType) {
        return supportedDatabases.get(databaseType);
    }

    public static Collection<DatabaseInfo> getSupportedDatabases() {
        return supportedDatabases.values();
    }

    public static Map<DatabaseType, DatabaseInfo> getSupportedDatabasesMap() {
        return supportedDatabases;
    }

    private static Properties getProperties() {
        Properties props = new Properties();
        ClassLoader loader = RelationalToSql.class.getClassLoader();
        try (InputStream stream = loader.getResourceAsStream("db.properties")) {
            props.load(stream);
        } catch (IOException e) {
            LOG.error("Failed to load db properties", e);
        }

        return props;

    }

}
