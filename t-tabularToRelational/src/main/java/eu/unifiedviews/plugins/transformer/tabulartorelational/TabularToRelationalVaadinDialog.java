package eu.unifiedviews.plugins.transformer.tabulartorelational;

import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TabularToRelationalVaadinDialog extends AbstractDialog<TabularToRelationalConfig_V1> {

    private TextField tableNameField;

    private NativeSelect charsetSelect;

    private TextField fieldDelimiterField;

    private TextField fieldSeparatorField;

    private CheckBox hasHeaderCheckbox;

    private Table table;

    public TabularToRelationalVaadinDialog() {
        super(TabularToRelational.class);
    }

    @Override
    protected void buildDialogLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);

        mainLayout.addComponent(buildFormLayout());
        mainLayout.addComponent(buildTableLayout());
        mainLayout.addComponent(buildButtonsLayout());

        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setContent(mainLayout);

        setWidth("100%");
        setHeight("100%");
        setCompositionRoot(panel);
    }

    private Component buildFormLayout() {
        final FormLayout layout = new FormLayout();
        layout.setSizeFull();
        layout.setSpacing(true);

        tableNameField = new TextField(ctx.tr("dialog.tablename"));
        tableNameField.setRequired(true);
        tableNameField.setImmediate(true);
        tableNameField.addValidator(new Validator() {
            @Override public void validate(Object value) throws InvalidValueException {
                if (!(value instanceof String && isNameValid((String) value)))
                    throw new InvalidValueException(ctx.tr("dialog.tablename.restriction"));
            }
        });
        tableNameField.setRequiredError(ctx.tr("dialog.tablename.required"));
        tableNameField.setDescription(ctx.tr("dialog.tablename.description") + " " + ctx.tr("dialog.tablename.restriction"));
        layout.addComponent(tableNameField);

        final BeanItemContainer<String> container = new BeanItemContainer<String>(String.class, Arrays.asList(TabularToRelational.CHARSETS));
        charsetSelect = new NativeSelect(ctx.tr("dialog.charset"), container);
        charsetSelect.setNullSelectionAllowed(false);
        charsetSelect.setImmediate(true);
        layout.addComponent(charsetSelect);

        hasHeaderCheckbox = new CheckBox(ctx.tr("dialog.header"));
        hasHeaderCheckbox.setDescription(ctx.tr("dialog.header.description"));
        layout.addComponent(hasHeaderCheckbox);

        fieldSeparatorField = new TextField(ctx.tr("dialog.fieldSeparator"));
        fieldSeparatorField.setDescription(ctx.tr("dialog.fieldSeparator.description"));
        layout.addComponent(fieldSeparatorField);

        fieldDelimiterField = new TextField(ctx.tr("dialog.fieldDelimiter"));
        fieldDelimiterField.setDescription(ctx.tr("dialog.fieldDelimiter.description"));
        layout.addComponent(fieldDelimiterField);

        return layout;
    }

    private Component buildTableLayout() {
        table = new Table();
        table.setPageLength(7);

        table.addContainerProperty("name", String.class, null);
        table.addContainerProperty("primaryKey", CheckBox.class, null);

        table.setColumnHeaders(ctx.tr("table.name"), ctx.tr("table.primary.key"));

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
                table.addItem(new Object[] { "", new CheckBox() }, (Integer) table.lastItemId() + 1);
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
        if (config.getTableName().isEmpty()) {
            tableNameField.setValue(ctx.tr("dialog.tableName.example"));
        } else {
            tableNameField.setValue(config.getTableName());
        }
        charsetSelect.setValue(config.getEncoding());
        fieldDelimiterField.setValue(config.getFieldDelimiter());
        fieldSeparatorField.setValue(config.getFieldSeparator());
        hasHeaderCheckbox.setValue(config.isHasHeader());
        table.removeAllItems();
        if (config.getColumnMapping() == null || config.getColumnMapping().isEmpty()) { // if config does not contain any mapping, create empty one
            // add first row
            table.addItem(new Object[] { "", new CheckBox() }, 0);
        } else {
            for (ColumnMappingEntry entry : config.getColumnMapping()) {
                CheckBox primaryKeyCheckBox = new CheckBox();
                primaryKeyCheckBox.setValue(entry.isPrimaryKey());
                Object[] row = new Object[] { entry.getColumnName(), primaryKeyCheckBox };
                Integer id = (table.lastItemId() == null) ? 0 : (Integer) table.lastItemId() + 1;
                table.addItem(row, id);
            }
        }
    }

    @Override
    protected TabularToRelationalConfig_V1 getConfiguration() throws DPUConfigException {
        // validation
        try {
            // check table name
            tableNameField.validate();
            // check column names
            for (Iterator i = table.getItemIds().iterator(); i.hasNext(); ) {
                Integer id = (Integer) i.next();
                String value = (String) table.getContainerProperty(id, "name").getValue();
                if (!isNameValid(value)) {
                    throw new DPUConfigException(ctx.tr("dialog.tablecolumn.restriction"));
                }
            }
        } catch (Validator.InvalidValueException e) {
            throw new DPUConfigException(e.getMessage());
        }

        TabularToRelationalConfig_V1 config = new TabularToRelationalConfig_V1();
        config.setTableName(tableNameField.getValue());
        config.setEncoding(String.valueOf(charsetSelect.getValue()));
        config.setFieldDelimiter(fieldDelimiterField.getValue());
        config.setFieldSeparator(fieldSeparatorField.getValue());
        config.setHasHeader(hasHeaderCheckbox.getValue());

        List<ColumnMappingEntry> list = new ArrayList<>();
        for (Iterator i = table.getItemIds().iterator(); i.hasNext(); ) {
            Integer id = (Integer) i.next();

            ColumnMappingEntry entry = new ColumnMappingEntry();
            entry.setColumnName((String) table.getContainerProperty(id, "name").getValue());
            entry.setDataType("VARCHAR"); // we have decided that we will force the type to varchar only
            entry.setPrimaryKey(((CheckBox) table.getContainerProperty(id, "primaryKey").getValue()).getValue());

            list.add(entry);
        }
        config.setColumnMapping(list);
        return config;
    }

    private boolean isNameValid(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern.compile("[A-Za-z][A-Za-z0-9_]*");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
