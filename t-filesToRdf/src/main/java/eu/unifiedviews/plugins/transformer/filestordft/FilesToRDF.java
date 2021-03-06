package eu.unifiedviews.plugins.transformer.filestordft;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.ParseErrorLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.copy.CopyHelpers;
import eu.unifiedviews.helpers.dataunit.dataset.DatasetBuilder;
import eu.unifiedviews.helpers.dataunit.files.FilesVocabulary;
import eu.unifiedviews.helpers.dataunit.metadata.MetadataUtils;
import eu.unifiedviews.helpers.dataunit.resource.Resource;
import eu.unifiedviews.helpers.dataunit.resource.ResourceHelpers;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.config.migration.ConfigurationUpdate;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultToleranceUtils;

@DPU.AsTransformer
public class FilesToRDF extends AbstractDpu<FilesToRDFConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(FilesToRDF.class);

    @DataUnit.AsInput(name = "filesInput")
    public FilesDataUnit filesInput;

    @DataUnit.AsOutput(name = "rdfOutput")
    public WritableRDFDataUnit rdfOutput;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    @ExtensionInitializer.Init(param = "eu.unifiedviews.plugins.transformer.filestordft.FilesToRDFConfig__V1")
    public ConfigurationUpdate _ConfigurationUpdate;

    private static final String SYMBOLIC_NAME_BINDING = "symbolicName";

    private static final String DATA_GRAPH_BINDING = "dataGraph";

    private static final String UPDATE_EXISTING_GRAPH_FROM_FILE = getUpdateQueryString();

    private static String getUpdateQueryString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE ");
        sb.append("{ ");
        sb.append("?s <" + FilesDataUnit.PREDICATE_FILE_URI + "> ?o ");
        sb.append("} ");
        sb.append("INSERT ");
        sb.append("{ ");
        sb.append("?s <" + RDFDataUnit.PREDICATE_DATAGRAPH_URI + "> ?" + DATA_GRAPH_BINDING + " ");
        sb.append("} ");
        sb.append("WHERE ");
        sb.append("{");
        sb.append("?s <" + MetadataDataUnit.PREDICATE_SYMBOLIC_NAME + "> ?" + SYMBOLIC_NAME_BINDING + " . ");
        sb.append("?s <" + FilesDataUnit.PREDICATE_FILE_URI + "> ?o ");
        sb.append("}");
        return sb.toString();
    }

    /**
     * True if at least one file has been skipped during conversion.
     */
    private boolean fileSkipped = false;

    protected AtomicInteger atomicInteger = new AtomicInteger();

    public FilesToRDF() {
        super(FilesToRDFVaadinDialog.class, ConfigHistory.noHistory(FilesToRDFConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {
        ContextUtils.sendInfo(ctx, "FilesToRDF.execute.starting", "");

        final URI globalOutputGraphUri;

        // Create output graph if we are in M->1 mode.
        if (FilesToRDFConfig_V1.USE_FIXED_SYMBOLIC_NAME.equals(config.getOutputNaming())) {
            // Use given value from config as output graph name.
            try {
                String value = config.getOutputSymbolicName();
                if (value == null || value.isEmpty()) {
                    Date currentTime = new Date();
                    value = "FilesToRDF/generated_" + Long.toString(currentTime.getTime());
                }
                LOG.info("Output symbolic name: {}", value);
                globalOutputGraphUri = rdfOutput.addNewDataGraph(value);
                final String outputSymbolicName = value;
                faultTolerance.execute(new FaultTolerance.Action() {

                    @Override
                    public void action() throws Exception {
                        Resource resource = ResourceHelpers.getResource(rdfOutput, outputSymbolicName);
                        Date now = new Date();
                        resource.setLast_modified(now);
                        resource.setCreated(now);
                        ResourceHelpers.setResource(rdfOutput, outputSymbolicName, resource);
                    }
                });

            } catch (DataUnitException ex) {
                throw ContextUtils.dpuException(ctx, ex, "FilesToRDF.execute.exception.outputGraph");
            }
        } else {
            globalOutputGraphUri = null;
        }

        // Load files.
        final List<FilesDataUnit.Entry> files = FaultToleranceUtils.getEntries(faultTolerance, filesInput, FilesDataUnit.Entry.class);

        // If true then next file is processed.
        int index = 1;
        for (final FilesDataUnit.Entry entry : files) {
            LOG.info("Processing file {}/{}", index++, files.size());
            if (ctx.canceled()) {
                throw ContextUtils.dpuExceptionCancelled(ctx);
            }

            // Set output graph name.
            final URI outputGraphUri;
            if (globalOutputGraphUri == null) {
                faultTolerance.execute(new FaultTolerance.Action() {

                    @Override
                    public void action() throws Exception {
                        CopyHelpers.copyMetadata(entry.getSymbolicName(), filesInput, rdfOutput);
                    }
                });

                outputGraphUri = faultTolerance.execute(new FaultTolerance.ActionReturn<URI>() {

                    @Override
                    public URI action() throws Exception {
                        return new URIImpl(rdfOutput.getBaseDataGraphURI().stringValue() + "/" + String.valueOf(atomicInteger.getAndIncrement()));
                    }
                });

                faultTolerance.execute(new FaultTolerance.Action() {

                    @Override
                    public void action() throws Exception {
                        updateExistingDataGraphFromFile(entry.getSymbolicName(), outputGraphUri);
                    }
                });

                faultTolerance.execute(new FaultTolerance.Action() {

                    @Override
                    public void action() throws Exception {
                        Resource resource = ResourceHelpers.getResource(filesInput, entry.getSymbolicName());
                        Date now = new Date();
                        resource.setLast_modified(now);
                        ResourceHelpers.setResource(rdfOutput, entry.getSymbolicName(), resource);
                    }
                });
            } else {
                outputGraphUri = globalOutputGraphUri;
            }
            // Determine format.
            final RDFFormat format = faultTolerance.execute(new FaultTolerance.ActionReturn<RDFFormat>() {

                @Override
                public RDFFormat action() throws Exception {
                    if (!config.getOutputType().equals("AUTO")) {
                        return Rio.getParserFormatForMIMEType(config.getOutputType());
                    }
                    String inputVirtualPath = MetadataUtils.get(filesInput, entry, FilesVocabulary.UV_VIRTUAL_PATH);
                    if (inputVirtualPath != null) {
                        return Rio.getParserFormatForFileName(inputVirtualPath);
                    } else {
                        return Rio.getParserFormatForFileName(entry.getSymbolicName());
                    }
                }
            });

            LOG.debug("Starting extraction of file: {}", entry);
            try {
                faultTolerance.execute(rdfOutput, new FaultTolerance.ConnectionAction() {

                    @Override
                    public void action(RepositoryConnection connection) throws Exception {

                        RDFInserter rdfInserter = new CancellableCommitSizeInserter(connection,
                                config.getCommitSize(), ctx);
                        rdfInserter.enforceContext(outputGraphUri);
                        ParseErrorListenerEnabledRDFLoader loader = new ParseErrorListenerEnabledRDFLoader(
                                connection.getParserConfig(), connection.getValueFactory());
                        try {
                            loader.load(new File(java.net.URI.create(entry.getFileURIString())), null, format,
                                    rdfInserter, new ParseErrorLogger());
                        } catch (IOException | RDFHandlerException | RDFParseException ex) {
                            switch (config.getFatalErrorHandling()) {
                                case FilesToRDFConfig_V1.SKIP_CONTINUE_NEXT_FILE_ERROR_HANDLING:
                                    handleErrorForEntry(entry, ex);
                                    fileSkipped = true;
                                    break;
                                case FilesToRDFConfig_V1.STOP_EXTRACTION_ERROR_HANDLING:
                                default:
                                    throw ex;
                            }
                        } catch (RDFDataFormatNullException ex) {
                            switch (config.getFatalErrorHandling()) {
                                case FilesToRDFConfig_V1.SKIP_CONTINUE_NEXT_FILE_ERROR_HANDLING:
                                    handleErrorForEntry(entry, ex);
                                    fileSkipped = true;
                                    break;
                                case FilesToRDFConfig_V1.STOP_EXTRACTION_ERROR_HANDLING:
                                default:
                                    LOG.error("There was a problem extracting file with symbolic name '{}' and path '{}': {}", entry.getSymbolicName(), entry.getFileURIString(), ctx.tr("FilesToRDF.execute.failure.rdfformatnull"));
                                    throw ContextUtils.dpuException(ctx, "FilesToRDF.execute.failure.rdfformatnull");
                            }
                        }
                    }

                    private void handleErrorForEntry(FilesDataUnit.Entry entry, java.lang.Exception ex) throws DataUnitException {
                        LOG.warn("There was a problem extracting file with symbolic name '{}' and path '{}', such file is skipped",
                                entry.getSymbolicName(),
                                entry.getFileURIString());
                        LOG.info("The detail of the problem: {}", ex.getLocalizedMessage());

                    }
                });
                LOG.debug("Finished extraction of file: {}", entry);
            } catch (DPUException ex) {
                throw ContextUtils.dpuException(ctx, ex, "FilesToRDF.execute.failure");
            }
        }
        // Publish messsage.
        if (fileSkipped) {
            ContextUtils.sendWarn(ctx, "FilesToRDF.execute.skipped", "");
        }
    }

    private void updateExistingDataGraphFromFile(String symbolicName, URI newDataGraphURI) throws DPUException {
        RepositoryConnection connection = null;
        RepositoryResult<Statement> result = null;
        try {
            connection = rdfOutput.getConnection();
            connection.begin();
            ValueFactory valueFactory = connection.getValueFactory();
            Literal symbolicNameLiteral = valueFactory.createLiteral(symbolicName);
            Update update = connection.prepareUpdate(QueryLanguage.SPARQL, UPDATE_EXISTING_GRAPH_FROM_FILE);
            update.setBinding(SYMBOLIC_NAME_BINDING, symbolicNameLiteral);
            update.setBinding(DATA_GRAPH_BINDING, newDataGraphURI);

            update.setDataset(new DatasetBuilder()
                    .addDefaultGraph(rdfOutput.getMetadataWriteGraphname())
                    .withInsertGraph(rdfOutput.getMetadataWriteGraphname())
                    .addDefaultRemoveGraph(rdfOutput.getMetadataWriteGraphname())
                    .build());
            update.execute();
            connection.commit();
        } catch (DataUnitException | RepositoryException | MalformedQueryException | UpdateExecutionException ex) {
            throw ContextUtils.dpuException(ctx, "FilesToRDF.execute.exception.dataGraph");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error when closing connection", ex);
                    // eat close exception, we cannot do anything clever here
                }
            }
            if (result != null) {
                try {
                    result.close();
                } catch (RepositoryException ex) {
                    LOG.warn("Error in close", ex);
                    // eat close exception, we cannot do anything clever here
                }
            }
        }
    }
}
