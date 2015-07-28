package eu.unifiedviews.plugins.loader.relationaltosql;

public class RelationalToSqlConfig_V1 {
    
    private String databaseURL = " jdbc:postgresql://localhost:5432/test";

    private String userName = "test";

    private String userPassword = "test";

    private String tableNamePrefix = "_prefix";

    private boolean dropTargetTable = false;

    private boolean clearTargetTable = true;

    private boolean useSSL = false;

    //TODO: For now only PostgreSQL is supported. Maybe database type can be user defined
    private String jdbcDriverName = "org.postgresql.Driver";

    public String getDatabaseURL() {
        return this.databaseURL;
    }

    public void setDatabaseURL(String databaseURL) {
        this.databaseURL = databaseURL;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return this.userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getTableNamePrefix() {
        return this.tableNamePrefix;
    }

    public void setTableNamePrefix(String tableName) {
        this.tableNamePrefix = tableName;
    }

    public boolean isDropTargetTable() {
        return this.dropTargetTable;
    }

    public void setDropTargetTable(boolean dropTargetTable) {
        this.dropTargetTable = dropTargetTable;
    }

    public boolean isClearTargetTable() {
        return this.clearTargetTable;
    }

    public void setClearTargetTable(boolean clearTargetTable) {
        this.clearTargetTable = clearTargetTable;
    }

    public boolean isUseSSL() {
        return this.useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public String getJDBCDriverName() {
        return this.jdbcDriverName;
    }

}
