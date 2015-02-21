package eu.unifiedviews.plugins.transformer.zipper;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.helpers.cuni.dpu.vaadin.AbstractDialog;
import eu.unifiedviews.dpu.config.DPUConfigException;

public class ZipperVaadinDialog extends AbstractDialog<ZipperConfig_V1> {

    private VerticalLayout mainLayout;

    private TextField txtZipFile;

    public ZipperVaadinDialog() {
        super(Zipper.class);
    }

    @Override
    protected void buildDialogLayout() {
        this.setSizeFull();

        mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        txtZipFile = new TextField(ctx.tr("zipper.dialog.zip.filename"));
        txtZipFile.setWidth("100%");
        txtZipFile.setRequired(true);
        mainLayout.addComponent(txtZipFile);

        mainLayout.addComponent(new Label(ctx.tr("zipper.dialog.zip.filename.specification"), ContentMode.HTML));

        setCompositionRoot(mainLayout);
    }

    @Override
    protected void setConfiguration(ZipperConfig_V1 c) throws DPUConfigException {
        txtZipFile.setValue(c.getZipFile());
    }

    @Override
    protected ZipperConfig_V1 getConfiguration() throws DPUConfigException {
        if (!txtZipFile.isValid()) {
            throw new DPUConfigException(ctx.tr("zipper.dialog.zip.valid.input.filename"));
        }
        ZipperConfig_V1 cnf = new ZipperConfig_V1();
        cnf.setZipFile(txtZipFile.getValue());
        return cnf;
    }

    @Override
    public String getDescription() {
        final StringBuilder desc = new StringBuilder();

        desc.append(ctx.tr("zipper.dialog.zip.description"));
        desc.append(txtZipFile.getValue());

        return desc.toString();
    }

}
