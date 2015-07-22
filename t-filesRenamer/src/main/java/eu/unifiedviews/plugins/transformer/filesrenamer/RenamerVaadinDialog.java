package eu.unifiedviews.plugins.transformer.filesrenamer;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

public class RenamerVaadinDialog extends AbstractDialog<RenamerConfig_V1> {

    private TextField txtPattern;

    private TextField txtValue;

    private CheckBox checkUseAdvanced;

    public RenamerVaadinDialog() {
        super(Renamer.class);
    }

    @Override
    protected void buildDialogLayout() {

        final VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.setHeight("-1px");
        layout.setSpacing(true);
        layout.setMargin(true);

        txtPattern = new TextField(ctx.tr("renamer.dialog.regExp"));
        txtPattern.setWidth("100%");
        layout.addComponent(txtPattern);

        txtValue = new TextField(ctx.tr("renamer.dialog.replaceWith"));
        txtValue.setWidth("100%");
        layout.addComponent(txtValue);

        checkUseAdvanced = new CheckBox(ctx.tr("renamer.dialog.advancedMode"));
        checkUseAdvanced.setWidth("100%");
        checkUseAdvanced.setDescription(ctx.tr("renamer.dialog.advancedMode.desc"));
        layout.addComponent(checkUseAdvanced);

        setCompositionRoot(layout);
    }

    @Override
    public void setConfiguration(RenamerConfig_V1 config) throws DPUConfigException {
        txtPattern.setValue(config.getPattern());
        txtValue.setValue(config.getReplaceText());
        checkUseAdvanced.setValue(config.isUseAdvanceReplace());

    }

    @Override
    public RenamerConfig_V1 getConfiguration() throws DPUConfigException {
        RenamerConfig_V1 c = new RenamerConfig_V1();

        c.setReplaceText(txtValue.getValue());
        c.setPattern(txtPattern.getValue());
        c.setUseAdvanceReplace(checkUseAdvanced.getValue());

        return c;
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder();

        return desc.toString();
    }

}
