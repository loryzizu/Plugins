package eu.unifiedviews.plugins.extractor.relationalfromsql;

import java.util.List;

public class RelationalFromSqlConfig_V1 {

    private String databaseURL;

    private String userName;

    private String userPassword;

    private boolean useSSL;

    private String sqlQuery;

    private String targetTableName;

    private List<String> primaryKeyColumns;

    // TODO: This should be generic. Now for test purposes, only Postgres is used
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

    public String getJdbcDriverName() {
        return this.jdbcDriverName;
    }

    public void setJdbcDriverName(String jdbcDriverName) {
        this.jdbcDriverName = jdbcDriverName;
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

}
