package eu.unifiedviews.plugins.transformer.tabulartorelational;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.resource.Resource;
import eu.unifiedviews.helpers.dataunit.resource.ResourceHelpers;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.config.migration.ConfigurationUpdate;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultToleranceUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@DPU.AsTransformer
public class TabularToRelational extends AbstractDpu<TabularToRelationalConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(TabularToRelational.class);

    @DataUnit.AsInput(name = "input")
    public FilesDataUnit inFilesData;

    @DataUnit.AsOutput(name = "output")
    public WritableRelationalDataUnit outRelationalData;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    @ExtensionInitializer.Init(param = "eu.unifiedviews.plugins.transformer.tabulartorelational.TabularToRelationalConfig__V1")
    public ConfigurationUpdate _ConfigurationUpdate;

    public static final String[] CHARSETS = { "UTF-8", "windows-1250", "ISO-8859-2", "US-ASCII", "IBM00858", "IBM437", "IBM775", "IBM850", "IBM852", "IBM855", "IBM857", "IBM862", "IBM866", "ISO-8859-1", "ISO-8859-4", "ISO-8859-5", "ISO-8859-7", "ISO-8859-9", "ISO-8859-13", "ISO-8859-15", "KOI8-R", "KOI8-U",
            "UTF-16", "UTF-16BE", "UTF-16LE", "UTF-32", "UTF-32BE", "UTF-32LE", "x-UTF-32BE-BOM", "x-UTF-32LE-BOM", "windows-1251", "windows-1252", "windows-1253", "windows-1254", "windows-1257", "x-IBM737", "x-IBM874", "x-UTF-16LE-BOM" };

    public TabularToRelational() {
        super(TabularToRelationalVaadinDialog.class, ConfigHistory.noHistory(TabularToRelationalConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {
        final List<FilesDataUnit.Entry> files = FaultToleranceUtils.getEntries(faultTolerance, inFilesData, FilesDataUnit.Entry.class);
        final Iterator<FilesDataUnit.Entry> filesIteration = files.iterator();

        try {
            // create table from user config
            String createTableQuery = prepareCreateTableQuery(config);
            LOG.debug("Table create query: {}", createTableQuery);
            ContextUtils.sendMessage(ctx, DPUContext.MessageType.DEBUG, ctx.tr("create.new.table"), createTableQuery);
            DatabaseHelper.executeUpdate(createTableQuery, outRelationalData);

            // add metadata
            final String tableName = config.getTableName().toUpperCase();
            outRelationalData.addExistingDatabaseTable(tableName, tableName);

            faultTolerance.execute(new FaultTolerance.Action() {

                @Override
                public void action() throws Exception {
                    Resource resource = ResourceHelpers.getResource(outRelationalData, tableName);
                    Date now = new Date();
                    resource.setCreated(now);
                    resource.setLast_modified(now);
                    ResourceHelpers.setResource(outRelationalData, tableName, resource);
                }
            });

            // for each input file
            while (!ctx.canceled() && filesIteration.hasNext()) {
                final FilesDataUnit.Entry entry = filesIteration.next();
                LOG.debug("Adding file: {}", entry.getSymbolicName());

                // remove URL prefixes
                final String csvPath = StringUtils.substringAfterLast(URLDecoder.decode(entry.getFileURIString(), "UTF8"), ":");

                String insertIntoQuery = prepareInsertIntoQuery(csvPath, config);
                LOG.debug("Insert into query: {}", insertIntoQuery);
                ContextUtils.sendMessage(ctx, DPUContext.MessageType.DEBUG, ctx.tr("insert.file", entry.getSymbolicName()), insertIntoQuery);
                // insert file into internal database
                DatabaseHelper.executeUpdate(insertIntoQuery, outRelationalData);
            }
        } catch (DataUnitException | UnsupportedEncodingException e) {
            throw ContextUtils.dpuException(ctx, e, "errors.dpu.parse.failed");
        }

        ContextUtils.sendShortInfo(ctx, "parsing.finished");
    }

    protected static String prepareInsertIntoQuery(String csvFilePath, TabularToRelationalConfig_V1 config) {
        List<String> columnNames = getColumnNamesFromColumnMappings(config);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("INSERT INTO %s (SELECT %s FROM CSVREAD( '%s', '%s', '%s')",
                config.getTableName(),
                StringUtils.join(columnNames, ", "),
                csvFilePath,
                StringUtils.join(columnNames, config.getFieldSeparator()).toUpperCase(), // names of columns have to be in upper case
                processCsvOptions(config)
        ));

        if (config.isHasHeader()) { // skip header
            sb.append(" LIMIT NULL OFFSET 1");
        }
        sb.append(");");
        return sb.toString();
    }

    protected static String prepareCreateTableQuery(TabularToRelationalConfig_V1 config) {
        // get column names that make up composite key
        List<String> compositeKeyList = getCompositeKeyColumnNamesFromColumnMappings(config);

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

    protected static String processCsvOptions(TabularToRelationalConfig_V1 config) {
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

    private static List<String> getColumnNamesFromColumnMappings(TabularToRelationalConfig_V1 config) {
        List<String> list = new ArrayList<>();
        for (ColumnMappingEntry e : config.getColumnMapping()) {
            list.add(e.getColumnName());
        }
        return list;
    }

    private static List<String> getCompositeKeyColumnNamesFromColumnMappings(TabularToRelationalConfig_V1 config) {
        List<String> list = new ArrayList<>();
        for (ColumnMappingEntry e : config.getColumnMapping()) {
            if (e.isPrimaryKey()) {
                list.add(e.getColumnName());
            }
        }
        return list;
    }
}
