package eu.unifiedviews.plugins.extractor.distributionmetadata;

import java.util.Date;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.resource.ResourceHelpers;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;

@DPU.AsExtractor
public class DistributionMetadata extends AbstractDpu<DistributionMetadataConfig_V1> {
    public static final String outputSymbolicName = "distributionMetadata";

    @DataUnit.AsOutput(name = "distributionOutput")
    public WritableRDFDataUnit distributionOutput;

    public DistributionMetadata() {
        super(DistributionMetadataVaadinDialog.class,
                ConfigHistory.noHistory(DistributionMetadataConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {
        final Date dateStart = new Date();

        try {
            distributionOutput.addNewDataGraph(outputSymbolicName);
            ResourceHelpers.setResource(distributionOutput, outputSymbolicName, DistributionMetadataConfigToResourceConverter.v1ToResource(config));
        } catch (DataUnitException ex) {
            ContextUtils.dpuException(ctx, ex, "DistributionMetadata.execute.exception");
        }

        final Date dateEnd = new Date();
        ContextUtils.sendShortInfo(ctx, "DistributionMetadata.innerExecute.done", (dateEnd.getTime() - dateStart.getTime()));
    }
}
