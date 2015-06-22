package eu.unifiedviews.plugins.transformer.filesfindandreplace;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import org.apache.commons.lang3.StringEscapeUtils;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class FilesFindAndReplaceVaadinDialog extends AbstractDialog<FilesFindAndReplaceConfig_V2> {

    private static final long serialVersionUID = -5668436075836909428L;

    private ObjectProperty<String> search = new ObjectProperty<String>("");

    private ObjectProperty<String> replace = new ObjectProperty<String>("");

    private NativeSelect encodingSelect;

    public FilesFindAndReplaceVaadinDialog() {
        super(FilesFindAndReplace.class);
    }

    @Override
    protected void buildDialogLayout() {
        FormLayout mainLayout = new FormLayout();

        // top-level component properties
        setWidth("100%");
        setHeight("100%");
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        encodingSelect = new NativeSelect(ctx.tr("dialog.tlfs.encoding"));
        for(Encoding encoding : Encoding.values()) {
            encodingSelect.addItem(encoding);
            encodingSelect.setItemCaption(encoding, encoding.getCharset());
        }
        encodingSelect.setNullSelectionAllowed(false);
        encodingSelect.setImmediate(true);
        TextField txtSearch = new TextField(this.ctx.tr("dialog.tlfs.search"), search);
        txtSearch.setWidth("100%");
        TextField txtReplace = new TextField(this.ctx.tr("dialog.tlfs.replace"), replace);
        txtReplace.setWidth("100%");

        mainLayout.addComponent(encodingSelect);
        mainLayout.addComponent(txtSearch);
        mainLayout.addComponent(txtReplace);

        setCompositionRoot(mainLayout);
    }

    @Override
    public void setConfiguration(FilesFindAndReplaceConfig_V2 conf)
            throws DPUConfigException {
        for (Map.Entry<String, String> entry : conf.getPatterns().entrySet()) {
            search.setValue(StringEscapeUtils.escapeJava(entry.getKey()));
            replace.setValue(StringEscapeUtils.escapeJava(entry.getValue()));
        }
        encodingSelect.setValue(conf.getEncoding());
    }

    @Override
    public FilesFindAndReplaceConfig_V2 getConfiguration()
            throws DPUConfigException {
        FilesFindAndReplaceConfig_V2 conf = new FilesFindAndReplaceConfig_V2();
        Encoding encoding = (Encoding) encodingSelect.getValue();
        conf.setEncoding(encoding);
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
