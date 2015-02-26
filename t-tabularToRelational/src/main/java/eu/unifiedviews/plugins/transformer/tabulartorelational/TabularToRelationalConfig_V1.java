package eu.unifiedviews.plugins.transformer.tabulartorelational;

import java.util.ArrayList;
import java.util.List;

public class TabularToRelationalConfig_V1 {

    private String tableName;

    private String encoding;

    private Integer rowsLimit;

    private String fieldDelimiter;

    private String fieldSeparator;

    private List<ColumnMappingEntry> columnMapping;

    public TabularToRelationalConfig_V1() {
        // default values
        this.tableName = "";
        this.encoding = "UTF-8";
        this.rowsLimit = 10000;
        this.fieldDelimiter = "\"";
        this.fieldSeparator = ",";
        this.columnMapping = new ArrayList<>();
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
