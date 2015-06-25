package eu.unifiedviews.plugins.transformer.rdfvalidator;

import java.util.HashSet;
import java.util.Set;

import eu.unifiedviews.helpers.dataunit.rdf.RDFHelper;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.config.migration.ConfigurationUpdate;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
import org.apache.commons.lang3.StringUtils;
import org.openrdf.model.URI;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext.MessageType;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.dataset.DatasetBuilder;

@DPU.AsTransformer
public class RdfValidator extends AbstractDpu<RdfValidatorConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(RdfValidator.class);

    @DataUnit.AsInput(name = "rdfInput")
    public RDFDataUnit rdfInput;

    @DataUnit.AsOutput(name = "rdfOutput")
    public WritableRDFDataUnit rdfOutput;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    @ExtensionInitializer.Init(param = "eu.unifiedviews.plugins.transformer.rdfvalidator.RdfValidatorConfig_V1")
    public ConfigurationUpdate _ConfigurationUpdate;

    public RdfValidator() {
        super(RdfValidatorVaadinDialog.class, ConfigHistory.noHistory(RdfValidatorConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {
        ContextUtils.sendMessage(ctx, MessageType.INFO, "status.starting", "");
        if (StringUtils.isBlank(config.getQuery())) {
            throw ContextUtils.dpuException(ctx, "error.invalidConfiguration.queryEmpty");
        }
        if (!config.isPerGraph() && StringUtils.isBlank(config.getOutputGraphSymbolicName())) {
            throw ContextUtils.dpuException(ctx, "error.invalidConfiguration.outputGraphSymbolicNameEmpty");
        }

        // Get input graphs.
        LOG.info("Reading input graphs ...");
        final Set<RDFDataUnit.Entry> graphs;
        try {
            graphs = RDFHelper.getGraphs(rdfInput);
        } catch (DataUnitException ex) {
            throw ContextUtils.dpuException(ctx, "error.dataunit.graphList");
        }
        LOG.info("Reading input graphs ... done");

        RepositoryConnection connection = null;
        try {
            connection = rdfInput.getConnection();
            // Execute query.
            if (config.isPerGraph()) {
                ContextUtils.sendMessage(ctx, MessageType.INFO, "status.perGraphMode", "");
                for (RDFDataUnit.Entry graph : graphs) {
                    URI outputGraphURI = rdfOutput.addNewDataGraph(graph.getSymbolicName());
                    // Prepare query.
                    Update update;
                    long size = 0;
                    try {
                        update = connection.prepareUpdate(QueryLanguage.SPARQL, config.getQuery());
                        update.setDataset(new DatasetBuilder().addDefaultGraph(graph.getDataGraphURI()).addDefaultRemoveGraph(outputGraphURI).withInsertGraph(outputGraphURI).build());
                        update.execute();
                        size = connection.size(outputGraphURI);
                    } catch (RepositoryException | MalformedQueryException | UpdateExecutionException ex) {
                        throw ContextUtils.dpuException(ctx, "error.query.execution");
                    }
                    if (size > 0) {
                        if (config.isFailExecution()) {
                            throw ContextUtils.dpuException(ctx, "error.data.validation");
                        } else {
                            ContextUtils.sendMessage(ctx, MessageType.WARNING, "error.data.validation", "");
                        }
                    }
                }
            } else {
                ContextUtils.sendMessage(ctx, MessageType.INFO, "status.allGraphsMode", "");
                URI outputGraphURI = rdfOutput.addNewDataGraph(config.getOutputGraphSymbolicName());
                Set<URI> graphURIs = new HashSet<>();
                for (RDFDataUnit.Entry entry : graphs) {
                    graphURIs.add(entry.getDataGraphURI());
                }
                // Prepare query.
                Update update;
                long size = 0;
                try {
                    update = connection.prepareUpdate(QueryLanguage.SPARQL, config.getQuery());
                    update.setDataset(new DatasetBuilder().withDefaultGraphs(graphURIs).addDefaultRemoveGraph(outputGraphURI).withInsertGraph(outputGraphURI).build());
                    update.execute();
                    size = connection.size(outputGraphURI);
                } catch (RepositoryException | MalformedQueryException | UpdateExecutionException ex) {
                    throw ContextUtils.dpuException(ctx, ex, "error.query.execution");
                }
                if (size > 0) {
                    if (config.isFailExecution()) {
                        throw ContextUtils.dpuException(ctx, "error.data.validation");
                    } else {
                        ContextUtils.sendMessage(ctx, MessageType.WARNING, "error.data.validation", "");
                    }
                }
            }
        } catch (DataUnitException ex) {
            throw ContextUtils.dpuException(ctx, ex, "error.dataunit");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                    LOG.info("Error in close", ex);
                }
            }
        }
    }
}
