package eu.unifiedviews.plugins.transformer.gzipper;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

/**
 * Vaadin configuration dialog.
 */
public class GzipperVaadinDialog extends AbstractDialog<GzipperConfig_V1> {
    /**
     * 
     */
    private static final long serialVersionUID = -1539890500190040L;

    private ObjectProperty<Boolean> skipOnError = new ObjectProperty<Boolean>(false);

    public GzipperVaadinDialog() {
        super(Gzipper.class);
    }

    @Override
    protected void buildDialogLayout() {
        setSizeFull();
        FormLayout mainLayout = new FormLayout();

        // top-level component properties
        setWidth("100%");
        setHeight("100%");
        mainLayout.addComponent(new CheckBox(ctx.tr("GzipperVaadinDialog.skipOnError"), skipOnError));
        setCompositionRoot(mainLayout);
    }

    @Override
    protected void setConfiguration(GzipperConfig_V1 conf) throws DPUConfigException {
        skipOnError.setValue(conf.isSkipOnError());
    }

    @Override
    protected GzipperConfig_V1 getConfiguration() throws DPUConfigException {
        final GzipperConfig_V1 conf = new GzipperConfig_V1();
        conf.setSkipOnError(skipOnError.getValue());
        return conf;
    }
}
