package eu.unifiedviews.plugins.transformer.tabulartorelational.parser;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import eu.unifiedviews.helpers.dpu.exec.UserExecContext;
import eu.unifiedviews.plugins.transformer.tabulartorelational.TabularToRelationalConfig_V2;
import eu.unifiedviews.plugins.transformer.tabulartorelational.model.ColumnMappingEntry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * General parser class.
 *
 * Class holds generic functionality and interface of a parser. Each class, that extends from this super class, need to provide concrete implementation of how file is parsed.
 */
public abstract class RelationalParser {

    protected static final Logger LOG = LoggerFactory.getLogger(RelationalParser.class);

    protected UserExecContext ctx;

    protected TabularToRelationalConfig_V2 config;

    protected WritableRelationalDataUnit outputDataunit;

    private boolean tableCreated;

    public RelationalParser(UserExecContext ctx, TabularToRelationalConfig_V2 config, WritableRelationalDataUnit outputDataunit) {
        this.ctx = ctx;
        this.config = config;
        this.outputDataunit = outputDataunit;
    }

    /**
     * Method reads input file, and writes output to output relational data unit.
     *
     * @param inputFile
     * @throws DataUnitException
     */
    public abstract void parseFile(File inputFile) throws DataUnitException;

    protected String buildCreateTableQuery(List<ColumnMappingEntry> columns) {
        // get column names that make up composite key
        List<String> compositeKeyList = getCompositeKeyColumnNamesFromColumnMappings();

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("CREATE TABLE %s (", config.getTableName()));
        for (ColumnMappingEntry entry : config.getColumnMapping()) {
            sb.append(entry.getColumnName());
            sb.append(" ");
            sb.append(entry.getDataType());
            sb.append(", ");
        }
        if (!compositeKeyList.isEmpty()) {
            sb.append(String.format("PRIMARY KEY (%s)", StringUtils.join(compositeKeyList, ", ")));
        } else {
            if(sb.charAt(sb.length() - 1) == ' ') { // if last char is space
                sb.delete(sb.length() - 2, sb.length()); // remove last join string (', ')
            }
        }
        sb.append(");");
        return sb.toString();
    }

    /**
     * Method retrieves column names from DPU config.
     * @return List of column names.
     */
    protected List<String> getColumnNamesFromColumnMappings() {
        List<String> list = new ArrayList<>();
        for (ColumnMappingEntry e : config.getColumnMapping()) {
            list.add(e.getColumnName());
        }
        return list;
    }

    /**
     * Method retrieves column names from config file, which together creates composite key.
     * @return List of column names, which are creating composite key.
     */
    protected List<String> getCompositeKeyColumnNamesFromColumnMappings() {
        List<String> list = new ArrayList<>();
        for (ColumnMappingEntry e : config.getColumnMapping()) {
            if (e.isPrimaryKey()) {
                list.add(e.getColumnName());
            }
        }
        return list;
    }

    /**
     * Method returns true if row data contains at least one non empty String.
     * @param rowData
     * @return true if rowdata contains some data
     */
    public static boolean rowContainsData(List<String> rowData) {
        if (!rowData.isEmpty()) {
            for (String s : rowData) {
                if (isNotEmpty(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return true if table was already created in output relational data unit.
     */
    public boolean isTableCreated() {
        return tableCreated;
    }

    public void setTableCreated(boolean tableCreated) {
        this.tableCreated = tableCreated;
    }
}
