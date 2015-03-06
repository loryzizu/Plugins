package eu.unifiedviews.plugins.extractor.relationalfromsql;

public class DatabaseTable {

    private String tableName;

    private String tableSchema;

    public DatabaseTable(String tableName, String tableSchema) {
        this.tableName = tableName;
        this.tableSchema = tableSchema;
    }

    public DatabaseTable(String tableName) {
        this(tableName, null);
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableSchema() {
        return this.tableSchema;
    }

    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    public String toString() {
        if (this.tableSchema != null) {
            return getTableSchema() + "." + getTableName();
        } else {
            return getTableName();
        }
    }

}
