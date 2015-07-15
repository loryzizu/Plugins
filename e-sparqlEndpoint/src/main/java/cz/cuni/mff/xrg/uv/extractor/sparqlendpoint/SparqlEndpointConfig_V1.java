package cz.cuni.mff.xrg.uv.extractor.sparqlendpoint;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.VersionedConfig;

/**
 * Configuration class for SparqlEndpoint.
 *
 * @author Petr Škoda
 */
public class SparqlEndpointConfig_V1 implements VersionedConfig<eu.unifiedviews.plugins.extractor.sparqlendpoint.SparqlEndpointConfig_V1> {

    private String endpoint = "http://localhost:8890/sparql";

    private String query = "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }";

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

    @Override
    public eu.unifiedviews.plugins.extractor.sparqlendpoint.SparqlEndpointConfig_V1 toNextVersion() throws DPUConfigException {
        eu.unifiedviews.plugins.extractor.sparqlendpoint.SparqlEndpointConfig_V1 c = new eu.unifiedviews.plugins.extractor.sparqlendpoint.SparqlEndpointConfig_V1();
        c.setEndpoint(endpoint);
        c.setQuery(query);
        return c;
    }

}
