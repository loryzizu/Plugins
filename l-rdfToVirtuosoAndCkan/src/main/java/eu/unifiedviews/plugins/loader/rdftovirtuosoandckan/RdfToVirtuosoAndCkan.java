package eu.unifiedviews.plugins.loader.rdftovirtuosoandckan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.plugins.loader.rdftockan.RdfToCkan;
import eu.unifiedviews.plugins.loader.rdftovirtuoso.RdfToVirtuoso;

@DPU.AsLoader
public class RdfToVirtuosoAndCkan extends AbstractDpu<RdfToVirtuosoAndCkanConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(RdfToVirtuosoAndCkan.class);

    @DataUnit.AsInput(name = "rdfInput")
    public RDFDataUnit rdfInput;
    
    @DataUnit.AsOutput(name = "rdfIntermediate")
    public WritableRDFDataUnit rdfIntermediate;

    public RdfToVirtuosoAndCkan() {
        super(RdfToVirtuosoAndCkanVaadinDialog.class, ConfigHistory.noHistory(RdfToVirtuosoAndCkanConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {
        RdfToVirtuoso rdfToVirtuoso = new RdfToVirtuoso();
        rdfToVirtuoso.rdfInput = rdfInput;
        rdfToVirtuoso.rdfOutput = rdfIntermediate;
        rdfToVirtuoso.outerExecute(ctx, config.getRdfToVirtuosoConfig_V1());
        
        RdfToCkan rdfToCkan = new RdfToCkan();
        rdfToCkan.rdfInput = rdfIntermediate;
        rdfToCkan.outerExecute(ctx, config.getRdfToCkanConfig_V1());
    }

}
