package eu.unifiedviews.plugins.extractor.filesdownload;

import eu.unifiedviews.helpers.dpu.config.ConfigException;
import eu.unifiedviews.helpers.dpu.config.MasterConfigObject;
import eu.unifiedviews.helpers.dpu.config.migration.ConfigurationUpdate;

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

        if (config.contains("<retryDelay>")) {
            this.defaultClassName = "eu.unifiedviews.plugins.extractor.httpdownload.HttpDownloadConfig__V1";
        } else if (config.contains("<softFail>")) {
            this.defaultClassName = "cz.cuni.mff.xrg.uv.extractor.scp.FilesFromScpConfig__V1";

        } else {
            throw new ConfigException("Can't detect configuratino class.");
        }
        // ...
        return super.transformString(configName, config);
    }

}
