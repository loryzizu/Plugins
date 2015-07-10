package eu.unifiedviews.plugins.transformer.unzipper;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

public class UnZipperVaadinDialog extends AbstractDialog<UnZipperConfig_V1> {

    private CheckBox checkNotPrefix;
    
    public UnZipperVaadinDialog() {
        super(UnZipper.class);
    }

    @Override
    public void buildDialogLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setMargin(true);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");

        checkNotPrefix = new CheckBox(ctx.tr("unzipper.dialog.unzip.noprefix"));
        checkNotPrefix.setDescription(ctx.tr("unzipper.dialog.unzip.noprefix.description"));
        mainLayout.addComponent(checkNotPrefix);

        setCompositionRoot(mainLayout);
        
    }

    @Override
    protected void setConfiguration(UnZipperConfig_V1 c) throws DPUConfigException {
        checkNotPrefix.setValue(!c.isNotPrefixed());
    }

    @Override
    protected UnZipperConfig_V1 getConfiguration() throws DPUConfigException {
        final UnZipperConfig_V1 cnf = new UnZipperConfig_V1();
        cnf.setNotPrefixed(checkNotPrefix.getValue() == null ? false : !checkNotPrefix.getValue());
        return cnf;
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder();

        if (checkNotPrefix.getValue() == true) {
            // If true then we do not use prefixes.
            desc.append(ctx.tr("unzipper.dialog.unzip.notprefixed"));
        } else {
            // If false prefix is not used.
            desc.append(ctx.tr("unzipper.dialog.unzip.prefixed"));
        }

        return desc.toString();
    }

}
