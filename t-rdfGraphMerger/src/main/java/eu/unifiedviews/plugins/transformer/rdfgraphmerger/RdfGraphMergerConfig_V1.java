package eu.unifiedviews.plugins.transformer.rdfgraphmerger;

import java.net.URI;

/**
 * DPU's configuration class.
 */
public class RdfGraphMergerConfig_V1 {

    private URI virtualGraph;

    public RdfGraphMergerConfig_V1() {

    }

    public URI getVirtualGraph() {
        return virtualGraph;
    }

    public void setVirtualGraph(URI virtualGraph) {
        this.virtualGraph = virtualGraph;
    }
}
