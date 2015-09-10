package eu.unifiedviews.plugins.transformer.tabulartorelational;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;
import eu.unifiedviews.plugins.transformer.tabulartorelational.model.ColumnMappingEntry;
import eu.unifiedviews.plugins.transformer.tabulartorelational.model.ParserType;

public class TabularToRelationalVaadinDialog extends AbstractDialog<TabularToRelationalConfig_V2> {

    private OptionGroup parserTypeOptionGroup;

    private TextField tableNameField;

    private NativeSelect charsetSelect;

    private TextField fieldDelimiterField;

    private TextField fieldSeparatorField;

    private TextField dataBeginningRowField;

    private Table table;

    public TabularToRelationalVaadinDialog() {
        super(TabularToRelational.class);
    }

    @Override
    protected void buildDialogLayout() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);

        final Label inputConfigurationsLabel = new Label(ctx.tr("dialog.configuration.input"));
        mainLayout.addComponent(inputConfigurationsLabel);
        mainLayout.addComponent(buildInputConfigurationLayout());

        final Label outputConfigurationsLabel = new Label(ctx.tr("dialog.configuration.output"));
        mainLayout.addComponent(outputConfigurationsLabel);
        mainLayout.addComponent(buildOutputConfigurationLayout());

        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setContent(mainLayout);

        setWidth("100%");
        setHeight("100%");
        setCompositionRoot(panel);
    }

    private Component buildInputConfigurationLayout() {
        final FormLayout formLayout = new FormLayout();

        BeanItemContainer<ParserType> parserTypesContainer = new BeanItemContainer<ParserType>(ParserType.class, Arrays.asList(ParserType.values()));
        parserTypeOptionGroup = new OptionGroup(ctx.tr("dialog.parser.type"), parserTypesContainer);
        parserTypeOptionGroup.setItemCaptionPropertyId("description");
        parserTypeOptionGroup.setNullSelectionAllowed(false);
        parserTypeOptionGroup.setImmediate(true);
        parserTypeOptionGroup.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent e) {
                ParserType newParserType = (ParserType) e.getProperty().getValue();
                switch (newParserType) {
                    case XLS:
                        hideComponents(charsetSelect, fieldDelimiterField, fieldSeparatorField);
                        break;
                    case CSV:
                        showComponents(charsetSelect, fieldDelimiterField, fieldSeparatorField);
                        break;
                    case DBF:
                        showComponents(charsetSelect);
                        hideComponents(fieldDelimiterField, fieldSeparatorField);
                        break;
                    default:
                }
            }
        });
        formLayout.addComponent(parserTypeOptionGroup);

        dataBeginningRowField = new TextField(ctx.tr("dialog.dataBeginningRow"));
        dataBeginningRowField.setDescription(ctx.tr("dialog.dataBeginningRow.description"));
        dataBeginningRowField.addValidator(new Validator() {
            @Override
            public void validate(Object value) throws Validator.InvalidValueException {
                try {
                    Integer number = Integer.valueOf(value.toString());
                    if (number < 1) {
                        throw new Validator.InvalidValueException(ctx.tr("dialog.dataBeginningRow.restriction"));
                    }
                } catch (NumberFormatException e) {
                    throw new Validator.InvalidValueException(ctx.tr("dialog.dataBeginningRow.restriction"));
                }

            }
        });
        dataBeginningRowField.setRequired(true);
        dataBeginningRowField.setImmediate(true);
        formLayout.addComponent(dataBeginningRowField);

        fieldSeparatorField = new TextField(ctx.tr("dialog.fieldSeparator"));
        fieldSeparatorField.setDescription(ctx.tr("dialog.fieldSeparator.description"));
        formLayout.addComponent(fieldSeparatorField);

        fieldDelimiterField = new TextField(ctx.tr("dialog.fieldDelimiter"));
        fieldDelimiterField.setDescription(ctx.tr("dialog.fieldDelimiter.description"));
        formLayout.addComponent(fieldDelimiterField);

        final BeanItemContainer<String> container = new BeanItemContainer<String>(String.class, Arrays.asList(TabularToRelational.CHARSETS));
        charsetSelect = new NativeSelect(ctx.tr("dialog.charset"), container);
        charsetSelect.setDescription(ctx.tr("dialog.charset.description"));
        charsetSelect.setNullSelectionAllowed(false);
        charsetSelect.setImmediate(true);
        formLayout.addComponent(charsetSelect);

        return formLayout;
    }

    private Component buildOutputConfigurationLayout() {
        final FormLayout formLayout = new FormLayout();

        tableNameField = new TextField(ctx.tr("dialog.tablename"));
        tableNameField.setRequired(true);
        tableNameField.setImmediate(true);
        tableNameField.addValidator(new Validator() {
            @Override
            public void validate(Object value) throws Validator.InvalidValueException {
                if (!(value instanceof String && isNameValid((String) value)))
                    throw new Validator.InvalidValueException(ctx.tr("dialog.tablename.restriction"));
            }
        });
        tableNameField.setRequiredError(ctx.tr("dialog.tablename.required"));
        tableNameField.setDescription(ctx.tr("dialog.tablename.description") + " " + ctx.tr("dialog.tablename.restriction"));
        formLayout.addComponent(tableNameField);

        table = new Table(ctx.tr("table.label"));
        table.setPageLength(7);
        table.addContainerProperty("name", String.class, null);
        table.addContainerProperty("primaryKey", CheckBox.class, null);
        table.setColumnHeaders(ctx.tr("table.name"), ctx.tr("table.primary.key"));
        table.setDescription(ctx.tr("table.desciption"));
        table.setImmediate(true);
        table.setEditable(true);
        formLayout.addComponent(table);

        formLayout.addComponent(buildButtonsLayout());

        return formLayout;
    }

    private Component buildButtonsLayout() {
        final HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        Button addRowButton = new Button("+");
        addRowButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                table.addItem(new Object[] { "", new CheckBox() }, (Integer) table.lastItemId() + 1);
            }
        });
        layout.addComponent(addRowButton);

        Button removeRowButton = new Button("-");
        removeRowButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (table.size() > 1) {
                    table.removeItem(table.lastItemId());
                }
            }
        });
        layout.addComponent(removeRowButton);

        return layout;
    }

    @Override
    protected void setConfiguration(TabularToRelationalConfig_V2 config) throws DPUConfigException {
        if (config.getTableName().isEmpty()) {
            tableNameField.setValue(ctx.tr("dialog.tableName.example"));
        } else {
            tableNameField.setValue(config.getTableName());
        }
        dataBeginningRowField.setValue(config.getDataBegginningRow().toString());
        charsetSelect.setValue(config.getEncoding());
        fieldDelimiterField.setValue(config.getFieldDelimiter());
        fieldSeparatorField.setValue(config.getFieldSeparator());
        parserTypeOptionGroup.setValue(config.getParserType());
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
    protected TabularToRelationalConfig_V2 getConfiguration() throws DPUConfigException {
        TabularToRelationalConfig_V2 config = new TabularToRelationalConfig_V2();
        config.setTableName(tableNameField.getValue());
        config.setEncoding(String.valueOf(charsetSelect.getValue()));
        config.setFieldDelimiter(fieldDelimiterField.getValue());
        config.setFieldSeparator(fieldSeparatorField.getValue());
        config.setParserType((ParserType) parserTypeOptionGroup.getValue());
        config.setDataBegginningRow(Integer.parseInt(dataBeginningRowField.getValue()));

        List<ColumnMappingEntry> list = new ArrayList<>();
        for (Iterator i = table.getItemIds().iterator(); i.hasNext();) {
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
        if (value == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[A-Za-z][A-Za-z0-9_]*");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches() || value.isEmpty();
    }

    private void showComponents(Component... components) {
        for (Component component : components) {
            component.setVisible(true);
        }
    }

    private void hideComponents(Component... components) {
        for (Component component : components) {
            component.setVisible(false);
        }
    }
}
