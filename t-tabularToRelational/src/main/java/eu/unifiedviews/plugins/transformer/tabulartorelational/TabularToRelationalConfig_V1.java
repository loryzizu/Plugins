package eu.unifiedviews.plugins.transformer.tabulartorelational;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.VersionedConfig;
import eu.unifiedviews.plugins.transformer.tabulartorelational.model.ColumnMappingEntry;
import eu.unifiedviews.plugins.transformer.tabulartorelational.model.ParserType;

import java.util.ArrayList;
import java.util.List;

/**
 * Class holds configuration of DPU.
 */
public class TabularToRelationalConfig_V1 implements VersionedConfig<TabularToRelationalConfig_V2> {

    private String tableName;

    private String encoding;

    private Integer rowsLimit;

    private String fieldDelimiter;

    private String fieldSeparator;

    private String xlsSheetName;

    private boolean hasHeader;

    private boolean autogenerateId;

    private ParserType parserType;

    private List<ColumnMappingEntry> columnMapping;

    public TabularToRelationalConfig_V1() {
        // default values
        this.tableName = "";
        this.encoding = "UTF-8";
        this.rowsLimit = 10000;
        this.fieldDelimiter = "\"";
        this.fieldSeparator = ",";
        this.columnMapping = new ArrayList<>();
        this.parserType = ParserType.CSV;
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

    public String getXlsSheetName() {
        return xlsSheetName;
    }

    public void setXlsSheetName(String xlsSheetName) {
        this.xlsSheetName = xlsSheetName;
    }

    public boolean isHasHeader() {
        return hasHeader;
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    public boolean isAutogenerateId() {
        return autogenerateId;
    }

    public void setAutogenerateId(boolean autogenerateId) {
        this.autogenerateId = autogenerateId;
    }

    public ParserType getParserType() {
        return parserType;
    }

    public void setParserType(ParserType parserType) {
        this.parserType = parserType;
    }

    @Override public String toString() {
        return "TabularToRelationalConfig_V1{" +
                "tableName='" + tableName + '\'' +
                ", encoding='" + encoding + '\'' +
                ", rowsLimit=" + rowsLimit +
                ", fieldDelimiter='" + fieldDelimiter + '\'' +
                ", fieldSeparator='" + fieldSeparator + '\'' +
                ", xlsSheetName='" + xlsSheetName + '\'' +
                ", hasHeader=" + hasHeader +
                ", autogenerateId=" + autogenerateId +
                ", parserType=" + parserType +
                ", columnMapping=" + columnMapping +
                '}';
    }

    @Override public TabularToRelationalConfig_V2 toNextVersion() throws DPUConfigException {
        TabularToRelationalConfig_V2 v2 = new TabularToRelationalConfig_V2();
        v2.setTableName(this.tableName);
        v2.setParserType(this.parserType);
        v2.setColumnMapping(this.columnMapping);
        v2.setEncoding(this.encoding);
        v2.setFieldDelimiter(this.fieldDelimiter);
        v2.setFieldSeparator(this.fieldSeparator);
        if(hasHeader) {
            v2.setDataBegginningRow(2);
        } else {
            v2.setDataBegginningRow(1);
        }
        return v2;
    }
}
