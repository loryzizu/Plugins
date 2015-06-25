package eu.unifiedviews.plugins.transformer.rdfvalidator;

/**
 * DPU's configuration class.
 */
public class RdfValidatorConfig_V1 {
    private boolean failExecution = false;

    private String query = "INSERT {?s ?p ?p} WHERE {?s ?p ?o}";

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
}
