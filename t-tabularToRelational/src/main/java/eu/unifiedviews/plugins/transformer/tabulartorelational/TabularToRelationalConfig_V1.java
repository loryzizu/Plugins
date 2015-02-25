package eu.unifiedviews.plugins.transformer.tabulartorelational;

import java.util.List;

public class TabularToRelationalConfig_V1 {

    private String tableName = ""; // DEFAULT VALUE

    private String encoding = "UTF-8"; // DEFAULT VALUE

    private Integer rowsLimit = 10000; // DEFAULT VALUE

    private String fieldDelimiter = "\""; // DEFAULT VALUE

    private String fieldSeparator = ","; // DEFAULT VALUE

    private List<ColumnMappingEntry> columnMapping;

    public TabularToRelationalConfig_V1() {
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Integer getRowsLimit() {
        return rowsLimit;
    }

    public void setRowsLimit(Integer rowsLimit) {
        this.rowsLimit = rowsLimit;
    }

    public String getFieldDelimiter() {
        return fieldDelimiter;
    }

    public void setFieldDelimiter(String fieldDelimiter) {
        this.fieldDelimiter = fieldDelimiter;
    }

    public String getFieldSeparator() {
        return fieldSeparator;
    }

    public void setFieldSeparator(String fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }

    public List<ColumnMappingEntry> getColumnMapping() {
        return columnMapping;
    }

    public void setColumnMapping(List<ColumnMappingEntry> columnMapping) {
        this.columnMapping = columnMapping;
    }

    @Override public String toString() {
        return "TabularToRelationalConfig_V1{" +
                "tableName='" + tableName + '\'' +
                ", encoding='" + encoding + '\'' +
                ", rowsLimit=" + rowsLimit +
                ", fieldDelimiter='" + fieldDelimiter + '\'' +
                ", fieldSeparator='" + fieldSeparator + '\'' +
                ", columnMapping=" + columnMapping +
                '}';
    }
}
