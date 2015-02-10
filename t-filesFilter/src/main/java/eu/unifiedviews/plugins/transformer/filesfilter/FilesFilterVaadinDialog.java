package eu.unifiedviews.plugins.transformer.filesfilter;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;
import eu.unifiedviews.helpers.dpu.config.InitializableConfigDialog;
import eu.unifiedviews.helpers.dpu.localization.Messages;

public class FilesFilterVaadinDialog extends BaseConfigDialog<FilesFilterConfig_V1> implements InitializableConfigDialog {

    private static final int OPTION_SYMBOLIC_NAME = 1;

    private static final int OPTION_VIRTUAL_PATH = 2;

    private VerticalLayout mainLayout;

    private OptionGroup optType;

    private TextField txtObject;

    private CheckBox checkUseRegExp;

    private Messages messages;

    public FilesFilterVaadinDialog() {
        super(FilesFilterConfig_V1.class);
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
        mainLayout.setSpacing(true);

        optType = new OptionGroup(messages.getString("FilesFilterVaadinDialog.filter.used"));
        optType.addItem(OPTION_SYMBOLIC_NAME);
        optType.setItemCaption(OPTION_SYMBOLIC_NAME, messages.getString("FilesFilterVaadinDialog.name.symbolic"));

        optType.addItem(OPTION_VIRTUAL_PATH);
        optType.setItemCaption(OPTION_VIRTUAL_PATH, messages.getString("FilesFilterVaadinDialog.path.virtual"));

        mainLayout.addComponent(optType);

        txtObject = new TextField(messages.getString("FilesFilterVaadinDialog.predicate.custom"));
        txtObject.setWidth("100%");
        txtObject.setRequired(true);
        mainLayout.addComponent(txtObject);

        checkUseRegExp = new CheckBox(messages.getString("FilesFilterVaadinDialog.regex.use"));
        mainLayout.addComponent(checkUseRegExp);

        setCompositionRoot(mainLayout);
    }

    @Override
    protected void setConfiguration(FilesFilterConfig_V1 c) throws DPUConfigException {

        if (c.getPredicate().compareTo(FilesFilterConfig_V1.SYMBOLIC_NAME) == 0) {
            optType.setValue(OPTION_SYMBOLIC_NAME);
        } else {
            // TODO We can be more save here ..
            optType.setValue(OPTION_VIRTUAL_PATH);
        }

        txtObject.setValue(c.getObject());
        checkUseRegExp.setValue(c.isUseRegExp());
    }

    @Override
    protected FilesFilterConfig_V1 getConfiguration() throws DPUConfigException {
        if (!txtObject.isValid()) {
            throw new DPUConfigException(messages.getString("FilesFilterVaadinDialog.constrain.fields.all"));
        }

        final FilesFilterConfig_V1 cnf = new FilesFilterConfig_V1();
        final Integer selected = (Integer) optType.getValue();
        switch (selected) {
            case OPTION_SYMBOLIC_NAME:
                cnf.setPredicate(FilesFilterConfig_V1.SYMBOLIC_NAME);
                break;
            case OPTION_VIRTUAL_PATH:
                cnf.setPredicate(FilesFilterConfig_V1.VIRTUAL_PATH);
                break;
            default:
                throw new DPUConfigException(messages.getString("FilesFilterVaadinDialog.dialog.broken"));
        }
        cnf.setObject(txtObject.getValue());
        cnf.setUseRegExp(checkUseRegExp.getValue());
        return cnf;
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder();

        desc.append(messages.getString("FilesFilterVaadinDialog.filterBy"));

        final Integer selected = (Integer) optType.getValue();
        switch (selected) {
            case OPTION_SYMBOLIC_NAME:
                desc.append(messages.getString("FilesFilterVaadinDialog.desc.name.symbolic"));
                break;
            case OPTION_VIRTUAL_PATH:
                desc.append(messages.getString("FilesFilterVaadinDialog.desc.path.virtual"));
                break;
        }

        desc.append(" " + messages.getString("FilesFilterVaadinDialog.desc.for") + " ");
        desc.append(txtObject.getValue());

        return desc.toString();
    }

}
