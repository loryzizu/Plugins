package eu.unifiedviews.plugins.transformer.tabular;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.TabSheet.Tab;

import cz.cuni.mff.xrg.uv.transformer.tabular.Tabular;
import cz.cuni.mff.xrg.uv.transformer.tabular.TabularConfig_V2;
import cz.cuni.mff.xrg.uv.transformer.tabular.TabularOntology;
import cz.cuni.mff.xrg.uv.transformer.tabular.column.ColumnInfo_V1;
import cz.cuni.mff.xrg.uv.transformer.tabular.column.NamedCell_V1;
import cz.cuni.mff.xrg.uv.transformer.tabular.parser.ParserType;
import cz.cuni.mff.xrg.uv.transformer.tabular.parser.ParserXls;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;
import eu.unifiedviews.plugins.transformer.tabular.gui.PropertyGroup;
import eu.unifiedviews.plugins.transformer.tabular.gui.PropertyGroupAdv;
import eu.unifiedviews.plugins.transformer.tabular.gui.PropertyNamedCell;

public class TabularVaadinDialog extends AbstractDialog<TabularConfig_V2> {

    private static final Logger LOG = LoggerFactory.getLogger(TabularVaadinDialog.class);

    private String[] encoding = { "UTF-8", "UTF-16", "ISO-8859-1", "windows-1250" };

    private OptionGroup optionTableType;

    private TextField txtBaseUri;

    private TextField txtKeyColumnName;

    private ComboBox txtEncoding;

    private TextField txtRowsLimit;

    private TextField txtRowsClass;

    private CheckBox checkGenerateNew;

    private CheckBox checkIgnoreBlankCell;

    private CheckBox checkStaticRowCounter;

    private CheckBox checkAdvancedKeyColumn;

    private CheckBox checkGenerateRowTriple;

    private CheckBox checkTableSubject;

    private CheckBox checkAutoAsString;

    private CheckBox checkGenerateTableClass;

    private CheckBox checkGenerateLabels;

    private TextField txtCsvQuoteChar;

    private TextField txtCsvDelimeterChar;

    private TextField txtCsvLinesToIgnore;

    private CheckBox checkCsvHasHeader;

    private TextField txtXlsSheetName;

    private TextField txtXlsLinesToIgnore;

    private CheckBox checkXlsHasHeader;

    private CheckBox checkXlsStripHeader;

    private CheckBox checkXlsAdvancedDoubleParser;

    private CheckBox checkTrimString;

    private CheckBox checkIgnoreMissingColumns;

    /**
     * Layout for basic column mapping.
     */
    private GridLayout basicLayout;

    /**
     * Layout for advanced column mapping.
     */
    private GridLayout advancedLayout;

    /**
     * Layout for xls.
     */
    private GridLayout xlsStaticLayout;

    private final List<PropertyGroup> basisMapping = new ArrayList<>();

    private final List<PropertyGroupAdv> advancedMapping = new ArrayList<>();

    private final List<PropertyNamedCell> xlsNamedCells = new ArrayList<>();

    private Panel mainPanel;

    private VerticalLayout mainLayout;

    /**
     * If true then the composite root has already been set.
     */
    private boolean layoutSet = false;

    public TabularVaadinDialog() {
        super(Tabular.class);
    }

    @Override
    protected void buildDialogLayout() {
        buildMappingImportExportTab();
        buildMainLayout();
    }

