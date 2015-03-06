package eu.unifiedviews.plugins.extractor.relationalfromsql;

import java.util.List;

import eu.unifiedviews.plugins.extractor.relationalfromsql.SqlDatabase.DatabaseType;

public class RelationalFromSqlConfig_V2 {

    private String databaseHost;

    private int databasePort;

    private String databaseName;

    private DatabaseType databaseType;

    private String userName;

    private String userPassword;

    private boolean useSSL;

    private String sqlQuery;

    private String targetTableName;

    private List<String> primaryKeyColumns;

    private String instanceName;

    public String getDatabaseHost() {
        return this.databaseHost;
    }

    public void setDatabaseHost(String databaseHost) {
        this.databaseHost = databaseHost;
    }

    public int getDatabasePort() {
        return this.databasePort;
    }

    public void setDatabasePort(int databasePort) {
        this.databasePort = databasePort;
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public DatabaseType getDatabaseType() {
        return this.databaseType;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
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

    public boolean isUseSSL() {
        return this.useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public String getSqlQuery() {
        return this.sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public String getTargetTableName() {
        return this.targetTableName;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    public List<String> getPrimaryKeyColumns() {
        return this.primaryKeyColumns;
    }

    public void setPrimaryKeyColumns(List<String> primaryKeyColumns) {
        this.primaryKeyColumns = primaryKeyColumns;
    }

    public String getInstanceName() {
        return this.instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

}
