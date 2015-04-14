package eu.unifiedviews.plugins.transformer.tabulartorelational;

import com.vaadin.data.Validator;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.ui.*;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.localization.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

public class TabularToRelationalVaadinDialog extends AbstractDialog<TabularToRelationalConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(TabularToRelationalVaadinDialog.class);

    private TextField tableNameField;

    private TextField encodingField;

    private ObjectProperty<Integer> rowLimitProperty;

    private TextField rowLimitField;

    private TextField fieldDelimiterField;

    private TextField fieldSeparatorField;

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
        mainLayout.setMargin(false);

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
        tableNameField.addValidator(new Validator() {
            @Override public void validate(Object value) throws InvalidValueException {
                if(!(value instanceof String && isTableNameValid((String) value))) throw new InvalidValueException(ctx.tr("dialog.tablename.restriction"));
            }
        });
        tableNameField.setRequiredError(ctx.tr("dialog.tablename.required"));
        tableNameField.setDescription(ctx.tr("dialog.tablename.description") + " " + ctx.tr("dialog.tablename.restriction"));
        layout.addComponent(tableNameField);

        encodingField = new TextField(ctx.tr("dialog.encoding"));
        encodingField.setDescription(ctx.tr("dialog.encoding.description"));
        layout.addComponent(encodingField);

        rowLimitProperty = new ObjectProperty<>(0);
        rowLimitField = new TextField(ctx.tr("dialog.rowsLimit"), rowLimitProperty);
        rowLimitField.setRequired(true);
        rowLimitField.setRequiredError(ctx.tr("dialog.rowsLimit.required"));
        rowLimitField.addValidator(new IntegerRangeValidator(ctx.tr("dialog.rowsLimit.validator"), 1 , Integer.MAX_VALUE));
        rowLimitField.setDescription(ctx.tr("dialog.rowsLimit.description"));
        layout.addComponent(rowLimitField);

        fieldDelimiterField = new TextField(ctx.tr("dialog.fieldDelimiter"));
        fieldDelimiterField.setDescription(ctx.tr("dialog.fieldDelimiter.description"));
        layout.addComponent(fieldDelimiterField);

        fieldSeparatorField = new TextField(ctx.tr("dialog.fieldSeparator"));
        fieldSeparatorField.setDescription(ctx.tr("dialog.fieldSeparator.description"));
        layout.addComponent(fieldSeparatorField);

        return layout;
    }

    private Component buildTableLayout() {
        table = new Table();
        table.setPageLength(7);

        table.addContainerProperty("name", String.class, null);
        table.addContainerProperty("type", String.class, null);
        table.addContainerProperty("primaryKey", CheckBox.class, null);

        table.setColumnHeaders(ctx.tr("table.name"), ctx.tr("table.type"), ctx.tr("table.primary.key"));

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
        if(config.getTableName().isEmpty()) {
            tableNameField.setValue(ctx.tr("dialog.tableName.example"));
        } else {
            tableNameField.setValue(config.getTableName());
        }
        encodingField.setValue(config.getEncoding());
        fieldDelimiterField.setValue(config.getFieldDelimiter());
        fieldSeparatorField.setValue(config.getFieldSeparator());
        rowLimitProperty.setValue(config.getRowsLimit());

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
        // validation
        try {
            tableNameField.validate();
            rowLimitField.validate();
        } catch(Validator.InvalidValueException e) {
            throw new DPUConfigException(e.getMessage());
        }

        TabularToRelationalConfig_V1 config = new TabularToRelationalConfig_V1();
        config.setTableName(tableNameField.getValue());
        config.setEncoding(encodingField.getValue());
        config.setFieldDelimiter(fieldDelimiterField.getValue());
        config.setFieldSeparator(fieldSeparatorField.getValue());
        config.setRowsLimit(rowLimitProperty.getValue());

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

    private boolean isTableNameValid(String value) {
        String regex = "[A-Z_]+";
        if(value == null || !value.matches(regex)) {
            return false;
        }
        return true;
    }
}
