package eu.unifiedviews.plugins.transformer.rdfgraphmerger;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

/**
 * DPU's configuration dialog.
 */
public class RdfGraphMergerVaadinDialog extends AbstractDialog<RdfGraphMergerConfig_V1> {

    public RdfGraphMergerVaadinDialog() {
        super(RdfGraphMerger.class);
    }

    @Override
    public void setConfiguration(RdfGraphMergerConfig_V1 c) throws DPUConfigException {

    }

    @Override
    public RdfGraphMergerConfig_V1 getConfiguration() throws DPUConfigException {
        final RdfGraphMergerConfig_V1 c = new RdfGraphMergerConfig_V1();

        return c;
    }

    @Override
    protected void buildDialogLayout() {
    }

}
