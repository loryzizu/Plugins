package eu.unifiedviews.plugins.transformer.unzipper;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.VerticalLayout;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

/**
 * @author Škoda Petr
 */
public class UnZipperVaadinDialog extends BaseConfigDialog<UnZipperConfig_V1> {

    private CheckBox checkNotPrefix;

    public UnZipperVaadinDialog() {
        super(UnZipperConfig_V1.class);
        buildMainLayout();
    }

    private void buildMainLayout() {
        setWidth("100%");
        setHeight("100%");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");

        checkNotPrefix = new CheckBox(Messages.getString("dialog.unzip.noprefix"));
        checkNotPrefix.setDescription(Messages.getString("dialog.unzip.noprefix.description"));
        mainLayout.addComponent(checkNotPrefix);

        setCompositionRoot(mainLayout);
    }

    @Override
    protected void setConfiguration(UnZipperConfig_V1 c) throws DPUConfigException {
        checkNotPrefix.setValue(c.isNotPrefixed());
    }

    @Override
    protected UnZipperConfig_V1 getConfiguration() throws DPUConfigException {
        final UnZipperConfig_V1 cnf = new UnZipperConfig_V1();
        cnf.setNotPrefixed(checkNotPrefix.getValue() == null ? false : checkNotPrefix.getValue());
        return cnf;
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder();

        if (checkNotPrefix.getValue() == true) {
            // is true, then we do not use prefixes
            desc.append(Messages.getString("dialog.unzip.notprefixed"));
        } else {
            // if false prefix is not used
            desc.append(Messages.getString("dialog.unzip.prefixed"));
        }

        return desc.toString();
    }
}
