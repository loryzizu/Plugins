package cz.cuni.mff.xrg.uv.transformer.tabular;

import cz.cuni.mff.xrg.uv.transformer.tabular.column.ColumnInfo_V1;
import cz.cuni.mff.xrg.uv.transformer.tabular.column.ValueGeneratorReplace;
import cz.cuni.mff.xrg.uv.transformer.tabular.mapper.TableToRdfConfig;
import cz.cuni.mff.xrg.uv.transformer.tabular.parser.ParserCsvConfig;
import cz.cuni.mff.xrg.uv.transformer.tabular.parser.ParserDbfConfig;
import cz.cuni.mff.xrg.uv.transformer.tabular.parser.ParserType;
import cz.cuni.mff.xrg.uv.transformer.tabular.parser.ParserXlsConfig;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.xrg.uv.transformer.tabular.column.NamedCell_V1;

/**
 *
 * @author Škoda Petr
 */
public class TabularConfig_V2 {

    public static class AdvanceMapping {
        
        private String uri = "";
        
        private String template = "";

        public AdvanceMapping() {
        }

        public AdvanceMapping(String uri, String template) {
            this.uri = uri;
            this.template = template;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getTemplate() {
            return template;
        }

        public void setTemplate(String template) {
            this.template = template;
        }
        
    }
    
    /**
     * Name of column that will be used as a key. If null then first column
     * is used. Can also contains template for constriction of primary subject.
     */
    private String keyColumn = null;

    /**
     * Base URI that is used to prefix generated URIs.
     */
    private String baseURI = "http://localhost";

    /**
     * Column mapping simple.
     */
    private Map<String, ColumnInfo_V1> columnsInfo = new LinkedHashMap<>();

    /**
     * Advanced column mapping using string templates directly. Based on
     * http://w3c.github.io/csvw/csv2rdf/#
     *
     * If { or } should be used then they can be escaped like: \{ and \}
     * this functionality is secured by
     * {@link ValueGeneratorReplace#compile}
     */
    private List<AdvanceMapping> columnsInfoAdv = new LinkedList<>();

    /**
     * Named cells for xls.
     */
    private List<NamedCell_V1> namedCells = new LinkedList<>();

    private String quoteChar = "\"";

    private String delimiterChar = ",";

    private Integer linesToIgnore = 0;

    private String encoding = "UTF-8";

    private Integer rowsLimit = null;

    private ParserType tableType = ParserType.CSV;

    private boolean hasHeader = true;

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
     * If true then {@link #keyColumn} is interpreted as advanced = tempalte.
     */
    private boolean advancedKeyColumn = false;

    /**
     * If null no class is set.
     */
    private String rowsClass = TabularOntology.ROW_CLASS.toString();

    /**
     * Sheet name.
     */
    private String xlsSheetName = null;

    /**
     * If checked same row counter is used for all files. Used only for xsls.
     */
    private boolean staticRowCounter = false;

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

    /**
     * If set then trailing null values in header are ignored.
     */
    private boolean stripHeader = false;

    /**
     * If true then string values are trimmed before used.
     *
     * WARNING: This field is in fact used not only for dbf, but in global scope.
     */
    private boolean dbfTrimString = false;

    private boolean xlsAdvancedDoubleParser = false;

    /**
     * If true only info log instead of error in case of missing named column.
     */
    private boolean ignoreMissingColumn = false;

    public TabularConfig_V2() {
    }

    public String getKeyColumn() {
        return keyColumn;
    }

    public void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
    }

