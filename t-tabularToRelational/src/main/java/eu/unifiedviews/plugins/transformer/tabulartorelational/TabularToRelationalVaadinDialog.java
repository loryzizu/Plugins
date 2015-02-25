package eu.unifiedviews.plugins.transformer.tabulartorelational;

import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.ui.*;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;
import eu.unifiedviews.helpers.dpu.config.InitializableConfigDialog;
import eu.unifiedviews.helpers.dpu.localization.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TabularToRelationalVaadinDialog extends BaseConfigDialog<TabularToRelationalConfig_V1> implements InitializableConfigDialog {

    private static final Logger LOG = LoggerFactory.getLogger(TabularToRelationalVaadinDialog.class);

    private Messages messages;

    private TextField tableNameField;

    private TextField encodingField;

    private TextField rowLimitField;

    private TextField fieldDelimiterField;

    private TextField fieldSeparatorField;

    private Table table;

    public TabularToRelationalVaadinDialog() {
        super(TabularToRelationalConfig_V1.class);
    }

    @Override
    public void initialize() {
        this.messages = new Messages(getContext().getLocale(), this.getClass().getClassLoader());

        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setContent(buildMainLayout());

        setWidth("100%");
        setHeight("100%");
        setCompositionRoot(panel);
    }

    private Component buildMainLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setSpacing(true);
        mainLayout.setMargin(false);

        mainLayout.addComponent(buildFormLayout());
        mainLayout.addComponent(buildTableLayout());
        mainLayout.addComponent(buildButtonsLayout());

        return mainLayout;
    }

    private Component buildFormLayout() {
        final FormLayout layout = new FormLayout();
        layout.setSizeFull();
        layout.setSpacing(true);

        tableNameField = new TextField(messages.getString("dialog.tablename"));
        tableNameField.setRequired(true);
        tableNameField.setRequiredError(messages.getString("dialog.tablename.required"));
        tableNameField.setDescription(messages.getString("dialog.tablename.description"));
        layout.addComponent(tableNameField);

        encodingField = new TextField(messages.getString("dialog.encoding"));
        encodingField.setDescription(messages.getString("dialog.encoding.description"));
        layout.addComponent(encodingField);

        rowLimitField = new TextField(messages.getString("dialog.rowsLimit"));
        rowLimitField.addValidator(new IntegerRangeValidator(messages.getString("dialog.rowsLimit.validator"), 1 , Integer.MAX_VALUE));
        rowLimitField.setDescription(messages.getString("dialog.rowsLimit.description"));
        layout.addComponent(rowLimitField);

        fieldDelimiterField = new TextField(messages.getString("dialog.fieldDelimiter"));
        fieldDelimiterField.setDescription(messages.getString("dialog.fieldDelimiter.description"));
        layout.addComponent(fieldDelimiterField);

        fieldSeparatorField = new TextField(messages.getString("dialog.fieldSeparator"));
        fieldSeparatorField.setDescription(messages.getString("dialog.fieldSeparator.description"));
        layout.addComponent(fieldSeparatorField);

        return layout;
    }

    private Component buildTableLayout() {
        table = new Table();
        table.setPageLength(7);

        table.addContainerProperty("name", String.class, null);
        table.addContainerProperty("type", String.class, null);
        table.addContainerProperty("primaryKey", CheckBox.class, null);

        table.setColumnHeaders(messages.getString("table.name"), messages.getString("table.type"), messages.getString("primary.key"));

        table.setImmediate(true);
        table.setEditable(true);
        return table;
    }

    private Component buildButtonsLayout() {
        final HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        Button addRowButton = new Button("+");
        addRowButton.addClickListener(new Button.ClickListener() {
            @Override public void buttonClick(Button.ClickEvent clickEvent) {
                table.addItem(new Object[] {"", "", new CheckBox()}, (Integer) table.lastItemId() + 1);
            }
        });
        layout.addComponent(addRowButton);

        Button removeRowButton = new Button("-");
        removeRowButton.addClickListener(new Button.ClickListener() {
            @Override public void buttonClick(Button.ClickEvent clickEvent) {
                if (table.size() > 1) {
                    table.removeItem(table.lastItemId());
                }
            }
        });
        layout.addComponent(removeRowButton);

        return layout;
    }

    @Override
    protected void setConfiguration(TabularToRelationalConfig_V1 config) throws DPUConfigException {
        tableNameField.setValue(config.getTableName());
        encodingField.setValue(config.getEncoding());
        fieldDelimiterField.setValue(config.getFieldDelimiter());
        fieldSeparatorField.setValue(config.getFieldSeparator());
        rowLimitField.setValue(config.getRowsLimit().toString());

        table.removeAllItems();
        if(config.getColumnMapping() == null || config.getColumnMapping().isEmpty()) { // if config does not contain any mapping, create empty one
            // add first row
            table.addItem(new Object[] { "", "", new CheckBox()}, 0);
        } else {
            for (ColumnMappingEntry entry : config.getColumnMapping()) {
                CheckBox primaryKeyCheckBox = new CheckBox();
                primaryKeyCheckBox.setValue(entry.isPrimaryKey());
                Object[] row = new Object[] { entry.getColumnName(), entry.getDataType(), primaryKeyCheckBox };
                Integer id = (table.lastItemId() == null) ? 0 : (Integer) table.lastItemId() + 1;
                table.addItem(row, id);
            }
        }
    }

    @Override
    protected TabularToRelationalConfig_V1 getConfiguration() throws DPUConfigException {
        TabularToRelationalConfig_V1 config = new TabularToRelationalConfig_V1();
        config.setTableName(tableNameField.getValue());
        config.setEncoding(encodingField.getValue());
        config.setFieldDelimiter(fieldDelimiterField.getValue());
        config.setFieldSeparator(fieldSeparatorField.getValue());
        config.setRowsLimit(Integer.parseInt(rowLimitField.getValue()));

        List<ColumnMappingEntry> list = new ArrayList<>();
        for(Iterator i = table.getItemIds().iterator(); i.hasNext();){
            Integer id = (Integer) i.next();

            ColumnMappingEntry entry = new ColumnMappingEntry();
            entry.setColumnName((String) table.getContainerProperty(id, "name").getValue());
            entry.setDataType((String) table.getContainerProperty(id, "type").getValue());
            entry.setPrimaryKey(((CheckBox) table.getContainerProperty(id, "primaryKey").getValue()).getValue());

            list.add(entry);
        }
        config.setColumnMapping(list);
        return config;
    }
}
