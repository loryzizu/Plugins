package eu.unifiedviews.plugins.transformer.gunzipper;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

/**
 * Vaadin configuration dialog for FilesMerger.
 */
public class GunzipperVaadinDialog extends AbstractDialog<GunzipperConfig_V1> {
    /**
     * 
     */
    private static final long serialVersionUID = 4297666014030908423L;
    private ObjectProperty<Boolean> skipOnError = new ObjectProperty<Boolean>(
            false);

    public GunzipperVaadinDialog() {
        super(Gunzipper.class);
    }

    @Override
    public void buildDialogLayout() {
        setSizeFull();
        FormLayout mainLayout = new FormLayout();

        // top-level component properties
        setWidth("100%");
        setHeight("100%");
        mainLayout.addComponent(new CheckBox(ctx.tr("GunzipperVaadinDialog.skipOnError"), skipOnError));
        setCompositionRoot(mainLayout);
    }

    @Override
    public void setConfiguration(GunzipperConfig_V1 conf) throws DPUConfigException {
        skipOnError.setValue(conf.isSkipOnError());
    }

    @Override
    public GunzipperConfig_V1 getConfiguration() throws DPUConfigException {
        final GunzipperConfig_V1 conf = new GunzipperConfig_V1();
        conf.setSkipOnError(skipOnError.getValue());
        return conf;
    }

}