    public String getBaseURI() {
        return baseURI;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public Map<String, ColumnInfo_V1> getColumnsInfo() {
        return columnsInfo;
    }

    public void setColumnsInfo(Map<String, ColumnInfo_V1> columnsInfo) {
        this.columnsInfo = columnsInfo;
    }

    public String getQuoteChar() {
        return quoteChar;
    }

    public void setQuoteChar(String quoteChar) {
        this.quoteChar = quoteChar;
    }

    public String getDelimiterChar() {
        return delimiterChar;
    }

    public void setDelimiterChar(String delimiterChar) {
        this.delimiterChar = delimiterChar;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Integer getLinesToIgnore() {
        return linesToIgnore;
    }

    public void setLinesToIgnore(Integer numberOfStartLinesToIgnore) {
        this.linesToIgnore = numberOfStartLinesToIgnore;
    }

    public Integer getRowsLimit() {
        return rowsLimit;
    }

    public void setRowsLimit(Integer rowLimit) {
        this.rowsLimit = rowLimit;
    }

    public ParserType getTableType() {
        return tableType;
    }

    public void setTableType(ParserType tabelType) {
        this.tableType = tabelType;
    }

    public boolean isHasHeader() {
        return hasHeader;
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    public boolean isGenerateNew() {
        return generateNew;
    }

    public void setGenerateNew(boolean generateNew) {
        this.generateNew = generateNew;
    }

    public List<AdvanceMapping> getColumnsInfoAdv() {
        return columnsInfoAdv;
    }

    public void setColumnsInfoAdv(List<AdvanceMapping> columnsInfoAdv) {
        this.columnsInfoAdv = columnsInfoAdv;
    }

    public List<NamedCell_V1> getNamedCells() {
        return namedCells;
    }

    public void setNamedCells(List<NamedCell_V1> namedCells) {
        this.namedCells = namedCells;
    }

    public String getRowsClass() {
        return rowsClass;
    }

    public void setRowsClass(String columnClass) {
        this.rowsClass = columnClass;
    }

    public String getXlsSheetName() {
        return xlsSheetName;
    }

    public void setXlsSheetName(String xlsSheetName) {
        this.xlsSheetName = xlsSheetName;
    }

    public boolean isIgnoreBlankCells() {
        return ignoreBlankCells;
    }

    public void setIgnoreBlankCells(boolean ignoreBlankCells) {
        this.ignoreBlankCells = ignoreBlankCells;
    }

    public boolean isAdvancedKeyColumn() {
        return advancedKeyColumn;
    }

    public void setAdvancedKeyColumn(boolean advancedKeyColumn) {
        this.advancedKeyColumn = advancedKeyColumn;
    }
    
    public boolean isStaticRowCounter() {
        return staticRowCounter;
    }

    public void setStaticRowCounter(boolean staticRowCounter) {
        this.staticRowCounter = staticRowCounter;
    }

    public boolean isGenerateRowTriple() {
        return generateRowTriple;
    }

    public void setGenerateRowTriple(boolean generateRowTriple) {
        this.generateRowTriple = generateRowTriple;
    }

    public boolean isUseTableSubject() {
        return useTableSubject;
    }

    public void setUseTableSubject(boolean useTableSubject) {
        this.useTableSubject = useTableSubject;
    }

    public Boolean isAutoAsStrings() {
        return autoAsStrings;
    }

    public void setAutoAsStrings(Boolean autoAsStrings) {
        this.autoAsStrings = autoAsStrings;
    }

    public boolean isGenerateTableClass() {
        return generateTableClass;
    }

    public void setGenerateTableClass(boolean tableRowClass) {
        this.generateTableClass = tableRowClass;
    }

    public boolean isGenerateLabels() {
        return generateLabels;
    }

    public void setGenerateLabels(boolean generateLabels) {
        this.generateLabels = generateLabels;
    }

    public boolean isStripHeader() {
        return stripHeader;
    }

    public void setStripHeader(boolean stripHeader) {
        this.stripHeader = stripHeader;
    }

    public boolean isDbfTrimString() {
        return dbfTrimString;
    }

    public void setDbfTrimString(boolean dbfTrimString) {
        this.dbfTrimString = dbfTrimString;
    }

    public boolean isXlsAdvancedDoubleParser() {
        return xlsAdvancedDoubleParser;
    }

    public void setXlsAdvancedDoubleParser(boolean xlsAdvancedDoubleParser) {
        this.xlsAdvancedDoubleParser = xlsAdvancedDoubleParser;
    }

    public boolean isIgnoreMissingColumn() {
        return ignoreMissingColumn;
    }

    public void setIgnoreMissingColumn(boolean ignoreMissingColumn) {
        this.ignoreMissingColumn = ignoreMissingColumn;
    }

    public TableToRdfConfig getTableToRdfConfig() {
        return new TableToRdfConfig(keyColumn, baseURI, columnsInfo,
                generateNew, rowsClass, ignoreBlankCells, columnsInfoAdv,
                advancedKeyColumn, generateRowTriple, autoAsStrings, 
                generateTableClass, generateLabels, dbfTrimString, ignoreMissingColumn);
    }

    public ParserCsvConfig getParserCsvConfig() {
        return new ParserCsvConfig(quoteChar, delimiterChar,
                encoding, linesToIgnore,
                rowsLimit == null || rowsLimit == -1 ? null : rowsLimit,
                hasHeader, staticRowCounter);
    }

    public ParserDbfConfig getParserDbfConfig() {
        return new ParserDbfConfig(encoding,
                rowsLimit == null || rowsLimit == -1 ? null : rowsLimit,
                staticRowCounter);
    }

    public ParserXlsConfig getParserXlsConfig() {
        return new ParserXlsConfig(xlsSheetName, linesToIgnore, hasHeader,
                namedCells, 
                rowsLimit == null || rowsLimit == -1 ? null : rowsLimit,
                staticRowCounter, stripHeader, xlsAdvancedDoubleParser);
    }

}
