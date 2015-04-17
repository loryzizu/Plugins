package eu.unifiedviews.plugins.loader.rdftovirtuosoandckan;

import eu.unifiedviews.plugins.loader.rdftockan.RdfToCkanConfig_V1;
import eu.unifiedviews.plugins.loader.rdftovirtuoso.RdfToVirtuosoConfig_V1;

public class RdfToVirtuosoAndCkanConfig_V1 {

    RdfToVirtuosoConfig_V1 rdfToVirtuosoConfig_V1 = new RdfToVirtuosoConfig_V1();

    RdfToCkanConfig_V1 rdfToCkanConfig_V1 = new RdfToCkanConfig_V1();

    public RdfToVirtuosoConfig_V1 getRdfToVirtuosoConfig_V1() {
        return rdfToVirtuosoConfig_V1;
    }

    public void setRdfToVirtuosoConfig_V1(RdfToVirtuosoConfig_V1 rdfToVirtuosoConfig_V1) {
        this.rdfToVirtuosoConfig_V1 = rdfToVirtuosoConfig_V1;
    }

    public RdfToCkanConfig_V1 getRdfToCkanConfig_V1() {
        return rdfToCkanConfig_V1;
    }

    public void setRdfToCkanConfig_V1(RdfToCkanConfig_V1 rdfToCkanConfig_V1) {
        this.rdfToCkanConfig_V1 = rdfToCkanConfig_V1;
    }
}
