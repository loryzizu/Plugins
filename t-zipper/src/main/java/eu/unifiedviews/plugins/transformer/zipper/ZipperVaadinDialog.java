package eu.unifiedviews.plugins.transformer.zipper;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

public class ZipperVaadinDialog extends BaseConfigDialog<ZipperConfig_V1> {

    private VerticalLayout mainLayout;

    private TextField txtZipFile;

    public ZipperVaadinDialog() {
        super(ZipperConfig_V1.class);
        buildMainLayout();
    }

    private void buildMainLayout() {
        setWidth("100%");
        setHeight("100%");

        mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");

        txtZipFile = new TextField(Messages.getString("dialog.zip.filename"));
        txtZipFile.setWidth("100%");
        txtZipFile.setRequired(true);
        mainLayout.addComponent(txtZipFile);

        mainLayout.addComponent(new Label(Messages.getString("dialog.zip.filename.specification"), ContentMode.HTML));

        setCompositionRoot(mainLayout);
    }

    @Override
    protected void setConfiguration(ZipperConfig_V1 c) throws DPUConfigException {
        txtZipFile.setValue(c.getZipFile());
    }

    @Override
    protected ZipperConfig_V1 getConfiguration() throws DPUConfigException {
        if (!txtZipFile.isValid()) {
            throw new DPUConfigException(Messages.getString("dialog.zip.valid.input.filename"));
        }
        ZipperConfig_V1 cnf = new ZipperConfig_V1();
        cnf.setZipFile(txtZipFile.getValue());
        return cnf;
    }

    @Override
    public String getDescription() {
        final StringBuilder desc = new StringBuilder();

        desc.append(Messages.getString("dialog.zip.description"));
        desc.append(txtZipFile.getValue());

        return desc.toString();
    }

}
