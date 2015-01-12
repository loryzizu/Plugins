package eu.unifiedviews.plugins.transformer.zipper;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;
import eu.unifiedviews.helpers.dpu.config.InitializableConfigDialog;
import eu.unifiedviews.helpers.dpu.localization.Messages;

public class ZipperVaadinDialog extends BaseConfigDialog<ZipperConfig_V1> implements InitializableConfigDialog {

    private VerticalLayout mainLayout;

    private TextField txtZipFile;
    
    private Messages messages;

    public ZipperVaadinDialog() {
        super(ZipperConfig_V1.class);
    }
    
    @Override
    public void initialize() {
        this.messages = new Messages(getContext().getLocale(), this.getClass().getClassLoader());
        
        setWidth("100%");
        setHeight("100%");

        mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");

        txtZipFile = new TextField(messages.getString("dialog.zip.filename"));
        txtZipFile.setWidth("100%");
        txtZipFile.setRequired(true);
        mainLayout.addComponent(txtZipFile);

        mainLayout.addComponent(new Label(messages.getString("dialog.zip.filename.specification"), ContentMode.HTML));

        setCompositionRoot(mainLayout);
    }

    @Override
    protected void setConfiguration(ZipperConfig_V1 c) throws DPUConfigException {
        txtZipFile.setValue(c.getZipFile());
    }

    @Override
    protected ZipperConfig_V1 getConfiguration() throws DPUConfigException {
        if (!txtZipFile.isValid()) {
            throw new DPUConfigException(messages.getString("dialog.zip.valid.input.filename"));
        }
        ZipperConfig_V1 cnf = new ZipperConfig_V1();
        cnf.setZipFile(txtZipFile.getValue());
        return cnf;
    }

    @Override
    public String getDescription() {
        final StringBuilder desc = new StringBuilder();

        desc.append(messages.getString("dialog.zip.description"));
        desc.append(txtZipFile.getValue());

        return desc.toString();
    }

}
