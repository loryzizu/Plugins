package eu.unifiedviews.plugins.transformer.filesrenamer;

import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.cuni.dpu.vaadin.AbstractDialog;

public class RenamerVaadinDialog extends AbstractDialog<RenamerConfig_V1> {

    private TextField txtPattern;

    private TextField txtValue;

    public RenamerVaadinDialog() {
        super(Renamer.class);
    }

    @Override
    protected void buildDialogLayout() {

        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.setMargin(true);

        txtPattern = new TextField(ctx.tr("renamer.dialog.regExp"));
        txtPattern.setWidth("100%");
        layout.addComponent(txtPattern);

        txtValue = new TextField(ctx.tr("renamer.dialog.replaceWith"));
        txtValue.setWidth("100%");
        layout.addComponent(txtValue);

        setCompositionRoot(layout);
    }

    @Override
    public void setConfiguration(RenamerConfig_V1 config) throws DPUConfigException {
        txtPattern.setValue(config.getPattern());
        txtValue.setValue(config.getReplaceText());

    }

    @Override
    public RenamerConfig_V1 getConfiguration() throws DPUConfigException {
        RenamerConfig_V1 c = new RenamerConfig_V1();

        c.setReplaceText(txtValue.getValue());
        c.setPattern(txtPattern.getValue());

        return c;
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder();

        return desc.toString();
    }

}
