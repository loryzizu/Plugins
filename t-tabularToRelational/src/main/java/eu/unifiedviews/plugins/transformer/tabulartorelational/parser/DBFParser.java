package eu.unifiedviews.plugins.transformer.tabulartorelational.parser;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.UserExecContext;
import eu.unifiedviews.plugins.transformer.tabulartorelational.TabularToRelationalConfig_V2;
import eu.unifiedviews.plugins.transformer.tabulartorelational.model.ColumnMappingEntry;
import eu.unifiedviews.plugins.transformer.tabulartorelational.util.DatabaseHelper;

import org.jamel.dbf.DbfReader;
import org.jamel.dbf.exception.DbfException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser responsible for reading DBF files.
 * Uses custom library to read file.
 */
public class DBFParser extends RelationalParser {

    public DBFParser(UserExecContext ctx, TabularToRelationalConfig_V2 config, WritableRelationalDataUnit outputDataunit) {
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
            ContextUtils.sendMessage(ctx, DPUContext.MessageType.DEBUG, "create.new.table", createTableQuery);
            DatabaseHelper.executeUpdate(createTableQuery, outputDataunit);
            setTableCreated(true);
        }

        try (DbfReader reader = new DbfReader(inputFile);
                DatabaseHelper.BulkInserter bulkInserter = new DatabaseHelper.BulkInserter(outputDataunit, config.getTableName(), config.getColumnMapping())) {
            Object[] row;
            int rowNum = -1;
            int skipFirstNRows = config.getDataBegginningRow() - 2; // first row is header
            while ((row = reader.nextRecord()) != null) {
                rowNum++;
                if (ctx.canceled()) { // check if processing was not cancelled
                    throw new DataUnitException(ctx.tr("dpu.cancelled"));
                }
                if (rowNum < skipFirstNRows) { // we skip first N rows
                    continue;
                }
                List<String> rowData = new ArrayList<>();
                for (int i = 0; i < row.length; i++) {
                    if (i < config.getColumnMapping().size()) {
                        String cellValue = null;
                        if (row[i] instanceof byte[]) {
                            try {
                                cellValue = new String((byte[]) row[i], config.getEncoding());
                            } catch (UnsupportedEncodingException e) {
                                throw new DataUnitException(ctx.tr("errors.unsupported.encoding"));
                            }
                        } else {
                            cellValue = row[i].toString();
                        }
                        rowData.add(cellValue);
                    }
                }
                // we have the data, now insert them into DB
                if (rowContainsData(rowData)) {
                    try {
                        bulkInserter.insertData(rowData);
                    } catch (SQLException e) {
                        throw new DataUnitException("Could not insert parsed data!", e);
                    }
                }
            }
        } catch (DbfException e) {
            throw new DataUnitException("Could not open DBF file.", e);
        }
    }
}
