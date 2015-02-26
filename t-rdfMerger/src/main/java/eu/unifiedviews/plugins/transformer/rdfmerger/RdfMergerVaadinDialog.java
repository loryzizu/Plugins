package eu.unifiedviews.plugins.transformer.rdfmerger;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

/**
 * Vaadin configuration dialog for RdfMerger.
 *
 * @author Petr Škoda
 */
public class RdfMergerVaadinDialog extends AbstractDialog<RdfMergerConfig_V1> {

    public RdfMergerVaadinDialog() {
        super(RdfMerger.class);
    }

    @Override
    public void setConfiguration(RdfMergerConfig_V1 c) throws DPUConfigException {

    }

    @Override
    public RdfMergerConfig_V1 getConfiguration() throws DPUConfigException {
        final RdfMergerConfig_V1 c = new RdfMergerConfig_V1();

        return c;
    }

    @Override
    public void buildDialogLayout() {

    }

}
