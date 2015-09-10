package eu.unifiedviews.plugins.extractor.sparqlendpoint;

import eu.unifiedviews.helpers.dpu.config.VersionedConfig;

/**
 * Configuration class for SparqlEndpoint.
 * 
 */
public class SparqlEndpointConfig_V1 implements VersionedConfig<SparqlEndpointConfig_V1> {

    private String endpoint = "http://localhost:8890/sparql";

    private String query = "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }";

    private Integer chunkSize = null;

    public SparqlEndpointConfig_V1() {

    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Integer chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Override
    public SparqlEndpointConfig_V1 toNextVersion() {
        // This is just a trick to enable use history with non-linear history, ie. many to one history.
        return this;
    }

}
