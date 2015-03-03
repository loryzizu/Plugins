package eu.unifiedviews.plugins.transformer.filesfindandreplace;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;
import eu.unifiedviews.helpers.dpu.config.InitializableConfigDialog;
import eu.unifiedviews.helpers.dpu.localization.Messages;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class FilesFindAndReplaceVaadinDialog extends BaseConfigDialog<FilesFindAndReplaceConfig_V1> implements InitializableConfigDialog {

    private static final long serialVersionUID = -5668436075836909428L;

    private ObjectProperty<String> search = new ObjectProperty<String>("");

    private ObjectProperty<String> replace = new ObjectProperty<String>("");

    private Messages messages;

    public FilesFindAndReplaceVaadinDialog() {
        super(FilesFindAndReplaceConfig_V1.class);
    }

    @Override
    public void initialize() {
        messages = new Messages(getContext().getLocale(), this.getClass().getClassLoader());

        FormLayout mainLayout = new FormLayout();

        // top-level component properties
        setWidth("100%");
        setHeight("100%");

        TextField txtSearch = new TextField(messages.getString("dialog.tlfs.search"), search);
        txtSearch.setWidth("100%");
        TextField txtReplace = new TextField(messages.getString("dialog.tlfs.replace"), replace);
        txtReplace.setWidth("100%");

        mainLayout.addComponent(txtSearch);
        mainLayout.addComponent(txtReplace);

        setCompositionRoot(mainLayout);
    }

    @Override
    public void setConfiguration(FilesFindAndReplaceConfig_V1 conf)
            throws DPUConfigException {
        for (Map.Entry<String, String> entry : conf.getPatterns().entrySet()) {
            search.setValue(StringEscapeUtils.escapeJava(entry.getKey()));
            replace.setValue(StringEscapeUtils.escapeJava(entry.getValue()));
        }
    }

    @Override
    public FilesFindAndReplaceConfig_V1 getConfiguration()
            throws DPUConfigException {
        FilesFindAndReplaceConfig_V1 conf = new FilesFindAndReplaceConfig_V1();
        Map<String, String> map = new HashMap<String, String>();
        map.put(StringEscapeUtils.unescapeJava(search.getValue()), StringEscapeUtils.unescapeJava(replace.getValue()));
        conf.setPatterns(map);

        return conf;
    }

    @Override
    public String getDescription() {
        return "s/" + search.getValue() + "/" + replace.getValue();
    }

}
