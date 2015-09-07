package eu.unifiedviews.plugins.transformer.relationaltordf;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;
import eu.unifiedviews.plugins.transformer.relationaltordf.column.ColumnInfo_V1;

public class RelationalToRdfVaadinDialog extends AbstractDialog<RelationalToRdfConfig_V1> {

    private static final long serialVersionUID = -5440845675048528093L;

    private TextField txtBaseUri;

    private TextField txtKeyColumnName;

    private TextField txtRowsClass;

    private CheckBox checkGenerateNew;

    private CheckBox checkIgnoreBlankCell;

    private CheckBox checkAdvancedKeyColumn;

    private CheckBox checkGenerateRowTriple;

    private CheckBox checkTableSubject;

    private CheckBox checkAutoAsString;

    private CheckBox checkGenerateTableClass;

    private CheckBox checkGenerateLabels;

    /**
     * Layout for basic column mapping.
     */
    private GridLayout basicLayout;

    private final List<PropertyGroup> basicMapping = new ArrayList<>();

    private VerticalLayout mainLayout;

    public RelationalToRdfVaadinDialog() {
        super(RelationalToRdf.class);
    }

    @Override
    protected void buildDialogLayout() {
        setWidth("100%");
        setHeight("100%");

        this.mainLayout = new VerticalLayout();
        this.mainLayout.setImmediate(false);
        this.mainLayout.setWidth("100%");
        this.mainLayout.setHeight("-1px");
        this.mainLayout.setSpacing(true);
        this.mainLayout.setMargin(true);

        this.txtBaseUri = new TextField(this.ctx.tr("dialog.base.uri"));
        this.txtBaseUri.setWidth("100%");
        this.txtBaseUri.setRequired(true);
        this.txtBaseUri.setRequiredError(this.ctx.tr("dialog.base.uri.required"));
        this.txtBaseUri.setDescription(this.ctx.tr("dialog.base.uri.description"));
        this.mainLayout.addComponent(this.txtBaseUri);

        this.txtKeyColumnName = new TextField(this.ctx.tr("dialog.column.key"));
        this.txtKeyColumnName.setNullRepresentation("");
        this.txtKeyColumnName.setNullSettingAllowed(true);
        this.txtKeyColumnName.setWidth("100%");
        this.txtKeyColumnName.setDescription(this.ctx.tr("dialog.column.key.description"));
        this.mainLayout.addComponent(this.txtKeyColumnName);

        this.txtRowsClass = new TextField(this.ctx.tr("dialog.row.class"));
        this.txtRowsClass.setDescription(this.ctx.tr("dialog.row.class.description"));
        this.txtRowsClass.setWidth("100%");
        this.txtRowsClass.setNullRepresentation("");
        this.txtRowsClass.setNullSettingAllowed(true);
        this.mainLayout.addComponent(this.txtRowsClass);

        buildCheckBoxLayout();

        // --------------------- Mapping - simple ---------------------
        this.basicLayout = new GridLayout(5, 1);
        this.basicLayout.setWidth("100%");
        this.basicLayout.setHeight("-1px");
        this.basicLayout.setImmediate(true);
        this.basicLayout.setSpacing(true);
        this.basicLayout.setMargin(true);

        //  add headers
        this.basicLayout.addComponent(new Label(this.ctx.tr("dialog.mapping.name")));
        this.basicLayout.setColumnExpandRatio(0, 0.3f);

        this.basicLayout.addComponent(new Label(this.ctx.tr("dialog.mapping.type")));
        this.basicLayout.setColumnExpandRatio(1, 0.0f);

        this.basicLayout.addComponent(new Label(this.ctx.tr("dialog.mapping.language")));
        this.basicLayout.setColumnExpandRatio(2, 0.0f);

        this.basicLayout.addComponent(new Label(this.ctx.tr("dialog.mapping.use.dbf.types")));
        this.basicLayout.setColumnExpandRatio(3, 0.0f);

        this.basicLayout.addComponent(new Label(this.ctx.tr("dialog.mapping.property.uri")));
        this.basicLayout.setColumnExpandRatio(4, 0.7f);

        addSimplePropertyMapping(null, null);
        this.mainLayout.addComponent(this.basicLayout);

        final Button btnAddMapping = new Button(this.ctx.tr("dialog.mapping.button.add"));
        btnAddMapping.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                // add empty line with mapping
                addSimplePropertyMapping(null, null);
            }
        });
        this.mainLayout.addComponent(btnAddMapping);

        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setContent(this.mainLayout);
        setCompositionRoot(panel);

    }

    private void buildCheckBoxLayout() {
        // area with check boxes
        GridLayout checkLayout = new GridLayout(3, 1);
        checkLayout.setWidth("100%");
        checkLayout.setHeight("-1px");
        checkLayout.setSpacing(true);
        this.mainLayout.addComponent(checkLayout);

        this.checkGenerateNew = new CheckBox(this.ctx.tr("dialog.column.generate.new"));
        this.checkGenerateNew.setDescription(this.ctx.tr("dialog.column.generate.new.description"));
        checkLayout.addComponent(this.checkGenerateNew);

        this.checkIgnoreBlankCell = new CheckBox(this.ctx.tr("dialog.ignore.blank"));
        this.checkIgnoreBlankCell.setDescription(this.ctx.tr("dialog.ignore.blank.description"));
        checkLayout.addComponent(this.checkIgnoreBlankCell);

        this.checkAdvancedKeyColumn = new CheckBox(this.ctx.tr("dialog.column.advanced.key"));
        this.checkAdvancedKeyColumn.setDescription(this.ctx.tr("dialog.column.advanced.key.description"));
        checkLayout.addComponent(this.checkAdvancedKeyColumn);

        this.checkGenerateRowTriple = new CheckBox(this.ctx.tr("dialog.row.generate.column"));
        this.checkGenerateRowTriple.setDescription(this.ctx.tr("dialog.row.generate.column.description"));
        checkLayout.addComponent(this.checkGenerateRowTriple);

        this.checkTableSubject = new CheckBox(this.ctx.tr("dialog.table.subject"));
        this.checkTableSubject.setDescription(this.ctx.tr("dialog.table.subject.description", TabularOntology.TABLE_HAS_ROW, TabularOntology.TABLE_SYMBOLIC_NAME));
        checkLayout.addComponent(this.checkTableSubject);

        this.checkAutoAsString = new CheckBox(this.ctx.tr("dialog.auto.type.string"));
        this.checkAutoAsString.setDescription(this.ctx.tr("dialog.auto.type.string.description"));
        checkLayout.addComponent(this.checkAutoAsString);

        this.checkGenerateTableClass = new CheckBox(this.ctx.tr("dialog.table.row.class"));
        this.checkGenerateRowTriple.setDescription(this.ctx.tr("dialog.table.row.class.description"));
        checkLayout.addComponent(this.checkGenerateTableClass);

        this.checkGenerateLabels = new CheckBox(this.ctx.tr("dialog.labels.generate"));
        this.checkGenerateLabels.setDescription(this.ctx.tr("dialog.labels.generate.description"));
        checkLayout.addComponent(this.checkGenerateLabels);
    }

    /**
     * Add new line (component) into tab "Simple" mapping.
     * 
     * @param name
     * @param setting
     */
    private void addSimplePropertyMapping(String name, ColumnInfo_V1 setting) {
        final PropertyGroup newGroup = new PropertyGroup(this.basicLayout);
        this.basicMapping.add(newGroup);
        if (name != null && setting != null) {
            newGroup.set(name, setting);
        }
    }

    @Override
    protected void setConfiguration(RelationalToRdfConfig_V1 config) throws DPUConfigException {
        this.txtKeyColumnName.setValue(config.getKeyColumn());
        //
        // save uri
        //
        String uriStr = config.getBaseURI();

        if (!uriStr.endsWith("/") && !uriStr.endsWith("#")) {
            //uri does not end with "/" or "#", add "/" automatically
            uriStr = uriStr + "/";
        }

        try {
            new java.net.URI(uriStr);
        } catch (URISyntaxException ex) {
            throw new DPUConfigException("Base URI has invalid format", ex);

        }
        this.txtBaseUri.setValue(uriStr);
        //
        // column/cell mapping
        //
        loadColumnMapping(config.getColumnsInfo());
        this.checkGenerateNew.setValue(config.isGenerateNew());
        this.txtRowsClass.setValue(config.getRowsClass());
        this.checkIgnoreBlankCell.setValue(config.isIgnoreBlankCells());
        this.checkAdvancedKeyColumn.setValue(config.isAdvancedKeyColumn());
        this.checkGenerateRowTriple.setValue(config.isGenerateRowTriple());
        this.checkTableSubject.setValue(config.isUseTableSubject());
        this.checkAutoAsString.setValue(config.isAutoAsStrings());
        this.checkGenerateTableClass.setValue(config.isGenerateTableClass());
        this.checkGenerateLabels.setValue(config.isGenerateLabels());

    }

    @Override
    protected RelationalToRdfConfig_V1 getConfiguration() throws DPUConfigException {
        RelationalToRdfConfig_V1 config = new RelationalToRdfConfig_V1();
        // check global validity
        if (!this.txtBaseUri.isValid()) {
            throw new DPUConfigException("Configuration contains invalid inputs.");
        }

        config.setKeyColumn(this.txtKeyColumnName.getValue());
        config.setBaseURI(this.txtBaseUri.getValue());
        // 
        // column mapping
        //
        storeColumnMapping(config.getColumnsInfo());

        final String rowsClass = this.txtRowsClass.getValue();
        if (rowsClass == null || rowsClass.isEmpty()) {
            config.setRowsClass(null);
        } else {
            // try parse URI
            try {
                new java.net.URI(rowsClass);
            } catch (URISyntaxException ex) {
                throw new DPUConfigException("Wrong uri for row class.", ex);
            }
            config.setRowsClass(this.txtRowsClass.getValue());
        }
        //
        // additional checks
        //
        if (!config.isGenerateNew() && config.getColumnsInfo().isEmpty()) {
            throw new DPUConfigException(
                    "Specify at least one column mapping or check 'Full column mapping' option.");
        }

        return config;
    }

    private void loadColumnMapping(Map<String, ColumnInfo_V1> basic) {
        //
        // column info basic
        //
        int index = 0;
        for (String key : basic.keySet()) {
            final ColumnInfo_V1 info = basic.get(key);
            if (index >= this.basicMapping.size()) {
                addSimplePropertyMapping(key, info);
            } else {
                // use existing
                this.basicMapping.get(index).set(key, info);
            }
            index++;
        }
        // clear old
        for (; index < this.basicMapping.size(); ++index) {
            this.basicMapping.get(index).clear();
        }
    }

    private void storeColumnMapping(Map<String, ColumnInfo_V1> basic) throws DPUConfigException {
        //
        // column info basic
        //
        for (PropertyGroup item : this.basicMapping) {
            final String name = item.getColumnName();
            if (name != null && !name.isEmpty()) {
                basic.put(name, item.get());
            }
        }
    }

}
