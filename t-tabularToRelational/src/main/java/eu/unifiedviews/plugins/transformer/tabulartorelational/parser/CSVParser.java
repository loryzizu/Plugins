package eu.unifiedviews.plugins.transformer.tabulartorelational.parser;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.UserExecContext;
import eu.unifiedviews.plugins.transformer.tabulartorelational.TabularToRelationalConfig_V2;
import eu.unifiedviews.plugins.transformer.tabulartorelational.model.ColumnMappingEntry;
import eu.unifiedviews.plugins.transformer.tabulartorelational.util.DatabaseHelper;

import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Parser responsible for reading CSV files.
 * Uses direct function of H2 database to bulk read from CSV file into database.
 */
public class CSVParser extends RelationalParser {

    public CSVParser(UserExecContext ctx, TabularToRelationalConfig_V2 config, WritableRelationalDataUnit outputDataunit) {
        super(ctx, config, outputDataunit);
    }

    @Override
    public void parseFile(File inputFile) throws DataUnitException {
        if (!isTableCreated()) {  // create table only for first file
            List<ColumnMappingEntry> columns = config.getColumnMapping();
            if (columns == null || columns.isEmpty()) {
                throw new DataUnitException("Empty column mapping! Cannot create target table in relational dataunit.");
            }

            // create table from user config
            String createTableQuery = buildCreateTableQuery(columns);
            LOG.debug("Table create query: {}", createTableQuery);
            ContextUtils.sendMessage(ctx, DPUContext.MessageType.DEBUG, ctx.tr("create.new.table"), createTableQuery);
            DatabaseHelper.executeUpdate(createTableQuery, outputDataunit);
            setTableCreated(true);
        }

        // insert data by creating H2 query which directly parses CSV (bulk load)
        String insertIntoQuery = buildInsertIntoQuery(inputFile.getAbsolutePath());
        LOG.debug("Insert into query: {}", insertIntoQuery);
        ContextUtils.sendMessage(ctx, DPUContext.MessageType.DEBUG, ctx.tr("insert.file", inputFile.getName()), insertIntoQuery);
        // insert file into internal database
        DatabaseHelper.executeUpdate(insertIntoQuery, outputDataunit);
    }

    protected String buildInsertIntoQuery(String csvFilePath) {
        List<String> columnNames = getColumnNamesFromColumnMappings();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("INSERT INTO %s (SELECT %s FROM CSVREAD( '%s', '%s', '%s')",
                config.getTableName(),
                StringUtils.join(columnNames, ", "),
                csvFilePath,
                StringUtils.join(columnNames, config.getFieldSeparator()).toUpperCase(), // names of columns have to be in upper case
                processCsvOptions()
        ));

        int skipFirstNRows = config.getDataBegginningRow() - 1;
        if (skipFirstNRows > 0) { // skip first n rows
            sb.append(String.format(" LIMIT NULL OFFSET %d", skipFirstNRows));
        }
        sb.append(");");
        return sb.toString();
    }

    protected String processCsvOptions() {
        StringBuilder sb = new StringBuilder();
        if (isNotEmpty(config.getEncoding())) {
            sb.append("charset=");
            sb.append(config.getEncoding());
            sb.append(" ");
        }
        if (isNotEmpty(config.getFieldDelimiter())) {
            sb.append("fieldDelimiter=");
            sb.append(config.getFieldDelimiter());
            sb.append(" ");
        }

        if (isNotEmpty(config.getFieldSeparator())) {
            sb.append("fieldSeparator=");
            sb.append(config.getFieldSeparator());
            sb.append(" ");
        }
        if (sb.length() > 0) { // remove last space if needed
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
