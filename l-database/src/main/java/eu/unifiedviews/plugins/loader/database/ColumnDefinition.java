package eu.unifiedviews.plugins.loader.database;

public class ColumnDefinition {

    private String columnName;

    private String columnTypeName;

    private int columnType;

    private Integer columnSize;

    public ColumnDefinition(String columnName, String columnTypeName, int columnType, Integer columnSize) {
        this.columnName = columnName;
        this.columnTypeName = columnTypeName;
        this.columnType = columnType;
        this.columnSize = columnSize;
    }

    public ColumnDefinition(String columnName, String columnTypeName, int columnType) {
        this(columnName, columnTypeName, columnType, null);
    }

    public String getColumnName() {
        return this.columnName;
    }

    public String getColumnTypeName() {
        return this.columnTypeName;
    }

    public Integer getColumnSize() {
        return this.columnSize;
    }

    public int getColumnType() {
        return this.columnType;
    }

}
