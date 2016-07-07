package eu.unifiedviews.plugins.quality.sparqlask;

import eu.unifiedviews.dpu.DPUContext;

/**
 * DPU's configuration class.
 */
public class SparqlAskConfig_V1 {

    public static final String AUTO_MESSAGE = "Outputted RDF data does not satisfy SPARQL ASK constraint";

    private DPUContext.MessageType messageType = DPUContext.MessageType.ERROR;

    private String message = null;

    private String askQuery = "ASK { ?s ?p ?o }";

    private boolean perGraph = true;

    public SparqlAskConfig_V1() {

    }

    public DPUContext.MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(DPUContext.MessageType messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAskQuery() {
        return askQuery;
    }

    public void setAskQuery(String askQuery) {
        this.askQuery = askQuery;
    }

    public boolean isPerGraph() {
        return perGraph;
    }

    public void setPerGraph(boolean perGraph) {
        this.perGraph = perGraph;
    }

}
