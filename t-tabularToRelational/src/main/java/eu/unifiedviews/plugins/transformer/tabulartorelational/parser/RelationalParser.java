package eu.unifiedviews.plugins.transformer.tabulartorelational.parser;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import eu.unifiedviews.helpers.dpu.exec.UserExecContext;
import eu.unifiedviews.plugins.transformer.tabulartorelational.TabularToRelationalConfig_V1;
import eu.unifiedviews.plugins.transformer.tabulartorelational.model.ColumnMappingEntry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class RelationalParser {

    protected static final Logger LOG = LoggerFactory.getLogger(RelationalParser.class);

    protected UserExecContext ctx;

    protected TabularToRelationalConfig_V1 config;

    protected WritableRelationalDataUnit outputDataunit;

    private boolean tableCreated;

    public RelationalParser(UserExecContext ctx, TabularToRelationalConfig_V1 config, WritableRelationalDataUnit outputDataunit) {
        this.ctx = ctx;
        this.config = config;
        this.outputDataunit = outputDataunit;
    }

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
        }
        sb.append(");");
        return sb.toString();
    }

    protected List<String> getColumnNamesFromColumnMappings() {
        List<String> list = new ArrayList<>();
        for (ColumnMappingEntry e : config.getColumnMapping()) {
            list.add(e.getColumnName());
        }
        return list;
    }

    protected List<String> getCompositeKeyColumnNamesFromColumnMappings() {
        List<String> list = new ArrayList<>();
        for (ColumnMappingEntry e : config.getColumnMapping()) {
            if (e.isPrimaryKey()) {
                list.add(e.getColumnName());
            }
        }
        return list;
    }

    public boolean isTableCreated() {
        return tableCreated;
    }

    public void setTableCreated(boolean tableCreated) {
        this.tableCreated = tableCreated;
    }
}
