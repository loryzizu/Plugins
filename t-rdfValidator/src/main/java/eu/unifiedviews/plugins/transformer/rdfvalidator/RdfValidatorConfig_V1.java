package eu.unifiedviews.plugins.transformer.rdfvalidator;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.VersionedConfig;

/**
 * DPU's configuration class.
 */
public class RdfValidatorConfig_V1 implements VersionedConfig<RdfValidatorConfig_V2> {
    private boolean failExecution = false;

    private String query = "ASK {?s ?p ?p}";

    private boolean perGraph = true;

    private String outputGraphSymbolicName = "outputValidation";

    public RdfValidatorConfig_V1() {
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

    public boolean isPerGraph() {
        return perGraph;
    }

    public void setPerGraph(boolean perGraph) {
        this.perGraph = perGraph;
    }

    public String getOutputGraphSymbolicName() {
        return outputGraphSymbolicName;
    }

    public void setOutputGraphSymbolicName(String outputGraphSymbolicName) {
        this.outputGraphSymbolicName = outputGraphSymbolicName;
    }

    @Override
    public RdfValidatorConfig_V2 toNextVersion() throws DPUConfigException {
        RdfValidatorConfig_V2 result = new RdfValidatorConfig_V2();
        result.setFailExecution(failExecution);
        result.setQuery(query);
        return result;
    }
}
