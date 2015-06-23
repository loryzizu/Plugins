package eu.unifiedviews.plugins.extractor.distributionmetadata;

import eu.unifiedviews.helpers.dataunit.resource.Resource;

public class DistributionMetadataConfigToResourceConverter {
    public static Resource v1ToResource(DistributionMetadataConfig_V1 config) {
        Resource resource = new Resource();
        resource.setName(config.getName());
        resource.setDescription(config.getDescription());
        resource.setFormat(config.getFormat());
        resource.setMimetype(config.getMimetype());
        resource.setCreated(config.getCreated());;
        return resource;
    }
}
