package eu.unifiedviews.plugins.loader.rdftovirtuosoandckan;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;
import eu.unifiedviews.plugins.loader.rdftockan.RdfToCkanVaadinDialog;
import eu.unifiedviews.plugins.loader.rdftovirtuoso.RdfToVirtuosoVaadinDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class RdfToVirtuosoAndCkanVaadinDialog extends AbstractDialog<RdfToVirtuosoAndCkanConfig_V1> {

    private static final long serialVersionUID = -5666909428L;

    private RdfToVirtuosoVaadinDialog rdfToVirtuosoVaadinDialog;

    private RdfToCkanVaadinDialog rdfToCkanVaadinDialog;

    public RdfToVirtuosoAndCkanVaadinDialog() {
        super(RdfToVirtuosoAndCkan.class);
        rdfToVirtuosoVaadinDialog = new RdfToVirtuosoVaadinDialog();
        rdfToCkanVaadinDialog = new RdfToCkanVaadinDialog();
    }

    @Override
    protected void buildDialogLayout() {
        try {
            rdfToVirtuosoVaadinDialog.setContext(this.getContext());
            rdfToVirtuosoVaadinDialog.initialize();
//            rdfToVirtuosoVaadinDialog.outerBuildDialogLayout(ctx);
            rdfToCkanVaadinDialog.setContext(this.getContext());
            rdfToCkanVaadinDialog.initialize();
//            rdfToCkanVaadinDialog.outerBuildDialogLayout(ctx);
        } catch (DPUConfigException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        addTab(rdfToVirtuosoVaadinDialog, "L-RdfToVirtuoso");
        addTab(rdfToCkanVaadinDialog, "L-RdfToCkan");
    }

    @Override
    public void setConfiguration(RdfToVirtuosoAndCkanConfig_V1 conf) throws DPUConfigException {
        rdfToVirtuosoVaadinDialog.setConfiguration(conf.getRdfToVirtuosoConfig_V1());
        rdfToCkanVaadinDialog.setConfiguration(conf.getRdfToCkanConfig_V1());
    }

    @Override
    public RdfToVirtuosoAndCkanConfig_V1 getConfiguration() throws DPUConfigException {
        RdfToVirtuosoAndCkanConfig_V1 conf = new RdfToVirtuosoAndCkanConfig_V1();
        conf.setRdfToVirtuosoConfig_V1(rdfToVirtuosoVaadinDialog.getConfiguration());
        conf.setRdfToCkanConfig_V1(rdfToCkanVaadinDialog.getConfiguration());
        return conf;
    }

}
