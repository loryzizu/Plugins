package eu.unifiedviews.plugins.transformer.tabulartorelational;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.files.FilesDataUnitUtils;
import eu.unifiedviews.helpers.dataunit.resource.Resource;
import eu.unifiedviews.helpers.dataunit.resource.ResourceHelpers;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.config.migration.ConfigurationUpdate;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultToleranceUtils;
import eu.unifiedviews.plugins.transformer.tabulartorelational.parser.CSVParser;
import eu.unifiedviews.plugins.transformer.tabulartorelational.parser.DBFParser;
import eu.unifiedviews.plugins.transformer.tabulartorelational.parser.RelationalParser;
import eu.unifiedviews.plugins.transformer.tabulartorelational.parser.XLSParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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

    public static final String[] CHARSETS = { "UTF-8", "windows-1250", "ISO-8859-2", "US-ASCII", "IBM00858", "IBM437", "IBM775", "IBM850", "IBM852", "IBM855", "IBM857", "IBM862", "IBM866", "ISO-8859-1", "ISO-8859-4", "ISO-8859-5", "ISO-8859-7", "ISO-8859-9", "ISO-8859-13", "ISO-8859-15", "KOI8-R",
            "KOI8-U",
            "UTF-16", "UTF-16BE", "UTF-16LE", "UTF-32", "UTF-32BE", "UTF-32LE", "x-UTF-32BE-BOM", "x-UTF-32LE-BOM", "windows-1251", "windows-1252", "windows-1253", "windows-1254", "windows-1257", "x-IBM737", "x-IBM874", "x-UTF-16LE-BOM" };

    public TabularToRelational() {
        super(TabularToRelationalVaadinDialog.class, ConfigHistory.noHistory(TabularToRelationalConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {
        final List<FilesDataUnit.Entry> files = FaultToleranceUtils.getEntries(faultTolerance, inFilesData, FilesDataUnit.Entry.class);
        final Iterator<FilesDataUnit.Entry> filesIteration = files.iterator();

        try {
            // for each input file
            while (!ctx.canceled() && filesIteration.hasNext()) {
                final File inputFile = FilesDataUnitUtils.asFile(filesIteration.next());
                LOG.debug("Adding file: {}", inputFile.getName());

                RelationalParser parser = null;
                switch (config.getParserType()) {
                    case CSV:
                        parser = new CSVParser(ctx, config, outRelationalData);
                        break;
                    case XLS:
                        parser = new XLSParser(ctx, config, outRelationalData);
                        break;
                    case DBF:
                        parser = new DBFParser(ctx, config, outRelationalData);
                        break;
                    default:
                        throw new DataUnitException("Unsupported ParserType!");
                }

                // parse file and add output to result table
                parser.parseFile(inputFile);
            }

            // finally add metadata
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
        } catch (DataUnitException e) {
            throw ContextUtils.dpuException(ctx, e, "errors.dpu.parse.failed");
        }
        ContextUtils.sendShortInfo(ctx, "parsing.finished");
    }
}
