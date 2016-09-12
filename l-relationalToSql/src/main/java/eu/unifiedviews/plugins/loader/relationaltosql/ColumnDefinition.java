package eu.unifiedviews.plugins.loader.relationaltosql;

public class ColumnDefinition {

    private String columnName = "";

    private String columnType = "";

    private boolean columnNotNull = false;

    private int columnSize = 0;

    public ColumnDefinition(String columnName, String columnType, boolean columnNotNull, int columnSize) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnNotNull = columnNotNull;
        this.columnSize = columnSize;
    }

    public ColumnDefinition() {
    }

    public ColumnDefinition(ColumnDefinition columnDefinition) {
        this.columnName = columnDefinition.getColumnName();
        this.columnType = columnDefinition.getColumnType();
        this.columnNotNull = columnDefinition.isColumnNotNull();
        this.columnSize = columnDefinition.getColumnSize();
    }

    public String getColumnName() {
        return this.columnName;
    }

    public String getColumnType() {
        return this.columnType;
    }

    public int getColumnSize() {
        return this.columnSize;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    public boolean isColumnNotNull() {
        return columnNotNull;
    }

    public void setColumnNotNull(boolean columnNotNull) {
        this.columnNotNull = columnNotNull;
    }
}
