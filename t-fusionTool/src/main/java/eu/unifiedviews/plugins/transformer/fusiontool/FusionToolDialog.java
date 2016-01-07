package eu.unifiedviews.plugins.transformer.fusiontool;

import com.vaadin.data.Validator;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;
import eu.unifiedviews.plugins.transformer.fusiontool.config.ConfigReader;
import eu.unifiedviews.plugins.transformer.fusiontool.exceptions.InvalidInputException;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 * 
 * @author Jan Michelfeit
 */
public class FusionToolDialog extends AbstractDialog<FusionToolConfig> {

    private static final long serialVersionUID = 1L;

    private TextArea configTextArea;

    private Label labelUpQuer;

    private String lastValidationError = "";

    /**
     * Initializes a new instance of the class.
     */
    public FusionToolDialog() {
        super(FusionToolDpu.class);
    }

    @Override
    public void setConfiguration(FusionToolConfig conf)
            throws DPUConfigException {
        configTextArea.setValue(conf.getXmlConfig());
    }

    @Override
    public FusionToolConfig getConfiguration() throws DPUConfigException {
        if (!configTextArea.isValid()) {
            throw new DPUConfigException("Invalid configuration: " + lastValidationError);
        } else {
            FusionToolConfig conf = new FusionToolConfig(configTextArea.getValue().trim());
            return conf;
        }
    }

    @Override
    public String getToolTip() {
        return super.getToolTip();
    }

    @Override
    public String getDescription() {
        return super.getDescription();
    }

    @Override
    protected void buildDialogLayout() {

        // common part: create layout
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");

        // top-level component properties
        setWidth("100%");
        setHeight("100%");

        // labelUpQuer
        labelUpQuer = new Label();
        labelUpQuer.setImmediate(false);
        labelUpQuer.setValue("Configuration");
        mainLayout.addComponent(labelUpQuer);

        // Configuration textArea
        configTextArea = new TextArea();

        configTextArea.addValidator(new com.vaadin.data.Validator() {
            private static final long serialVersionUID = 1L;

            @Override
            public void validate(Object value) throws Validator.InvalidValueException {
                try {
                    ConfigReader.parseConfigXml(value.toString());
                } catch (InvalidInputException e) {
                    String message = "Invalid XML configuration: " + e.getMessage();
                    lastValidationError = message;
                    throw new Validator.InvalidValueException(message);
                }
            }
        });

        // configTextArea.setNullRepresentation("");
        configTextArea.setImmediate(true);
        configTextArea.setWidth("100%");
        configTextArea.setHeight("300px");
        configTextArea.setInputPrompt("<?xml version=\"1.0\"?>\n<config>\n</config>");

        mainLayout.addComponent(configTextArea);

        setCompositionRoot(mainLayout);

    }

}
