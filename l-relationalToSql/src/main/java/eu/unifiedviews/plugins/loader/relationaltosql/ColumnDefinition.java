package eu.unifiedviews.plugins.loader.relationaltosql;

public class ColumnDefinition {

    private String columnName;

    private SqlDatatype columnType;

    private boolean columnNotNull;

    private int columnSize;

    public ColumnDefinition(String columnName, SqlDatatype columnType, boolean columnNotNull, int columnSize) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnNotNull = columnNotNull;
        this.columnSize = columnSize;
    }

    public ColumnDefinition(String columnName, SqlDatatype columnType, boolean columnNotNull) {
        this(columnName, columnType, columnNotNull, -1);
    }

    public String getColumnName() {
        return this.columnName;
    }

    public SqlDatatype getColumnType() {
        return this.columnType;
    }

    public boolean isNotNull() {
        return this.columnNotNull;
    }

    public int getColumnSize() {
        return this.columnSize;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ColumnDefinition)) {
            return false;
        }
        ColumnDefinition cd = (ColumnDefinition) o;
        return (this.columnName.equals(cd.getColumnName()) && this.columnType == cd.getColumnType()) ;
    }

}
