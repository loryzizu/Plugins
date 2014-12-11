package eu.unifiedviews.plugins.dputemplate;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class DPUTemplateVaadinDialog extends BaseConfigDialog<DPUTemplateConfig_V1> {

    /**
     * Lets define some label for our text box
     */
    private static final String OUTPUT_FILENAME_LABEL = "Output filename (symbolicName and virtualPath)";

    /**
     * Define Vaadin data binding property for String type, the default value is irrelevant, we reset the value immediately
     * after creation in {@link #setConfiguration(DPUTemplateConfig_V1)} method (called by container proccess).
     *
     */
    private ObjectProperty<String> outputFilename = new ObjectProperty<String>("");

    public DPUTemplateVaadinDialog() {
        super(DPUTemplateConfig_V1.class);
        initialize();
    }

    private void initialize() {
        /**
         * Simple layout suitable for simple forms
         */
        FormLayout mainLayout = new FormLayout();

        /**
         * Be nice, fill the width of window please
         */
        setWidth("100%");
        setHeight("100%");

        /**
         * Create our text box, use the label and data binding property
         */
        TextField txtOutputFilename = new TextField(OUTPUT_FILENAME_LABEL, outputFilename);
        /**
         * Be nice, fill the width of window please
         */
        txtOutputFilename.setWidth("100%");

        /**
         * Add the text box to main layout
         */
        mainLayout.addComponent(txtOutputFilename);
        /**
         * This is Vaadin CustomComponent, we have to set compositionRoot
         */
        setCompositionRoot(mainLayout);
    }

    /**
     * Here we update dialog according to provided configuration object
     */
    @Override
    public void setConfiguration(DPUTemplateConfig_V1 conf) throws DPUConfigException {
        /**
         * Update our Vaadin data binding property (notice we do not work with TextField component at all, just data binding)
         */
        outputFilename.setValue(conf.getOutputFilename());
    }

    /**
     * Here we update the configuration object, filling the values user provided in dialog (called on Save action)
     */
    @Override
    public DPUTemplateConfig_V1 getConfiguration() throws DPUConfigException {
        /**
         * Create new empty (and with defaults set) config object
         */
        DPUTemplateConfig_V1 conf = new DPUTemplateConfig_V1();
        /**
         * Set our property on config object, get the new value from Vaadin data binding object (input by user in text box)
         */
        conf.setOutputFilename(outputFilename.getValue());
        /**
         * Return new configuration to be saved for this DPU instance
         */
        return conf;
    }

}
