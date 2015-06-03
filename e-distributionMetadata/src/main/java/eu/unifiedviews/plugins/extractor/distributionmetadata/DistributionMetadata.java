package eu.unifiedviews.plugins.extractor.distributionmetadata;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryConnection;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.DataUnitUtils;
import eu.unifiedviews.helpers.dataunit.rdf.RdfDataUnitUtils;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.config.migration.ConfigurationUpdate;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
import eu.unifiedviews.helpers.dpu.extension.rdf.simple.WritableSimpleRdf;
import eu.unifiedviews.helpers.dpu.rdf.EntityBuilder;
import eu.unifiedviews.helpers.dpu.rdf.sparql.SparqlUtils;

@DPU.AsExtractor
public class DistributionMetadata extends AbstractDpu<DistributionMetadataConfig_V1> {

    @DataUnit.AsInput(name = "datasetMetadata", optional = true)
    public RDFDataUnit inRdfData;

    @DataUnit.AsOutput(name = "metadata")
    public WritableRDFDataUnit outRdfData;

    @ExtensionInitializer.Init(param = "outRdfData")
    public WritableSimpleRdf rdfData;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    @ExtensionInitializer.Init
    public ConfigurationUpdate _configurationUpdate;

    public DistributionMetadata() {
        super(DistributionMetadataVaadinDialog.class,
                ConfigHistory.noHistory(DistributionMetadataConfig_V1.class)
                        );
    }

    @Override
    protected void innerExecute() throws DPUException {
        final Date dateStart = new Date();

        final Date dateEnd = new Date();
        ContextUtils.sendShortInfo(ctx, "DistributionMetadata.innerExecute.done", (dateEnd.getTime() - dateStart.getTime()));
    }
}
