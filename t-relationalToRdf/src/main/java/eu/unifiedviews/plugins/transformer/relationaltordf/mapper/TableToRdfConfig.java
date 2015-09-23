package eu.unifiedviews.plugins.transformer.relationaltordf.mapper;

import java.util.HashMap;
import java.util.Map;

import eu.unifiedviews.plugins.transformer.relationaltordf.column.ColumnInfo_V1;

/**
 * See main DPU's configuration for more details about fields meaning.
 */
public class TableToRdfConfig {

    /**
     * Name of column with key, null, or template.
     */
    final String keyColumn;

    /**
     * Base URI used to prefix generated URIs.
     */
    final String baseURI;

    /**
     * User configuration about parsing process.
     */
    final Map<String, ColumnInfo_V1> columnsInfo;

    /**
     * If true then new column, not specified in {@link #columnsInfo},
     * can be added.
     */
    final boolean generateNew;

    /**
     * Metadata for column - type.
     */
    final String rowsClass;

    final boolean ignoreBlankCells;

    final boolean advancedKeyColumn;

    final boolean generateRowTriple;

    final boolean autoAsStrings;

    final boolean generateTableClass;

    final boolean generateLabels;

    public TableToRdfConfig(String keyColumnName, String baseURI,
            Map<String, ColumnInfo_V1> columnsInfo, boolean generateNew,
            String rowsClass, boolean ignoreBlankCells,
            boolean advancedKeyColumn, boolean generateRowTriple, boolean autoAsStrings,
            boolean generateTableRowClass, boolean generateLabels) {
        this.keyColumn = keyColumnName;
        this.baseURI = baseURI;
        this.columnsInfo = columnsInfo != null ? columnsInfo : new HashMap<String, ColumnInfo_V1>();
        this.generateNew = generateNew;
        this.rowsClass = rowsClass;
        this.ignoreBlankCells = ignoreBlankCells;
        this.advancedKeyColumn = advancedKeyColumn;
        this.generateRowTriple = generateRowTriple;
        this.autoAsStrings = autoAsStrings;
        this.generateTableClass = generateTableRowClass;
        this.generateLabels = generateLabels;
    }

}
