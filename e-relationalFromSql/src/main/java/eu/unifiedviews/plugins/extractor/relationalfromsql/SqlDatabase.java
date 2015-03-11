package eu.unifiedviews.plugins.extractor.relationalfromsql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class contains database engines specific configuration parameters
 */
public class SqlDatabase {

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
    private static final String MSSQL_NAME = "MS SQL";

    private static final String MSSQL_JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    private static final String MSSQL_JDBC_URL_PREFIX = "jdbc:sqlserver://";

    private static final int MSSQL_DEFAULT_PORT = 1433;

    // Oracle parameters
    public static final String ORACLE_URL = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=%s)(HOST=%s)(PORT=%s))(CONNECT_DATA=(SERVICE_NAME=%s)))";

    private static final String ORACLE_NAME = "ORACLE";

    private static final String ORACLE_JDBC_DRIVER = "oracle.jdbc.OracleDriver";

    private static final String ORACLE_JDBC_URL_PREFIX = "jdbc:oracle:thin:@";

    private static final int ORACLE_DEFAULT_PORT = 1521;

    // H2 Parameters - used for testing purposes
    private static final String H2_MEM_JDBC_DRIVER = "org.h2.Driver";

    private static final String H2_MEM_JDBC_URL_PREFIX = "jdbc:h2:";

    public enum DatabaseType {
        POSTGRES, MSSQL, MYSQL, ORACLE, H2_MEM;
    }

    private static Map<String, DatabaseType> namesToType;

    private static Map<DatabaseType, String> typesToName;

    private static Map<DatabaseType, String> jdbcDriverNames;

    private static Map<DatabaseType, String> jdbcUrlPrefixes;

    private static Map<DatabaseType, Integer> defaultDatabasePorts;

    private static List<String> supportedDatabaseNames;

    static {
        initNamesToType();
        initJdbcDriverNames();
        initJdbcUrlPrefixes();
        initDefaultDatabasePorts();
        initTypesToName();
        initSupportedDatabaseNames();
    }

    private static void initNamesToType() {
        namesToType = new HashMap<>();
        namesToType.put(POSTGRES_NAME, DatabaseType.POSTGRES);
        namesToType.put(MYSQL_NAME, DatabaseType.MYSQL);
        namesToType.put(MSSQL_NAME, DatabaseType.MSSQL);
        namesToType.put(ORACLE_NAME, DatabaseType.ORACLE);
    }

    private static void initTypesToName() {
        typesToName = new HashMap<>();
        typesToName.put(DatabaseType.POSTGRES, POSTGRES_NAME);
        typesToName.put(DatabaseType.MYSQL, MYSQL_NAME);
        typesToName.put(DatabaseType.MSSQL, MSSQL_NAME);
        typesToName.put(DatabaseType.ORACLE, ORACLE_NAME);
    }

    private static void initJdbcDriverNames() {
        jdbcDriverNames = new HashMap<>();
        jdbcDriverNames.put(DatabaseType.POSTGRES, POSTGRES_JDBC_DRIVER);
        jdbcDriverNames.put(DatabaseType.MYSQL, MYSQL_JDBC_DRIVER);
        jdbcDriverNames.put(DatabaseType.ORACLE, ORACLE_JDBC_DRIVER);
        jdbcDriverNames.put(DatabaseType.MSSQL, MSSQL_JDBC_DRIVER);
        jdbcDriverNames.put(DatabaseType.H2_MEM, H2_MEM_JDBC_DRIVER);
    }

    private static void initJdbcUrlPrefixes() {
        jdbcUrlPrefixes = new HashMap<>();
        jdbcUrlPrefixes.put(DatabaseType.POSTGRES, POSTGRES_JDBC_URL_PREFIX);
        jdbcUrlPrefixes.put(DatabaseType.MYSQL, MYSQL_JDBC_URL_PREFIX);
        jdbcUrlPrefixes.put(DatabaseType.ORACLE, ORACLE_JDBC_URL_PREFIX);
        jdbcUrlPrefixes.put(DatabaseType.MSSQL, MSSQL_JDBC_URL_PREFIX);
        jdbcUrlPrefixes.put(DatabaseType.H2_MEM, H2_MEM_JDBC_URL_PREFIX);
    }

    private static void initDefaultDatabasePorts() {
        defaultDatabasePorts = new HashMap<>();
        defaultDatabasePorts.put(DatabaseType.POSTGRES, POSTGRES_DEFAULT_PORT);
        defaultDatabasePorts.put(DatabaseType.MYSQL, MYSQL_DEFAULT_PORT);
        defaultDatabasePorts.put(DatabaseType.MSSQL, MSSQL_DEFAULT_PORT);
        defaultDatabasePorts.put(DatabaseType.ORACLE, ORACLE_DEFAULT_PORT);
    }

    private static void initSupportedDatabaseNames() {
        supportedDatabaseNames = new ArrayList<>();
        supportedDatabaseNames.add(POSTGRES_NAME);
        supportedDatabaseNames.add(MYSQL_NAME);
        supportedDatabaseNames.add(MSSQL_NAME);
        supportedDatabaseNames.add(ORACLE_NAME);
    }

    public static DatabaseType getDatabaseTypeForDatabaseName(String dbName) {
        return namesToType.get(dbName);
    }

    public static String getJdbcDriverNameForDatabase(DatabaseType dbType) {
        return jdbcDriverNames.get(dbType);
    }

    public static String getJdbcUrlPrefixForDatabase(DatabaseType dbType) {
        return jdbcUrlPrefixes.get(dbType);
    }

    public static Set<String> getDatabaseTypeNames() {
        return namesToType.keySet();
    }

    public static int getDefaultDatabasePort(DatabaseType dbType) {
        return defaultDatabasePorts.get(dbType);
    }

    public static String getDatabaseNameForDatabaseType(DatabaseType dbType) {
        return typesToName.get(dbType);
    }

}
