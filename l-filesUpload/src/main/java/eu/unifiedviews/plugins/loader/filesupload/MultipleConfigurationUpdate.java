package eu.unifiedviews.plugins.loader.filesupload;

import eu.unifiedviews.helpers.cuni.dpu.config.ConfigException;
import eu.unifiedviews.helpers.cuni.dpu.config.MasterConfigObject;
import eu.unifiedviews.helpers.cuni.migration.ConfigurationUpdate;

/**
 *
 * @author Škoda Petr
 */
public class MultipleConfigurationUpdate extends ConfigurationUpdate {

    @Override
    public String transformString(String configName, String config) throws ConfigException {
        if (!configName.equals(MasterConfigObject.CONFIG_NAME)) {
            return config;
        }
        // Fast initial check.
        if (!config.contains("<ConfigurationVersion>")) {
            return config;
        }
        // We geus the class type and set it to super class, and then let it do its job.

        if (config.contains("<port>")) {
            this.defaultClassName = "eu.unifiedviews.plugins.extractor.filestoscp.FilesToScpConfig__V1";
        } else if (config.contains("<moveFiles>")) {
            this.defaultClassName = "eu.unifiedviews.plugins.loader.filestolocalfs.FilesToLocalFSConfig__V1";
        } else if (config.contains("<uri>")) {
            // Use default class.
            this.defaultClassName = "eu.unifiedviews.plugins.extractor.filestoscp.FilesToScpConfig__V1";
        } else {
            throw new ConfigException("Can't detect configuratino class.");
        }
        // ...
        return super.transformString(configName, config);
    }



}
