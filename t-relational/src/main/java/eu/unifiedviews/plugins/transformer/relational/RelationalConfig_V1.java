package eu.unifiedviews.plugins.transformer.relational;

public class RelationalConfig_V1 {

    private String targetTableName;

    private String sqlQuery;

    public String getTargetTableName() {
        return this.targetTableName;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    public String getSqlQuery() {
        return this.sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

}
