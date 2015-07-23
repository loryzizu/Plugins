package eu.unifiedviews.plugins.transformer.unzipper;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

public class UnZipperVaadinDialog extends AbstractDialog<UnZipperConfig_V1> {

    private CheckBox checkDuplicityAvoid;
    
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

        checkDuplicityAvoid = new CheckBox(ctx.tr("unzipper.dialog.unzip.duplicity.avoid"));
        checkDuplicityAvoid.setDescription(ctx.tr("unzipper.dialog.unzip.duplicity.avoid.description"));
        mainLayout.addComponent(checkDuplicityAvoid);

        setCompositionRoot(mainLayout);
        
    }

    @Override
    protected void setConfiguration(UnZipperConfig_V1 c) throws DPUConfigException {
        // We have changed the description of filed in user GUI to negation of old description, so we have to negate the result
        // and we do not want to change config class
        checkDuplicityAvoid.setValue(!c.isNotPrefixed());
    }

    @Override
    protected UnZipperConfig_V1 getConfiguration() throws DPUConfigException {
        final UnZipperConfig_V1 cnf = new UnZipperConfig_V1();
        // We have changed the description of filed in user GUI to negation of old description, so we have to negate the result
        // and we do not want to change config class
        cnf.setNotPrefixed(checkDuplicityAvoid.getValue() == null ? true : !checkDuplicityAvoid.getValue());
        return cnf;
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder();

        if (checkDuplicityAvoid.getValue() == true) {
            // If true then we do not use prefixes.
            desc.append(ctx.tr("unzipper.dialog.unzip.duplicity.avoid.on"));
        } else {
            // If false prefix is not used.
            desc.append(ctx.tr("unzipper.dialog.unzip.duplicity.avoid.off"));
        }

        return desc.toString();
    }

}
