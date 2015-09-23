package eu.unifiedviews.plugins.transformer.relationaltordf;

import java.util.LinkedHashMap;
import java.util.Map;

import eu.unifiedviews.plugins.transformer.relationaltordf.column.ColumnInfo_V1;
import eu.unifiedviews.plugins.transformer.relationaltordf.mapper.TableToRdfConfig;

public class RelationalToRdfConfig_V1 {

    /**
     * Name of column that will be used as a key. If null then first column
     * is used. Can also contains template for construction of primary subject.
     */
    private String keyColumn = null;

    /**
     * Base URI that is used to prefix generated URIs.
     */
    private String baseURI = "http://localhost/";

    /**
     * Column mapping simple.
     */
    private Map<String, ColumnInfo_V1> columnsInfo = new LinkedHashMap<>();

    /**
     * If false only columns from {@link #columnsInfo} are used.
     */
    private boolean generateNew = true;

    /**
     * If false then for blank cells the {@link TabularOntology#URI_BLANK_CELL}
     * is inserted.
     */
    private boolean ignoreBlankCells = false;

    /**
     * If true then {@link #keyColumn} is interpreted as advanced = template.
     */
    private boolean advancedKeyColumn = false;

    /**
     * If null no class is set.
     */
    private String rowsClass = TabularOntology.ROW_CLASS.toString();

    /**
     * If true then triple with row number is generated for each line.
     */
    private boolean generateRowTriple = true;

    private boolean useTableSubject = false;

    /**
     * If checked then type auto is always set to string.
     */
    private boolean autoAsStrings = false;

    /**
     * If true then 'a' predicate with class is generated for table and row entity.
     */
    private boolean generateTableClass = false;

    /**
     * Generate RDF.LABEL for columns from colum name.
     */
    private boolean generateLabels = false;

    public String getKeyColumn() {
        return this.keyColumn;
    }

    public void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
    }

    public String getBaseURI() {
        return this.baseURI;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public Map<String, ColumnInfo_V1> getColumnsInfo() {
        return this.columnsInfo;
    }

    public void setColumnsInfo(Map<String, ColumnInfo_V1> columnsInfo) {
        this.columnsInfo = columnsInfo;
    }

    public boolean isGenerateNew() {
        return this.generateNew;
    }

    public void setGenerateNew(boolean generateNew) {
        this.generateNew = generateNew;
    }

    public String getRowsClass() {
        return this.rowsClass;
    }

    public void setRowsClass(String columnClass) {
        this.rowsClass = columnClass;
    }

    public boolean isIgnoreBlankCells() {
        return this.ignoreBlankCells;
    }

    public void setIgnoreBlankCells(boolean ignoreBlankCells) {
        this.ignoreBlankCells = ignoreBlankCells;
    }

    public boolean isAdvancedKeyColumn() {
        return this.advancedKeyColumn;
    }

    public void setAdvancedKeyColumn(boolean advancedKeyColumn) {
        this.advancedKeyColumn = advancedKeyColumn;
    }

    public boolean isGenerateRowTriple() {
        return this.generateRowTriple;
    }

    public void setGenerateRowTriple(boolean generateRowTriple) {
        this.generateRowTriple = generateRowTriple;
    }

    public boolean isUseTableSubject() {
        return this.useTableSubject;
    }

    public void setUseTableSubject(boolean useTableSubject) {
        this.useTableSubject = useTableSubject;
    }

    public Boolean isAutoAsStrings() {
        return this.autoAsStrings;
    }

    public void setAutoAsStrings(Boolean autoAsStrings) {
        this.autoAsStrings = autoAsStrings;
    }

    public boolean isGenerateTableClass() {
        return this.generateTableClass;
    }

    public void setGenerateTableClass(boolean tableRowClass) {
        this.generateTableClass = tableRowClass;
    }

    public boolean isGenerateLabels() {
        return this.generateLabels;
    }

    public void setGenerateLabels(boolean generateLabels) {
        this.generateLabels = generateLabels;
    }

    public TableToRdfConfig getTableToRdfConfig() {
        return new TableToRdfConfig(this.keyColumn, this.baseURI, this.columnsInfo,
                this.generateNew, this.rowsClass, this.ignoreBlankCells,
                this.advancedKeyColumn, this.generateRowTriple, this.autoAsStrings,
                this.generateTableClass, this.generateLabels);
    }

}
