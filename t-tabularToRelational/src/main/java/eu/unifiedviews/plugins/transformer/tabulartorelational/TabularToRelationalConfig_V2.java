package eu.unifiedviews.plugins.transformer.tabulartorelational;

import java.util.ArrayList;
import java.util.List;

import eu.unifiedviews.plugins.transformer.tabulartorelational.model.ColumnMappingEntry;
import eu.unifiedviews.plugins.transformer.tabulartorelational.model.ParserType;

/**
 * Class holds configuration of DPU.
 */
public class TabularToRelationalConfig_V2 {

    private String tableName;

    private String encoding;

    private String fieldDelimiter;

    private String fieldSeparator;

    private ParserType parserType;

    private List<ColumnMappingEntry> columnMapping;

    private Integer dataBegginningRow;

    private boolean processOnlyValidCsv;

    public TabularToRelationalConfig_V2() {
        // default values
        this.tableName = "";
        this.encoding = "UTF-8";
        this.fieldDelimiter = "\"";
        this.fieldSeparator = ",";
        this.columnMapping = new ArrayList<>();
        this.parserType = ParserType.CSV;
        this.dataBegginningRow = 1;
        this.processOnlyValidCsv = false;
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

    public ParserType getParserType() {
        return parserType;
    }

    public void setParserType(ParserType parserType) {
        this.parserType = parserType;
    }

    public List<ColumnMappingEntry> getColumnMapping() {
        return columnMapping;
    }

    public void setColumnMapping(List<ColumnMappingEntry> columnMapping) {
        this.columnMapping = columnMapping;
    }

    public Integer getDataBegginningRow() {
        return dataBegginningRow;
    }

    public void setDataBegginningRow(Integer dataBegginningRow) {
        this.dataBegginningRow = dataBegginningRow;
    }

    public boolean isProcessOnlyValidCsv() {
        return this.processOnlyValidCsv;
    }

    public void setProcessOnlyValidCsv(boolean processOnlyValidCsv) {
        this.processOnlyValidCsv = processOnlyValidCsv;
    }

    @Override
    public String toString() {
        return "TabularToRelationalConfig_V2{" +
                "tableName='" + tableName + '\'' +
                ", encoding='" + encoding + '\'' +
                ", fieldDelimiter='" + fieldDelimiter + '\'' +
                ", fieldSeparator='" + fieldSeparator + '\'' +
                ", parserType=" + parserType +
                ", columnMapping=" + columnMapping +
                ", dataBegginningRow=" + dataBegginningRow +
                '}';
    }
}
