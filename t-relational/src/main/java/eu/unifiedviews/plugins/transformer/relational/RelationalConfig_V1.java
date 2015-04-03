package eu.unifiedviews.plugins.transformer.relational;

import java.util.List;

public class RelationalConfig_V1 {

    private String targetTableName;

    private String sqlQuery;

    private List<String> primaryKeyColumns;

    private List<String> indexedColumns;

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

    public List<String> getPrimaryKeyColumns() {
        return this.primaryKeyColumns;
    }

    public void setPrimaryKeyColumns(List<String> primaryKeyColumns) {
        this.primaryKeyColumns = primaryKeyColumns;
    }

    public List<String> getIndexedColumns() {
        return this.indexedColumns;
    }

    public void setIndexedColumns(List<String> indexedColumns) {
        this.indexedColumns = indexedColumns;
    }

}
