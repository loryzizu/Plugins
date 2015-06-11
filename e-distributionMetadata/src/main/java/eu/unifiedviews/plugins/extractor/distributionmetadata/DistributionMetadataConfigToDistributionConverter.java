package eu.unifiedviews.plugins.extractor.distributionmetadata;

import eu.unifiedviews.helpers.dataunit.distribution.Distribution;

public class DistributionMetadataConfigToDistributionConverter {
    public static Distribution v1ToDistribution(DistributionMetadataConfig_V1 config) {
        Distribution distribution = new Distribution();
        if (config.getDcatAccessURL() != null) {
            distribution.setDcatAccessURL(config.getDcatAccessURL().toASCIIString());
        }
        distribution.setDctermsDescription(config.getDctermsDescription());
        distribution.setDctermsFormat(config.getDctermsFormat());
        if (config.getDctermsLicense() != null) {
            distribution.setDctermsLicense(config.getDctermsLicense().toASCIIString());
        }
        if (config.getDcatDownloadURL() != null) {
            distribution.setDcatDownloadURL(config.getDcatDownloadURL().toASCIIString());
        }
        distribution.setDcatMediaType(config.getDcatMediaType());
        distribution.setDctermsTitle(config.getDctermsTitle());
        if (config.getWdrsDescribedBy() != null) {
            distribution.setWdrsDescribedBy(config.getWdrsDescribedBy().toASCIIString());
        }
        if (config.getPodDistributionDescribedByType() != null) {
            distribution.setPodDistributionDescribedByType(config.getPodDistributionDescribedByType().toASCIIString());
        }
        if (config.getVoidExampleResource() != null) {
            distribution.setVoidExampleResource(config.getVoidExampleResource().toASCIIString());
        }
        return distribution;
    }
}
