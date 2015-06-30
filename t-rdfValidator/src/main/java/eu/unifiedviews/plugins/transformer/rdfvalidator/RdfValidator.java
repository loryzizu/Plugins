package eu.unifiedviews.plugins.transformer.rdfvalidator;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openrdf.model.URI;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.UnsupportedQueryLanguageException;
import org.openrdf.query.parser.ParsedBooleanQuery;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.ParsedTupleQuery;
import org.openrdf.query.parser.QueryParserUtil;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext.MessageType;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.dataset.DatasetBuilder;
import eu.unifiedviews.helpers.dataunit.rdf.RDFHelper;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.config.migration.ConfigurationUpdate;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;

@DPU.AsTransformer
public class RdfValidator extends AbstractDpu<RdfValidatorConfig_V2> {

    private static final Logger LOG = LoggerFactory.getLogger(RdfValidator.class);

    @DataUnit.AsInput(name = "rdfInput")
    public RDFDataUnit rdfInput;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    @ExtensionInitializer.Init(param = "eu.unifiedviews.plugins.transformer.rdfvalidator.RdfValidatorConfig_V2")
    public ConfigurationUpdate _ConfigurationUpdate;

    public RdfValidator() {
        super(RdfValidatorVaadinDialog.class, ConfigHistory.history(RdfValidatorConfig_V2.class)
                .alternative(RdfValidatorConfig_V1.class).addCurrent(RdfValidatorConfig_V2.class));
    }

    @Override
    protected void innerExecute() throws DPUException {
        ContextUtils.sendMessage(ctx, MessageType.INFO, "status.starting", "");
        if (StringUtils.isBlank(config.getQuery())) {
            throw ContextUtils.dpuException(ctx, "error.invalidConfiguration.queryEmpty");
        }
        ParsedQuery parsedQuery;
        try {
            parsedQuery = QueryParserUtil.parseQuery(QueryLanguage.SPARQL, config.getQuery(), (String) null);
            if (!(parsedQuery instanceof ParsedTupleQuery) && !(parsedQuery instanceof ParsedBooleanQuery)) {
                throw ContextUtils.dpuException(ctx, "error.unsupported.query.type");
            }
        } catch (UnsupportedQueryLanguageException | MalformedQueryException ex) {
            throw ContextUtils.dpuException(ctx, ex, "error.query.parse");
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
            Set<URI> graphURIs = new HashSet<>();
            for (RDFDataUnit.Entry entry : graphs) {
                graphURIs.add(entry.getDataGraphURI());
            }

            // Prepare query.
            if (parsedQuery instanceof ParsedTupleQuery) {
                TupleQuery query;
                try {
                    query = connection.prepareTupleQuery(QueryLanguage.SPARQL, config.getQuery());
                    query.setDataset(new DatasetBuilder().withDefaultGraphs(graphURIs).build());
                    TupleQueryResult result = query.evaluate();
                    if (result.hasNext()) {
                        if (config.isFailExecution()) {
                            throw ContextUtils.dpuException(ctx, "error.data.validation");
                        } else {
                            ContextUtils.sendMessage(ctx, MessageType.WARNING, "error.data.validation", "");
                        }
                    }
                } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
                    throw ContextUtils.dpuException(ctx, "error.query.execution");
                }
            } else if (parsedQuery instanceof ParsedBooleanQuery) {
                BooleanQuery query;
                try {
                    query = connection.prepareBooleanQuery(QueryLanguage.SPARQL, config.getQuery());
                    query.setDataset(new DatasetBuilder().withDefaultGraphs(graphURIs).build());
                    if (query.evaluate()) {
                        if (config.isFailExecution()) {
                            throw ContextUtils.dpuException(ctx, "error.data.validation");
                        } else {
                            ContextUtils.sendMessage(ctx, MessageType.WARNING, "error.data.validation", "");
                        }
                    }
                } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
                    throw ContextUtils.dpuException(ctx, "error.query.execution");
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
