package eu.unifiedviews.plugins.transformer.gunzipper;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

/**
 * Vaadin configuration dialog for FilesMerger.
 *
 */
public class GunzipperVaadinDialog extends AbstractDialog<GunzipperConfig_V1> {

    public GunzipperVaadinDialog() {
        super(Gunzipper.class);
    }

    @Override
    public void setConfiguration(GunzipperConfig_V1 c) throws DPUConfigException {

    }

    @Override
    public GunzipperConfig_V1 getConfiguration() throws DPUConfigException {
        final GunzipperConfig_V1 c = new GunzipperConfig_V1();

        return c;
    }

    @Override
    public void buildDialogLayout() {
    }

}
