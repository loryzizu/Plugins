package eu.unifiedviews.plugins.transformer.tabulartorelational.parser;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.UserExecContext;
import eu.unifiedviews.plugins.transformer.tabulartorelational.TabularToRelationalConfig_V2;
import eu.unifiedviews.plugins.transformer.tabulartorelational.model.ColumnMappingEntry;
import eu.unifiedviews.plugins.transformer.tabulartorelational.util.DatabaseHelper;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class XLSParser extends RelationalParser {

    public XLSParser(UserExecContext ctx, TabularToRelationalConfig_V2 config, WritableRelationalDataUnit outputDataunit) {
        super(ctx, config, outputDataunit);
    }

    @Override
    public void parseFile(File inputFile) throws DataUnitException {
        final Workbook wb;
        try {
            wb = WorkbookFactory.create(inputFile);
        } catch (IOException | InvalidFormatException e) {
            throw new DataUnitException("Could not open Excel workbook.", e);
        }

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

        // we process only first sheet of document
        Sheet sheet = wb.getSheetAt(0);

        DataFormatter formatter = new DataFormatter();
        FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
        try (DatabaseHelper.BulkInserter bulkInserter = new DatabaseHelper.BulkInserter(outputDataunit, config.getTableName(), config.getColumnMapping())) {
            int skipFirstNRows = config.getDataBegginningRow() - 1;
            for (Row row : sheet) { // for each row in sheet
                if (ctx.canceled()) { // check if processing was not cancelled
                    throw new DataUnitException(ctx.tr("dpu.cancelled"));
                }
                if (row.getRowNum() < skipFirstNRows) { // we skip first N rows
                    continue;
                }
                List<String> rowData = new ArrayList<>();
                for (Cell cell : row) {
                    if (cell.getColumnIndex() < config.getColumnMapping().size()) {
                        String cellValue;
                        try {
                            cellValue = formatter.formatCellValue(cell, formulaEvaluator);
                        } catch (RuntimeException e) {
                            // cell reference is used for EXCEL style cell location e.g. 'A3'
                            CellReference cr = new CellReference(cell);
                            ContextUtils.sendMessage(ctx, DPUContext.MessageType.WARNING, "csv.cell.parse.error", e, "csv.cell.parse.error.number", cr.formatAsString());
                            cellValue = "";
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
        }
    }
}
