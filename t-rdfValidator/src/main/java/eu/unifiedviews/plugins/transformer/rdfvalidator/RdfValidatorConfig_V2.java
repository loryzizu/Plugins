package eu.unifiedviews.plugins.transformer.rdfvalidator;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.VersionedConfig;

/**
 * DPU's configuration class.
 */
public class RdfValidatorConfig_V2 implements VersionedConfig<RdfValidatorConfig_V2> {
    private boolean failExecution = false;

    private String query = "ASK {?s ?p ?p}";

    public RdfValidatorConfig_V2() {
    }

    public boolean isFailExecution() {
        return failExecution;
    }

    public void setFailExecution(boolean failExecution) {
        this.failExecution = failExecution;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public RdfValidatorConfig_V2 toNextVersion() throws DPUConfigException {
        return this;
    }
}
