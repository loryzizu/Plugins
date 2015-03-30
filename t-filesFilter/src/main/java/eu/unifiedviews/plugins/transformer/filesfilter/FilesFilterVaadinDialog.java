package eu.unifiedviews.plugins.transformer.filesfilter;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

public class FilesFilterVaadinDialog extends AbstractDialog<FilesFilterConfig_V1> {

    private TextField txtObject;

    private CheckBox checkUseRegExp;

    public FilesFilterVaadinDialog() {
        super(FilesFilter.class);
    }

    @Override
    protected void buildDialogLayout() {
        setSizeFull();

        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);

        txtObject = new TextField(ctx.tr("filesFilter.dialog.value"));
        txtObject.setWidth("100%");
        txtObject.setRequired(true);
        mainLayout.addComponent(txtObject);

        checkUseRegExp = new CheckBox(ctx.tr("filesFilter.dialog.regexp"));
        checkUseRegExp.setDescription(ctx.tr("filesFilter.dialog.regexp.desc"));
        mainLayout.addComponent(checkUseRegExp);

        setCompositionRoot(mainLayout);
    }

    @Override
    protected void setConfiguration(FilesFilterConfig_V1 c) throws DPUConfigException {
        txtObject.setValue(c.getObject());
        checkUseRegExp.setValue(c.isUseRegExp());
    }

    @Override
    protected FilesFilterConfig_V1 getConfiguration() throws DPUConfigException {
        if (!txtObject.isValid()) {
            throw new DPUConfigException(ctx.tr("filesFilter.dialog.constrain.fields.all"));
        }

        final FilesFilterConfig_V1 cnf = new FilesFilterConfig_V1();
        cnf.setObject(txtObject.getValue());
        cnf.setUseRegExp(checkUseRegExp.getValue());
        return cnf;
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder();
        if (checkUseRegExp.getValue()) {
            desc.append(ctx.tr("filesFilter.dialog.desc.regExp"));
        } else {
            desc.append(ctx.tr("filesFilter.dialog.desc.strict"));
        }
        desc.append(txtObject.getValue());
        return desc.toString();
    }

}
