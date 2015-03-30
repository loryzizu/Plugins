package eu.unifiedviews.plugins.transformer.tabulartorelational;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import eu.unifiedviews.helpers.dataunit.resource.Resource;
import eu.unifiedviews.helpers.dataunit.resource.ResourceHelpers;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.config.migration.ConfigurationUpdate;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultToleranceUtils;
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
            ctx.getExecMasterContext().getDpuContext().sendMessage(DPUContext.MessageType.DEBUG, ctx.tr("create.new.table"), createTableQuery);
            DatabaseHelper.executeUpdate(createTableQuery, outRelationalData);

            // add metadata
            final String tableName = processString(config.getTableName());
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
                ctx.getExecMasterContext().getDpuContext().sendMessage(DPUContext.MessageType.DEBUG, ctx.tr("insert.file", entry.getSymbolicName()), insertIntoQuery);
                // insert file into internal database
                DatabaseHelper.executeUpdate(insertIntoQuery, outRelationalData);
            }
        } catch (DataUnitException | UnsupportedEncodingException e) {
            throw ContextUtils.dpuException(ctx, e, "errors.dpu.parse.failed");
        }

        ContextUtils.sendShortInfo(ctx, "parsing.finished");
    }

    protected static String prepareInsertIntoQuery(String csvFilePath, TabularToRelationalConfig_V1 config) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");

        // add tablename
        String tableName = processString(config.getTableName());
        sb.append(tableName);

        sb.append("(SELECT ");
        // add maximum row limit
        sb.append("TOP ");
        sb.append(config.getRowsLimit());
        sb.append(" ");
        // columns
        sb.append(joinColumnNames(config.getColumnMapping(), ", "));

        sb.append(" FROM CSVREAD(");

        // add filename
        sb.append("'");
        sb.append(csvFilePath);
        sb.append("'");
        // column mapping
        sb.append(", '");
        sb.append(joinColumnNames(config.getColumnMapping(), config.getFieldSeparator()));
        sb.append("'");
        // add csv options
        sb.append(", ");
        sb.append(processCsvOptions(config));

        sb.append("))");

        return sb.toString();
    }

    protected static String prepareCreateTableQuery(TabularToRelationalConfig_V1 config) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");

        // add tablename
        String tableName = processString(config.getTableName());
        sb.append(tableName);

        sb.append("(");
        StringBuilder primaryKeys = new StringBuilder("PRIMARY KEY (");
        for (ColumnMappingEntry entry : config.getColumnMapping()) {
            // add column name
            sb.append(processString(entry.getColumnName()));

            sb.append(" ");
            // add data type
            sb.append(processString(entry.getDataType()));
            sb.append(", ");
            if (entry.isPrimaryKey()) {
                primaryKeys.append(processString(entry.getColumnName()));
                primaryKeys.append(", ");
            }
        }
        // remove last ", "
        if (primaryKeys.length() > 1) {
            primaryKeys.setLength(primaryKeys.length() - 2);
        }
        primaryKeys.append(")");
        sb.append(" ");
        sb.append(primaryKeys);
        sb.append(")");

        return sb.toString();
    }

    protected static String processString(String input) {
        String output = (input == null) ? "" : input;
        output = output.trim();
        output = output.toUpperCase();
        return output;
    }

    protected static String joinColumnNames(List<ColumnMappingEntry> list, String joiner) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(joiner);
            }
            sb.append(processString(list.get(i).getColumnName()));
        }
        return sb.toString();
    }

    protected static String processCsvOptions(TabularToRelationalConfig_V1 config) {
        StringBuilder sb = new StringBuilder();
        sb.append("'");

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

        if (sb.charAt(sb.length() - 1) == ' ') { // if last char is space, remove it
            sb.setLength(sb.length() - 1);
        }
        sb.append("'");
        return sb.toString();
    }
}
