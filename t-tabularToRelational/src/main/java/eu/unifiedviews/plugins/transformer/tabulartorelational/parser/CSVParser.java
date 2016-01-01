package eu.unifiedviews.plugins.transformer.tabulartorelational.parser;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.QuoteMode;
import org.supercsv.util.CsvContext;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.UserExecContext;
import eu.unifiedviews.plugins.transformer.tabulartorelational.TabularToRelationalConfig_V2;
import eu.unifiedviews.plugins.transformer.tabulartorelational.model.ColumnMappingEntry;
import eu.unifiedviews.plugins.transformer.tabulartorelational.util.DatabaseHelper;

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
        if (!isTableCreated()) { // create table only for first file
            List<ColumnMappingEntry> columns = config.getColumnMapping();
            if (columns == null || columns.isEmpty()) {
                throw new DataUnitException("Empty column mapping! Cannot create target table in relational dataunit.");
            }

            if (this.config.isProcessOnlyValidCsv()) {
                if (!isCsvFileValid(inputFile)) {
                    throw new DataUnitException(this.ctx.tr("csv.errors.invalid.input.short"));
                }
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
                processCsvOptions()));

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

    private boolean isCsvFileValid(File inputFile) {
        boolean isValid = false;

        CsvPreference csvPreference;
        if (this.config.getFieldDelimiter() == null || this.config.getFieldDelimiter().isEmpty()) {
            final QuoteMode customQuoteMode = new QuoteMode() {
                @Override
                public boolean quotesRequired(String csvColumn, CsvContext context, CsvPreference preference) {
                    return false;
                }
            };
            csvPreference = new CsvPreference.Builder(' ', this.config.getFieldSeparator().charAt(0),
                    "\\n").useQuoteMode(customQuoteMode).build();

        } else {
            csvPreference = new CsvPreference.Builder(this.config.getFieldDelimiter().charAt(0),
                    this.config.getFieldSeparator().charAt(0), "\\n").build();
        }

        try (FileInputStream fileInputStream = new FileInputStream(inputFile);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, this.config.getEncoding());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                CsvListReader csvListReader = new CsvListReader(bufferedReader, csvPreference)) {

            for (int i = 1; i <= this.config.getDataBegginningRow(); i++) {
                bufferedReader.readLine();
            }

            int rowNum = this.config.getDataBegginningRow();
            List<String> row = csvListReader.read();
            if (row == null) {
                // no data
                LOG.warn("No data found!");
                return true;
            }

            while (row != null && !this.ctx.canceled()) {
                if (row.size() < this.config.getColumnMapping().size()) {
                    ContextUtils.sendError(this.ctx, "csv.errors.invalid.input.short", "csv.errors.invalid.input.long", rowNum, row.size(), this.config.getColumnMapping().size());
                    return false;
                }
                row = csvListReader.read();
                rowNum++;
            }

            isValid = true;

        } catch (IOException e) {
            LOG.error("Failed to parse input CSV file", e);
            isValid = false;
        }

        return isValid;
    }
}