    private void buildMainLayout() {

        // ------------------------ General ------------------------
        final VerticalLayout generalLayout = new VerticalLayout();
        generalLayout.setImmediate(true);
        generalLayout.setWidth("350px");
        generalLayout.setHeight("-1px");

        this.optionTableType = new OptionGroup(this.ctx.tr("dialog.input.type.choose"));
        this.optionTableType.setImmediate(true);
        for (ParserType type : ParserType.values()) {
            this.optionTableType.addItem(type);
        }
        this.optionTableType.setNullSelectionAllowed(false);
        this.optionTableType.setValue(ParserType.CSV);
        generalLayout.addComponent(this.optionTableType);

        this.txtBaseUri = new TextField(this.ctx.tr("dialog.base.uri"));
        this.txtBaseUri.setWidth("100%");
        this.txtBaseUri.setRequired(true);
        this.txtBaseUri.setRequiredError(this.ctx.tr("dialog.base.uri.required"));
        this.txtBaseUri.setDescription(this.ctx.tr("dialog.base.uri.description"));
        generalLayout.addComponent(this.txtBaseUri);

        this.txtKeyColumnName = new TextField(this.ctx.tr("dialog.column.key"));
        this.txtKeyColumnName.setNullRepresentation("");
        this.txtKeyColumnName.setNullSettingAllowed(true);
        this.txtKeyColumnName.setWidth("100%");
        this.txtKeyColumnName.setDescription(this.ctx.tr("dialog.column.key.description"));
        generalLayout.addComponent(this.txtKeyColumnName);

        this.txtEncoding = new ComboBox(this.ctx.tr("dialog.encoding"));
        for (String encd : this.encoding) {
            this.txtEncoding.addItem(encd);
            this.txtEncoding.setItemCaption(encd, encd);
        }
        txtEncoding.setTextInputAllowed(true);
        txtEncoding.setNewItemsAllowed(true);
        txtEncoding.setNullSelectionAllowed(false);
        txtEncoding.setImmediate(true);
        this.txtEncoding.setRequired(true);
        generalLayout.addComponent(this.txtEncoding);

        this.txtRowsLimit = new TextField(this.ctx.tr("dialog.rows.limit"));
        this.txtRowsLimit.setInputPrompt(this.ctx.tr("dialog.rows.limit.prompt"));
        this.txtRowsLimit.setNullRepresentation("");
        this.txtRowsLimit.setNullSettingAllowed(true);
        generalLayout.addComponent(this.txtRowsLimit);

        this.txtRowsClass = new TextField(this.ctx.tr("dialog.row.class"));
        this.txtRowsClass.setDescription(this.ctx.tr("dialog.row.class.description"));
        this.txtRowsClass.setWidth("100%");
        this.txtRowsClass.setNullRepresentation("");
        this.txtRowsClass.setNullSettingAllowed(true);
        generalLayout.addComponent(this.txtRowsClass);

        // area with check boxes
        GridLayout checkLayout = new GridLayout(5, 1);
        checkLayout.setWidth("100%");
        checkLayout.setHeight("-1px");
        checkLayout.setSpacing(true);

        this.checkGenerateNew = new CheckBox(this.ctx.tr("dialog.column.generate.new"));
        this.checkGenerateNew.setDescription(this.ctx.tr("dialog.column.generate.new.description"));
        checkLayout.addComponent(this.checkGenerateNew);

        this.checkIgnoreBlankCell = new CheckBox(this.ctx.tr("dialog.ignore.blank"));
        this.checkIgnoreBlankCell.setDescription(this.ctx.tr("dialog.ignore.blank.description"));
        checkLayout.addComponent(this.checkIgnoreBlankCell);

        this.checkStaticRowCounter = new CheckBox(this.ctx.tr("dialog.static.counter"));
        this.checkStaticRowCounter.setDescription(this.ctx.tr("dialog.static.counter.description"));
        checkLayout.addComponent(this.checkStaticRowCounter);

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

        this.checkTrimString = new CheckBox(this.ctx.tr("dialog.trailing.spaces.remove"));
        this.checkTrimString.setDescription(this.ctx.tr("dialog.trailing.spaces.remove.description"));
        checkLayout.addComponent(this.checkTrimString);

        this.checkIgnoreMissingColumns = new CheckBox(this.ctx.tr("dialog.missing.columns.ignore"));
        this.checkIgnoreMissingColumns.setDescription(this.ctx.tr("dialog.missing.columns.ignore.description"));
        checkLayout.addComponent(this.checkIgnoreMissingColumns);

        // -------------------------- CSV ----------------------------
        final FormLayout csvLayout = new FormLayout();
        csvLayout.setImmediate(true);
        csvLayout.setSpacing(true);
        csvLayout.setWidth("100%");
        csvLayout.setHeight("-1px");
        csvLayout.addComponent(new Label(this.ctx.tr("dialog.csv.settings")));

        this.txtCsvQuoteChar = new TextField(this.ctx.tr("csv.dialog.char.quote"));
        this.txtCsvQuoteChar.setDescription(this.ctx.tr("csv.dialog.char.quote.description"));
        //this.txtCsvQuoteChar.setInputPrompt("\"");
        csvLayout.addComponent(this.txtCsvQuoteChar);

        this.txtCsvDelimeterChar = new TextField(this.ctx.tr("dialog.csv.char.delimiter"));
        //this.txtCsvDelimeterChar.setInputPrompt(",");
        this.txtCsvDelimeterChar.setRequired(true);
        csvLayout.addComponent(this.txtCsvDelimeterChar);

        this.txtCsvLinesToIgnore = new TextField(this.ctx.tr("dialog.lines.skip"));
        csvLayout.addComponent(this.txtCsvLinesToIgnore);

        this.checkCsvHasHeader = new CheckBox(this.ctx.tr("dialog.header"));
        this.checkCsvHasHeader.setDescription(this.ctx.tr("dialog.header.description"));
        csvLayout.addComponent(this.checkCsvHasHeader);

        // XLS
        final FormLayout xlsLayout = new FormLayout();
        xlsLayout.setImmediate(true);
        xlsLayout.setSpacing(true);
        xlsLayout.setWidth("100%");
        xlsLayout.setHeight("-1px");
        xlsLayout.addComponent(new Label(this.ctx.tr("dialog.xls.settings")));

        this.txtXlsSheetName = new TextField(this.ctx.tr("dialog.xls.sheet.name"));
        this.txtXlsSheetName.setNullRepresentation("");
        this.txtXlsSheetName.setNullSettingAllowed(true);
        this.txtXlsSheetName.setDescription("dialog.xls.sheet.name.description");
        xlsLayout.addComponent(this.txtXlsSheetName);

        xlsLayout.addComponent(new Label(this.ctx.tr("dialog.xls.sheet.name.property", ParserXls.SHEET_COLUMN_NAME)));

        this.txtXlsLinesToIgnore = new TextField(this.ctx.tr("dialog.lines.skip"));
        xlsLayout.addComponent(this.txtXlsLinesToIgnore);

        this.checkXlsHasHeader = new CheckBox(this.ctx.tr("dialog.header"));
        this.checkXlsHasHeader.setDescription(this.ctx.tr("dialog.header.description"));
        xlsLayout.addComponent(this.checkXlsHasHeader);

        this.checkXlsStripHeader = new CheckBox(this.ctx.tr("dialog.xls.header.strip"));
        this.checkXlsStripHeader.setDescription(this.ctx.tr("dialog.xls.header.strip.description"));
        xlsLayout.addComponent(this.checkXlsStripHeader);

        this.checkXlsAdvancedDoubleParser = new CheckBox(this.ctx.tr("dialog.xls.double.advanced.parser"));
        this.checkXlsAdvancedDoubleParser.setDescription(this.ctx.tr("dialog.xls.double.advanced.parser.description"));
        xlsLayout.addComponent(this.checkXlsAdvancedDoubleParser);

        // add change listener
        this.optionTableType.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                final ParserType value = (ParserType) event.getProperty().getValue();
                setControllStates(value);
            }
        });

        // DBF
        final FormLayout dbfLayout = new FormLayout();
        dbfLayout.setImmediate(true);
        dbfLayout.setSpacing(true);
        dbfLayout.setWidth("100%");
        dbfLayout.setHeight("-1px");
        dbfLayout.addComponent(new Label(this.ctx.tr("dialog.dbf.settings")));

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

        // --------------------- Mapping - template based --------------
        this.advancedLayout = new GridLayout(2, 1);
        this.advancedLayout.setWidth("100%");
        this.advancedLayout.setHeight("-1px");
        this.advancedLayout.setImmediate(true);
        this.advancedLayout.setSpacing(true);
        this.advancedLayout.setMargin(true);

        this.advancedLayout.addComponent(new Label(this.ctx.tr("dialog.mapping.property.uri")));
        this.advancedLayout.setColumnExpandRatio(0, 0.3f);

        this.advancedLayout.addComponent(new Label(this.ctx.tr("dialog.mapping.template")));
        this.advancedLayout.setColumnExpandRatio(1, 0.7f);

        // ----------------------- Mapping - xls -----------------------
        this.xlsStaticLayout = new GridLayout(3, 1);
        this.xlsStaticLayout.setWidth("100%");
        this.xlsStaticLayout.setHeight("-1px");
        this.xlsStaticLayout.setImmediate(true);
        this.xlsStaticLayout.setSpacing(true);
        this.xlsStaticLayout.setMargin(true);

        Label lblXlsName = new Label(this.ctx.tr("dialog.mapping.xls.name"));
        lblXlsName.setDescription(this.ctx.tr("dialog.mapping.xls.name.description"));
        this.xlsStaticLayout.addComponent(lblXlsName);
        this.xlsStaticLayout.setColumnExpandRatio(0, 0.6f);

        this.xlsStaticLayout.addComponent(new Label(this.ctx.tr("dialog.mapping.xls.column")));
        this.xlsStaticLayout.setColumnExpandRatio(1, 0.2f);

        this.xlsStaticLayout.addComponent(new Label(this.ctx.tr("dialog.mapping.xls.row")));
        this.xlsStaticLayout.setColumnExpandRatio(2, 0.2f);

        // -------------------------------------------------------------
        final TabSheet propertiesTab = new TabSheet();
        propertiesTab.setSizeFull();

        propertiesTab.addTab(this.basicLayout, this.ctx.tr("dialog.mapping.simple"));
        Tab tabAdv = propertiesTab.addTab(this.advancedLayout, this.ctx.tr("dialog.mapping.advanced"));
        tabAdv.setDescription(this.ctx.tr("dialog.mapping.advanced.description"));
        Tab tabXls = propertiesTab.addTab(this.xlsStaticLayout, this.ctx.tr("dialog.mapping.xls"));
        tabXls.setDescription(this.ctx.tr("dialog.mapping.xls.description"));

        // -------------------------------------------------------------
        // top layout with configuration
        final HorizontalLayout configLayout = new HorizontalLayout();
        configLayout.setWidth("-1px");
        configLayout.setHeight("-1px");
        configLayout.setSpacing(true);

        configLayout.addComponent(generalLayout);
        configLayout.addComponent(csvLayout);
        configLayout.addComponent(dbfLayout);
        configLayout.addComponent(xlsLayout);

        // main layout for whole dialog
        mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setSpacing(true);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setMargin(true);
        mainLayout.addComponent(configLayout);
        mainLayout.addComponent(checkLayout);
        mainLayout.setExpandRatio(configLayout, 0.0f);

        mainLayout.addComponent(new Label(this.ctx.tr("dialog.labels.mapping")));

        mainLayout.addComponent(propertiesTab);
        mainLayout.setExpandRatio(propertiesTab, 1.0f);

        final Button btnAddMapping = new Button(this.ctx.tr("dialog.mapping.button.add"));
        btnAddMapping.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                // add empty line with mapping
                if (propertiesTab.getSelectedTab() == basicLayout) {
                    addSimplePropertyMapping(null, null);
                } else if (propertiesTab.getSelectedTab() == advancedLayout) {
                    addAdvancedPropertyMapping(null, null);
                } else if (propertiesTab.getSelectedTab() == xlsStaticLayout) {
                    if (xlsStaticLayout.isEnabled()) {
                        addXlsMapping(null, null, null);
                    }
                } else {
                    LOG.error("No tabs selected!");
                }
            }
        });
        mainLayout.addComponent(btnAddMapping);
        mainLayout.setExpandRatio(configLayout, 0.0f);

        mainPanel = new Panel();
        mainPanel.setContent(mainLayout);
        mainPanel.setSizeFull();

        setCompositionRoot(mainPanel);
        // composite root can be updated in
        // setConfiguration method in reaction to dialog type (instance, template)

        // then we
    }

    private void buildMappingImportExportTab() {
        final VerticalLayout generalLayout = new VerticalLayout();
        generalLayout.setMargin(true);
        generalLayout.setSizeFull();

        final Label label = new Label(this.ctx.tr("dialog.labels.hover"), ContentMode.HTML);
        generalLayout.addComponent(label);
        generalLayout.setExpandRatio(label, 0.0f);

        final HorizontalLayout buttonLine = new HorizontalLayout();
        buttonLine.setWidth("100%");
        buttonLine.setSpacing(true);
        generalLayout.addComponent(buttonLine);
        generalLayout.setExpandRatio(buttonLine, 0.0f);

        final Button btnImportColNames = new Button(this.ctx.tr("dialog.buttons.import"));
        btnImportColNames.setDescription(this.ctx.tr("dialog.buttons.import.description"));
        buttonLine.addComponent(btnImportColNames);

        final TextField txtSeparator = new TextField(this.ctx.tr("dialog.import.separator"));
        txtSeparator.setValue("\\t");
        generalLayout.addComponent(txtSeparator);
        generalLayout.setExpandRatio(txtSeparator, 0.0f);

        final TextArea txtValue = new TextArea("");
        txtValue.setSizeFull();
        generalLayout.addComponent(txtValue);
        generalLayout.setExpandRatio(txtValue, 1.0f);

        btnImportColNames.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unqualified-field-access")
            @Override
            public void buttonClick(Button.ClickEvent event) {
                importColumnNames(txtValue.getValue(),
                        txtSeparator.getValue().trim());

                Notification.show(ctx.tr("dialog.import.notification"), ctx.tr("dialog.import.notification.text"),
                        Notification.Type.HUMANIZED_MESSAGE);
            }
        });

        this.addTab(generalLayout, this.ctx.tr("dialog.tabs.import"));
    }

    /**
     * Based on given type update properties.
     * 
     * @param value
     */
    private void setControllStates(ParserType value) {
        boolean csvEnabled = value == ParserType.CSV;
        boolean xlsEnabled = value == ParserType.XLS;
        boolean dbfEnabled = value == ParserType.DBF;

        txtCsvQuoteChar.setEnabled(csvEnabled);
        txtCsvDelimeterChar.setEnabled(csvEnabled);
        txtCsvLinesToIgnore.setEnabled(csvEnabled);
        checkCsvHasHeader.setEnabled(csvEnabled);

        xlsStaticLayout.setEnabled(xlsEnabled);
        txtXlsSheetName.setEnabled(xlsEnabled);
        txtXlsLinesToIgnore.setEnabled(xlsEnabled);
        checkXlsHasHeader.setEnabled(xlsEnabled);
        checkXlsStripHeader.setEnabled(xlsEnabled);
        checkXlsAdvancedDoubleParser.setEnabled(xlsEnabled);
        for (PropertyNamedCell namedCell : xlsNamedCells) {
            namedCell.setEnabled(xlsEnabled);
        }
    }

    /**
     * Add new line (component) into tab "Simple" mapping.
     * 
     * @param name
     * @param setting
     */
    private void addSimplePropertyMapping(String name, ColumnInfo_V1 setting) {
        final PropertyGroup newGroup = new PropertyGroup(basicLayout);
        basisMapping.add(newGroup);

        if (name != null && setting != null) {
            newGroup.set(name, setting);
        }
    }

    /**
     * Add new line (component) into tab "Advanced" mapping.
     * 
     * @param uri
     * @param template
     */
    private void addAdvancedPropertyMapping(String uri, String template) {
        final PropertyGroupAdv newGroup = new PropertyGroupAdv(advancedLayout);
        advancedMapping.add(newGroup);

        if (uri != null && template != null) {
            newGroup.set(uri, template);
        }
    }

    /**
     * Add new line into "xls" mapping.
     * 
     * @param name
     * @param column
     * @param row
     */
    private void addXlsMapping(String name, Integer column, Integer row) {
        final PropertyNamedCell newNamedCell = new PropertyNamedCell(xlsStaticLayout);
        xlsNamedCells.add(newNamedCell);

        if (name != null && column != null && row != null) {
            newNamedCell.set(name, column, row);
        }
    }

    /**
     * Parse given string and use it to set column (properties) names. Original data are lost.
     * 
     * @param str
     */
    private void importColumnNames(String str, String separator) {
        if (separator == null || separator.isEmpty()) {
            separator = "\\t";
        }
        final String[] columnNames = str.split(separator);
        int index = 0;
        for (; index < columnNames.length; ++index) {
            if (index >= basisMapping.size()) {
                addSimplePropertyMapping(columnNames[index],
                        new ColumnInfo_V1());
            } else {
                // use existing
                basisMapping.get(index).set(columnNames[index],
                        new ColumnInfo_V1());
            }
        }
        // clear old
        for (; index < basisMapping.size(); ++index) {
            basisMapping.get(index).clear();
        }
    }

    @Override
    protected void setConfiguration(TabularConfig_V2 c) throws DPUConfigException {
        //
        // update dialog, as the isTempalte is decided at the begining
        // this should occure only once per dialog creation
        //
        if (!layoutSet) {
            if (getContext().isTemplate()) {
                setCompositionRoot(mainLayout);
            } else {
                setCompositionRoot(mainPanel);
            }
            layoutSet = true;
        }
        //
        txtKeyColumnName.setValue(c.getKeyColumn());
        //
        // save uri
        //
        String uriStr = c.getBaseURI();

        if (!uriStr.endsWith("/") && !uriStr.endsWith("#")) {
            //uri does not end with "/" or "#", add "/" automatically
            uriStr = uriStr + "/";
        }

        try {
            new java.net.URI(uriStr);
        } catch (URISyntaxException ex) {
            throw new DPUConfigException(this.ctx.tr("dialog.errors.uri.invalid.format"), ex);

        }
        txtBaseUri.setValue(uriStr);
        //
        // column/cell mapping
        //
        loadColumnMapping(c.getColumnsInfo(), c.getColumnsInfoAdv());
        //
        // csv data
        //
        if (c.getTableType() == ParserType.CSV) {
            txtCsvQuoteChar.setValue(c.getQuoteChar());
            txtCsvDelimeterChar.setValue(c.getDelimiterChar());
            txtCsvLinesToIgnore.setValue(c.getLinesToIgnore().toString());
            checkCsvHasHeader.setValue(c.isHasHeader());
        } else {
            txtCsvQuoteChar.setValue("\"");
            txtCsvDelimeterChar.setValue(",");
            txtCsvLinesToIgnore.setValue("0");
            checkCsvHasHeader.setValue(true);
        }
        if (c.getTableType() == ParserType.XLS) {
            txtXlsSheetName.setValue(c.getXlsSheetName());
            txtXlsLinesToIgnore.setValue(c.getLinesToIgnore().toString());
            loadCellMapping(c.getNamedCells());
            checkXlsHasHeader.setValue(c.isHasHeader());
            checkXlsStripHeader.setValue(c.isStripHeader());
            checkXlsAdvancedDoubleParser.setValue(c.isXlsAdvancedDoubleParser());
        } else {
            txtXlsSheetName.setValue("");
            txtXlsLinesToIgnore.setValue("0");
            loadCellMapping(Collections.EMPTY_LIST);
            checkXlsHasHeader.setValue(true);
            checkXlsStripHeader.setValue(false);
            checkXlsAdvancedDoubleParser.setValue(false);
        }
        //
        // other data
        //
        if (!txtEncoding.containsId(c.getEncoding())) {
            txtEncoding.addItem(c.getEncoding());
        }
        txtEncoding.setValue(c.getEncoding());
        if (c.getRowsLimit() == null) {
            txtRowsLimit.setValue(null);
        } else {
            txtRowsLimit.setValue(c.getRowsLimit().toString());
        }
        optionTableType.setValue(c.getTableType());
        checkGenerateNew.setValue(c.isGenerateNew());
        txtRowsClass.setValue(c.getRowsClass());
        checkIgnoreBlankCell.setValue(c.isIgnoreBlankCells());
        checkStaticRowCounter.setValue(c.isStaticRowCounter());
        checkAdvancedKeyColumn.setValue(c.isAdvancedKeyColumn());
        checkGenerateRowTriple.setValue(c.isGenerateRowTriple());
        checkTableSubject.setValue(c.isUseTableSubject());
        checkAutoAsString.setValue(c.isAutoAsStrings());
        checkGenerateTableClass.setValue(c.isGenerateTableClass());
        checkGenerateLabels.setValue(c.isGenerateLabels());
        checkTrimString.setValue(c.isDbfTrimString());
        checkIgnoreMissingColumns.setValue(c.isIgnoreMissingColumn());
        //
        // enable/disable controlls
        //
        setControllStates(c.getTableType());
    }

    @Override
    protected TabularConfig_V2 getConfiguration() throws DPUConfigException {
        TabularConfig_V2 cnf = new TabularConfig_V2();

        // check global validity
        if (!this.txtBaseUri.isValid()) {
            throw new DPUConfigException(this.ctx.tr("dialog.errors.uri.invalid.format"));
        }

        if (!this.txtEncoding.isValid()) {
            throw new DPUConfigException(this.ctx.tr("dialog.errors.encoding.invalid"));
        }

        cnf.setKeyColumn(txtKeyColumnName.getValue());
        cnf.setBaseURI(txtBaseUri.getValue());
        // 
        // column mapping
        //
        storeColumnMapping(cnf.getColumnsInfo(), cnf.getColumnsInfoAdv());
        //
        // csv data
        //
        final ParserType value = (ParserType) optionTableType.getValue();
        if (value == ParserType.CSV) {

            if (!txtCsvQuoteChar.isValid() || !txtCsvDelimeterChar.isValid()
                    || !txtCsvLinesToIgnore.isValid()) {
                throw new DPUConfigException(this.ctx.tr("dialog.errors.csv.input.invalid"));
            }

            cnf.setQuoteChar(txtCsvQuoteChar.getValue());
            cnf.setDelimiterChar(txtCsvDelimeterChar.getValue());
            try {
                final String linesToSkipStr = txtCsvLinesToIgnore.getValue();
                if (linesToSkipStr == null || linesToSkipStr.isEmpty()) {
                    cnf.setLinesToIgnore(0);
                } else {
                    cnf.setLinesToIgnore(
                            Integer.parseInt(linesToSkipStr));
                }
            } catch (NumberFormatException ex) {
                throw new DPUConfigException(this.ctx.tr("dialog.errors.lines.skip.invalid"), ex);
            }
            cnf.setHasHeader(checkCsvHasHeader.getValue());
        } else if (value == ParserType.XLS) {
            String xlsSheetName = txtXlsSheetName.getValue();
            if (xlsSheetName == null || xlsSheetName.isEmpty()) {
                xlsSheetName = null;
            }

            try {
                final String linesToSkipStr = txtXlsLinesToIgnore.getValue();
                if (linesToSkipStr == null || linesToSkipStr.isEmpty()) {
                    cnf.setLinesToIgnore(0);
                } else {
                    cnf.setLinesToIgnore(Integer.parseInt(linesToSkipStr));
                }
            } catch (NumberFormatException ex) {
                throw new DPUConfigException(this.ctx.tr("dialog.errors.lines.skip.invalid"), ex);
            }

            cnf.setXlsSheetName(xlsSheetName);
            storeCellMapping(cnf.getNamedCells());

            cnf.setHasHeader(checkXlsHasHeader.getValue());
            cnf.setStripHeader(checkXlsStripHeader.getValue());
            cnf.setXlsAdvancedDoubleParser(checkXlsAdvancedDoubleParser.getValue());
        }
        //
        // other data
        //
        cnf.setEncoding((String) txtEncoding.getValue());

        final String rowsLimitStr = txtRowsLimit.getValue();
        if (rowsLimitStr == null || rowsLimitStr.isEmpty()) {
            cnf.setRowsLimit(null);
        } else {
            try {
                cnf.setRowsLimit(Integer.parseInt(rowsLimitStr));
            } catch (NumberFormatException ex) {
                throw new DPUConfigException("dialog.errors.row.limit.invalid", ex);
            }
        }

        cnf.setTableType((ParserType) optionTableType.getValue());
        cnf.setGenerateNew(checkGenerateNew.getValue());
        cnf.setIgnoreBlankCells(checkIgnoreBlankCell.getValue());
        cnf.setStaticRowCounter(checkStaticRowCounter.getValue());
        cnf.setAdvancedKeyColumn(checkAdvancedKeyColumn.getValue());
        cnf.setGenerateRowTriple(checkGenerateRowTriple.getValue());
        cnf.setUseTableSubject(checkTableSubject.getValue());
        cnf.setAutoAsStrings(checkAutoAsString.getValue());
        cnf.setGenerateTableClass(checkGenerateTableClass.getValue());
        cnf.setGenerateLabels(checkGenerateLabels.getValue());
        cnf.setDbfTrimString(checkTrimString.getValue());
        cnf.setIgnoreMissingColumn(checkIgnoreMissingColumns.getValue());

        final String rowsClass = txtRowsClass.getValue();
        if (rowsClass == null || rowsClass.isEmpty()) {
            cnf.setRowsClass(null);
        } else {
            // try parse URI
            try {
                new java.net.URI(rowsClass);
            } catch (URISyntaxException ex) {
                throw new DPUConfigException(this.ctx.tr("dialog.errors.row.class.uri.invalid"), ex);
            }
            cnf.setRowsClass(txtRowsClass.getValue());
        }
        //
        // additional checks
        //
        if (!cnf.isGenerateNew() && cnf.getColumnsInfo().isEmpty() && cnf.getColumnsInfoAdv().isEmpty()) {
            throw new DPUConfigException(this.ctx.tr("dialog.errors.column.mapping"));
        }

        return cnf;
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder();

        return desc.toString();
    }

    private void loadColumnMapping(Map<String, ColumnInfo_V1> basic,
            List<TabularConfig_V2.AdvanceMapping> advance) {
        //
        // column info basic
        //
        int index = 0;
        for (String key : basic.keySet()) {
            final ColumnInfo_V1 info = basic.get(key);
            if (index >= basisMapping.size()) {
                addSimplePropertyMapping(key, info);
            } else {
                // use existing
                basisMapping.get(index).set(key, info);
            }
            index++;
        }
        // clear old
        for (; index < basisMapping.size(); ++index) {
            basisMapping.get(index).clear();
        }
        //
        // column info advanced
        //
        index = 0;
        if (advance != null) {
            for (TabularConfig_V2.AdvanceMapping item : advance) {
                final String uri = item.getUri();
                final String template = item.getTemplate();
                if (index >= advancedMapping.size()) {
                    addAdvancedPropertyMapping(uri, template);
                } else {
                    // use existing
                    advancedMapping.get(index).set(uri, template);
                }
                index++;
            }
            // clear old
            for (; index < advancedMapping.size(); ++index) {
                advancedMapping.get(index).clear();
            }
        } else {
            LOG.debug("c.getColumnsInfoAdv() is null!");
        }
    }

    private void loadCellMapping(List<NamedCell_V1> namedCells) {
        int index = 0;
        for (NamedCell_V1 item : namedCells) {
            if (index >= xlsNamedCells.size()) {
                addXlsMapping(item.getName(), item.getColumnNumber(),
                        item.getRowNumber());
            } else {
                xlsNamedCells.get(index).set(item);
            }

            index++;
        }
        // clear old
        for (; index < xlsNamedCells.size(); ++index) {
            xlsNamedCells.get(index).clear();
        }
    }

    private void storeColumnMapping(Map<String, ColumnInfo_V1> basic,
            List<TabularConfig_V2.AdvanceMapping> advance) throws DPUConfigException {
        //
        // column info basic
        //
        for (PropertyGroup item : basisMapping) {
            final String name = item.getColumnName();
            if (name != null && !name.isEmpty()) {
                basic.put(name, item.get());
            }
        }
        //
        // column info advanced
        //
        for (PropertyGroupAdv item : advancedMapping) {
            final String uri = item.getUri();
            final String template = item.getTemplate();
            if (uri != null && template != null
                    && !uri.isEmpty() && !template.isEmpty()) {
                advance.add(new TabularConfig_V2.AdvanceMapping(uri, template));
            }
        }
    }

    private void storeCellMapping(List<NamedCell_V1> namedCells) throws DPUConfigException {
        for (PropertyNamedCell item : xlsNamedCells) {
            NamedCell_V1 namedCell = item.get();
            if (namedCell != null) {
                namedCells.add(namedCell);
            }
        }
    }

}
