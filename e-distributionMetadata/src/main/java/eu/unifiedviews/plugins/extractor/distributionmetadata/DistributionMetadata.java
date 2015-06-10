package eu.unifiedviews.plugins.extractor.distributionmetadata;

import java.util.Date;

import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.distribution.DistributionToStatementsConverter;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;

@DPU.AsExtractor
public class DistributionMetadata extends AbstractDpu<DistributionMetadataConfig_V1> {

    @DataUnit.AsOutput(name = "distributionOutput")
    public WritableRDFDataUnit distributionOutput;

    public DistributionMetadata() {
        super(DistributionMetadataVaadinDialog.class,
                ConfigHistory.noHistory(DistributionMetadataConfig_V1.class)
                        );
    }

    @Override
    protected void innerExecute() throws DPUException {
        final Date dateStart = new Date();

        RepositoryConnection connection = null;
        try {
            URI graph = distributionOutput.addNewDataGraph("distributionMetadata");
            connection = distributionOutput.getConnection();
            connection.begin();
            connection.add(DistributionToStatementsConverter.distributionToStatements(DistributionMetadataConfigDistributionConverter.v1ToDistribution(config)), graph);
            connection.commit();
        } catch (RepositoryException | DataUnitException ex) {
            ContextUtils.dpuException(ctx, ex, "DistributionMetadata.execute.exception");
        } finally {
            if (connection!=null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {
                }
            }
        }

        final Date dateEnd = new Date();
        ContextUtils.sendShortInfo(ctx, "DistributionMetadata.innerExecute.done", (dateEnd.getTime() - dateStart.getTime()));
    }
}
