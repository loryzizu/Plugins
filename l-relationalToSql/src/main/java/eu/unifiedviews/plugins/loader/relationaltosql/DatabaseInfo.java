package eu.unifiedviews.plugins.loader.relationaltosql;

public class DatabaseInfo {

    private String databaseName;

    private String jdbcDriverName;

    private String jdbcPrefix;

    private int defaultPort;

    private DatabaseConfig.DatabaseType databaseType;

    public DatabaseInfo(String databaseName, String jdbcDriverName, String jdbcPrefix, int defaultPort, DatabaseConfig.DatabaseType databaseType) {
        this.databaseName = databaseName;
        this.jdbcDriverName = jdbcDriverName;
        this.jdbcPrefix = jdbcPrefix;
        this.defaultPort = defaultPort;
        this.databaseType = databaseType;
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public String getJdbcDriverName() {
        return this.jdbcDriverName;
    }

    public String getJdbcPrefix() {
        return this.jdbcPrefix;
    }

    public int getDefaultPort() {
        return this.defaultPort;
    }

    public DatabaseConfig.DatabaseType getDatabaseType() {
        return this.databaseType;
    }

    @Override
    public String toString() {
        return this.databaseName;
    }

}
