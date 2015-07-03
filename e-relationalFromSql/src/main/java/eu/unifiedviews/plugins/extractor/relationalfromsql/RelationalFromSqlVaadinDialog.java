package eu.unifiedviews.plugins.extractor.relationalfromsql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;
import eu.unifiedviews.plugins.extractor.relationalfromsql.SqlDatabase.DatabaseType;

public class RelationalFromSqlVaadinDialog extends AbstractDialog<RelationalFromSqlConfig_V2> {

    private static final long serialVersionUID = -6978431151165728797L;

    private VerticalLayout mainLayout;

    private NativeSelect databaseType;

    private TextField txtDatabaseHost;

    private TextField txtDatabasePort;

    private TextField txtDatabaseName;

    private TextField txtInstanceName;

    private TextField txtUserName;

    private PasswordField txtPassword;

    private CheckBox chckUseSsl;

    private TextField txtTruststoreLocation;

    private PasswordField txtTruststorePassword;

    private TextField txtTargetTableName;

    private TextArea txtSqlQuery;

    private Button btntestConnection;

    private TextField txtPrimaryKeys;

    private TextField txtIndexes;

    private Button btnPreview;

    private Button btnCreateQuery;

    public RelationalFromSqlVaadinDialog() {
        super(RelationalFromSql.class);
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

        this.databaseType = new NativeSelect();
        this.databaseType.setCaption(ctx.tr("dialog.extractdb.dbtype"));
        this.databaseType.addItems(SqlDatabase.getDatabaseTypeNames());
        this.databaseType.setNullSelectionAllowed(false);
        this.databaseType.setImmediate(true);
        String defaultDbName = SqlDatabase.getDatabaseNameForDatabaseType(DatabaseType.POSTGRES);
        this.databaseType.select(defaultDbName);
        this.databaseType.addValueChangeListener(createDatabaseTypeChangeListener());
        this.mainLayout.addComponent(this.databaseType);

        this.txtDatabaseHost = new TextField();
        this.txtDatabaseHost.setCaption(ctx.tr("dialog.extractdb.dbhost"));
        this.txtDatabaseHost.setRequired(true);
        this.txtDatabaseHost.setNullRepresentation("");
        this.txtDatabaseHost.setWidth("100%");
        this.mainLayout.addComponent(this.txtDatabaseHost);

        this.txtDatabasePort = new TextField();
        this.txtDatabasePort.setCaption(ctx.tr("dialog.extractdb.dbport"));
        this.txtDatabasePort.setRequired(true);
        this.txtDatabasePort.setWidth("100%");
        this.mainLayout.addComponent(this.txtDatabasePort);

        this.txtDatabaseName = new TextField();
        this.txtDatabaseName.setCaption(ctx.tr("dialog.extractdb.dbname"));
        this.txtDatabaseName.setRequired(true);
        this.txtDatabaseName.setNullRepresentation("");
        this.txtDatabaseName.setWidth("100%");
        this.mainLayout.addComponent(this.txtDatabaseName);

        this.txtInstanceName = new TextField();
        this.txtInstanceName.setCaption(ctx.tr("dialog.extractdb.instance"));
        this.txtInstanceName.setNullRepresentation("");
        this.txtInstanceName.setWidth("100%");
        this.txtInstanceName.setVisible(false);
        this.mainLayout.addComponent(this.txtInstanceName);

        this.txtUserName = new TextField();
        this.txtUserName.setCaption(ctx.tr("dialog.extractdb.username"));
        this.txtUserName.setRequired(true);
        this.txtUserName.setNullRepresentation("");
        this.txtUserName.setWidth("100%");
        this.mainLayout.addComponent(this.txtUserName);

        this.txtPassword = new PasswordField();
        this.txtPassword.setCaption(ctx.tr("dialog.extractdb.password"));
        this.txtPassword.setRequired(true);
        this.txtPassword.setNullRepresentation("");
        this.txtPassword.setWidth("100%");
        this.mainLayout.addComponent(this.txtPassword);

        this.chckUseSsl = new CheckBox();
        this.chckUseSsl.setCaption(ctx.tr("dialog.extractdb.usessl"));
        this.chckUseSsl.addValueChangeListener(createSslValueChangeListener());
        this.mainLayout.addComponent(this.chckUseSsl);

        this.txtTruststoreLocation = new TextField();
        this.txtTruststoreLocation.setCaption(ctx.tr("dialog.extractdb.truststore.location"));
        this.txtTruststoreLocation.setDescription(ctx.tr("dialog.extractdb.truststore"));
        this.txtTruststoreLocation.setVisible(false);
        this.txtTruststoreLocation.setNullRepresentation("");
        this.txtTruststoreLocation.setWidth("100%");
        this.mainLayout.addComponent(this.txtTruststoreLocation);

        this.txtTruststorePassword = new PasswordField();
        this.txtTruststorePassword.setCaption(ctx.tr("dialog.extractdb.truststore.password"));
        this.txtTruststorePassword.setNullRepresentation("");
        this.txtTruststorePassword.setWidth("100%");
        this.txtTruststorePassword.setVisible(false);
        this.mainLayout.addComponent(this.txtTruststorePassword);

        this.btntestConnection = new Button();
        this.btntestConnection.setCaption(ctx.tr("dialog.extractdb.testdb"));
        this.btntestConnection.addClickListener(createTestClickListener());
        this.mainLayout.addComponent(this.btntestConnection);

        this.txtTargetTableName = new TextField();
        this.txtTargetTableName.setCaption(ctx.tr("dialog.extractdb.targettable"));
        this.txtTargetTableName.setDescription(ctx.tr("dialog.extractdb.tabledescr"));
        this.txtTargetTableName.setRequired(true);
        this.txtTargetTableName.setNullRepresentation("");
        this.txtTargetTableName.setWidth("100%");
        this.mainLayout.addComponent(this.txtTargetTableName);

        this.txtSqlQuery = new TextArea();
        this.txtSqlQuery.setCaption(ctx.tr("dialog.extractdb.query"));
        this.txtSqlQuery.setRequired(true);
        this.txtSqlQuery.setNullRepresentation("");
        this.txtSqlQuery.setWidth("100%");
        this.txtSqlQuery.setHeight("125px");
        this.txtSqlQuery.setInputPrompt(ctx.tr("dialog.extractdb.query.prompt"));
        this.mainLayout.addComponent(this.txtSqlQuery);

        this.btnPreview = new Button();
        this.btnPreview.setCaption(ctx.tr("dialog.extractdb.preview"));
        this.btnPreview.addClickListener(createPreviewInitListener());

        this.btnCreateQuery = new Button();
        this.btnCreateQuery.setCaption(ctx.tr("dialog.extractdb.createquery"));
        this.btnCreateQuery.addClickListener(createSelectQueryListener());

        HorizontalLayout queryButtons = new HorizontalLayout();
        queryButtons.setMargin(true);
        queryButtons.addComponent(this.btnCreateQuery);
        queryButtons.addComponent(this.btnPreview);
        this.mainLayout.addComponent(queryButtons);

        this.txtPrimaryKeys = new TextField();
        this.txtPrimaryKeys.setCaption(ctx.tr("dialog.extractdb.keys"));
        this.txtPrimaryKeys.setDescription(ctx.tr("dialog.extractdb.keysdescr"));
        this.txtPrimaryKeys.setNullRepresentation("");
        this.txtPrimaryKeys.setWidth("100%");
        this.mainLayout.addComponent(this.txtPrimaryKeys);

        this.txtIndexes = new TextField();
        this.txtIndexes.setCaption(ctx.tr("dialog.extractdb.indexes"));
        this.txtIndexes.setDescription(ctx.tr("dialog.extractdb.indexdescr"));
        this.txtIndexes.setNullRepresentation("");
        this.txtIndexes.setWidth("100%");
        this.mainLayout.addComponent(this.txtIndexes);

        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setContent(this.mainLayout);
        setCompositionRoot(panel);
    }

    /**
     * Change listener for database value type
     * If database type changes, some optional specific fields can be shown / hidden
     * Default database port is automatically filled in when database type changes
     */
    private ValueChangeListener createDatabaseTypeChangeListener() {
        ValueChangeListener listener = new ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unqualified-field-access")
            @Override
            public void valueChange(ValueChangeEvent event) {
                String databaseName = (String) databaseType.getValue();
                int portForSelectedDbType = SqlDatabase.getDefaultDatabasePort(SqlDatabase.getDatabaseTypeForDatabaseName(databaseName));
                DatabaseType dbType = SqlDatabase.getDatabaseTypeForDatabaseName(databaseName);
                txtDatabasePort.setValue(String.valueOf(portForSelectedDbType));
                if (dbType == DatabaseType.ORACLE) {
                    txtDatabaseName.setCaption(ctx.tr("dialog.extractdb.dbsid"));
                } else {
                    txtDatabaseName.setCaption(ctx.tr("dialog.extractdb.dbname"));
                }

                if (dbType == DatabaseType.MSSQL) {
                    txtInstanceName.setVisible(true);
                } else {
                    txtInstanceName.setVisible(false);
                }
            }
        };
        return listener;
    }

    /**
     * Change listener for SSL checkbox. If checked, optional truststore configuration fields are shown
     */
    private ValueChangeListener createSslValueChangeListener() {
        ValueChangeListener listener = new ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unqualified-field-access")
            @Override
            public void valueChange(ValueChangeEvent event) {
                txtTruststoreLocation.setVisible(chckUseSsl.getValue());
                txtTruststorePassword.setVisible(chckUseSsl.getValue());
            }
        };

        return listener;
    }

    /**
     * Test connection button listener
     * When button is clicked, connection is tested and corresponding message is shown
     */
    private ClickListener createTestClickListener() {
        ClickListener listener = new ClickListener() {

            private static final long serialVersionUID = -3540329527677997780L;

            @Override
            public void buttonClick(ClickEvent event) {
                if (checkConnectionParametersInput()) {
                    boolean bTestResult = true;
                    bTestResult = RelationalFromSqlHelper.testDatabaseConnection(getConfigurationInternal());
                    if (bTestResult) {
                        showMessage("dialog.messages.testsuccess", Notification.Type.HUMANIZED_MESSAGE);
                    } else {
                        showMessage("dialog.messages.testfail", Notification.Type.ERROR_MESSAGE);
                    }
                } else {
                    showMessage("dialog.messages.dbparams", Notification.Type.WARNING_MESSAGE);
                }
            }
        };

        return listener;
    }

    private void showMessage(String messageResource, Notification.Type type) {
        Notification notification = new Notification(ctx.tr(messageResource), type);
        notification.show(Page.getCurrent());
    }

    /**
     * Generate select button click listener
     * When clicked, table with all tables from source database are shown
     */
    private ClickListener createSelectQueryListener() {
        ClickListener listener = new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                if (checkConnectionParametersInput()) {
                    Window tablesWindow = createSelectTableWindow();
                    if (tablesWindow != null) {
                        UI.getCurrent().addWindow(tablesWindow);
                    }
                } else {
                    showMessage("dialog.messages.dbparams", Notification.Type.WARNING_MESSAGE);
                }
            }
        };
        return listener;
    }

    /**
     * Preview data button click listener
     * When clicked, data preview windows is shown based on filled query
     */
    private ClickListener createPreviewInitListener() {
        ClickListener listener = new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                if (checkConnectionParametersInput() && RelationalFromSqlVaadinDialog.this.txtSqlQuery.isValid()) {
                    Window previewInit = createPreviewWindow();
                    UI.getCurrent().addWindow(previewInit);
                } else {
                    showMessage("dialog.messages.dbparams", Notification.Type.WARNING_MESSAGE);
                }
            }
        };

        return listener;
    }

    /**
     * Creates window with list of available tables from source database
     * When clicked on table, SQL select query is generated and shown in SQL query text area
     */
    private Window createSelectTableWindow() {
        final Window window = new Window();
        window.center();
        window.setModal(true);
        VerticalLayout layout = new VerticalLayout();
        window.setContent(layout);
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setWidth("100%");
        layout.setHeight("-1px");

        List<DatabaseTable> tables = null;
        try {
            tables = RelationalFromSqlHelper.getTablesInSourceDatabase(getConfigurationInternal());
        } catch (SQLException e) {
            showMessage("dialog.errors.select.tables", Notification.Type.ERROR_MESSAGE);
            return null;
        }
        final ListSelect tableSelect = new ListSelect(ctx.tr("dialog.extractdb.tables"), tables);
        tableSelect.setRows(7);
        tableSelect.setNullSelectionAllowed(false);
        layout.addComponent(tableSelect);

        Button btnClose = new Button();
        btnClose.setCaption(ctx.tr("dialog.extractdb.close"));
        btnClose.addClickListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                window.close();
            }
        });

        Button btnCreate = new Button();
        btnCreate.setCaption(ctx.tr("dialog.extractdb.createsql"));
        btnCreate.addClickListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                DatabaseTable table = (DatabaseTable) tableSelect.getValue();
                try {
                    if (checkConnectionParametersInput()) {
                        List<String> tableColumns = RelationalFromSqlHelper.getColumnsForTable(getConfigurationInternal(), table.getTableName());
                        String query = RelationalFromSqlHelper.generateSelectForTable(table, tableColumns);
                        RelationalFromSqlVaadinDialog.this.txtSqlQuery.setValue(query);
                    } else {
                        showMessage("dialog.messages.dbparams", Notification.Type.WARNING_MESSAGE);
                    }
                } catch (SQLException e) {
                    showMessage("dialog.errors.select.query", Notification.Type.ERROR_MESSAGE);
                }
                window.close();
            }
        });
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponent(btnCreate);
        buttons.addComponent(btnClose);
        layout.addComponent(buttons);

        return window;
    }

    /**
     * Creates preview window with text field with limit for select
     * After clicking Preview button, preview table window is displayed
     */
    private Window createPreviewWindow() {
        final Window window = new Window();
        window.setWidth(250.0f, Unit.PIXELS);
        window.setModal(true);
        window.center();

        VerticalLayout layout = new VerticalLayout();
        window.setContent(layout);
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setWidth("100%");
        layout.setHeight("-1px");

        final TextField txtLimit = new TextField();
        txtLimit.setCaption(ctx.tr("dialog.preview.limit"));
        txtLimit.setWidth("100%");
        txtLimit.setValue("100");
        txtLimit.addValidator(createSelectLimitValidator());
        layout.addComponent(txtLimit);

        Button btnPreview = new Button();
        btnPreview.setCaption(ctx.tr("dialog.extractdb.preview"));
        btnPreview.addClickListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    if (!txtLimit.isValid()) {
                        showMessage("dialog.errors.limit", Notification.Type.ERROR_MESSAGE);
                        return;
                    }
                    window.close();
                    DataPreviewWindow dataPreview = new DataPreviewWindow(getConfigurationInternal(), Integer.parseInt(txtLimit.getValue()));
                    UI.getCurrent().addWindow(dataPreview);
                } catch (Exception e) {
                    showMessage("dialog.errors.preview", Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        layout.addComponent(btnPreview);

        return window;
    }

    /**
     * Validator for preview data limit
     */
    private Validator createSelectLimitValidator() {
        Validator validator = new Validator() {

            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unqualified-field-access")
            @Override
            public void validate(Object value) throws InvalidValueException {
                int limit = 0;
                try {
                    limit = Integer.parseInt((String) value);
                } catch (NumberFormatException e) {
                    throw new InvalidValueException(ctx.tr("dialog.errors.limit"));
                }
                if (limit < 1 || limit > 10000) {
                    throw new InvalidValueException(ctx.tr("dialog.errors.limit"));
                }
            }
        };
        return validator;
    }

    /**
     * Check required parameters for GUI database operations, e.g. connection check
     */
    private boolean checkConnectionParametersInput() {
        boolean bResult = this.txtDatabaseHost.isValid() && this.txtDatabaseName.isValid() && this.txtUserName.isValid()
                && this.txtPassword.isValid();

        return bResult;
    }

    private List<String> getPrimaryKeyColumns() {
        List<String> keyColumns = new ArrayList<>();
        if (this.txtPrimaryKeys.getValue() != null && !this.txtPrimaryKeys.getValue().equals("")) {
            String[] keys = this.txtPrimaryKeys.getValue().trim().split(",");
            for (String key : keys) {
                keyColumns.add(key.trim().toUpperCase());
            }
        }

        return keyColumns;
    }

    private List<String> getIndexedColumns() {
        List<String> keyColumns = new ArrayList<>();
        if (this.txtIndexes.getValue() != null && !this.txtIndexes.getValue().equals("")) {
            String[] keys = this.txtIndexes.getValue().trim().split(",");
            for (String key : keys) {
                keyColumns.add(key.trim().toUpperCase());
            }
        }

        return keyColumns;
    }

    /**
     * This configuration is used for GUI methods and they no always need all required parameters to be set
     * 
     * @return Database configuration without checking
     */
    private RelationalFromSqlConfig_V2 getConfigurationInternal() {
        RelationalFromSqlConfig_V2 config = new RelationalFromSqlConfig_V2();
        config.setDatabaseHost(this.txtDatabaseHost.getValue());
        config.setDatabasePort(Integer.parseInt(this.txtDatabasePort.getValue()));
        config.setDatabaseName(this.txtDatabaseName.getValue());
        config.setUserName(this.txtUserName.getValue());
        config.setUserPassword(this.txtPassword.getValue());
        config.setUseSSL(this.chckUseSsl.getValue());
        config.setSqlQuery(this.txtSqlQuery.getValue());
        config.setTargetTableName(this.txtTargetTableName.getValue());
        config.setPrimaryKeyColumns(getPrimaryKeyColumns());
        config.setDatabaseType(SqlDatabase.getDatabaseTypeForDatabaseName((String) this.databaseType.getValue()));
        config.setTruststoreLocation(this.txtTruststoreLocation.getValue());
        config.setTruststorePassword(this.txtTruststorePassword.getValue());

        return config;
    }

    @Override
    protected void setConfiguration(RelationalFromSqlConfig_V2 config) throws DPUConfigException {
        if (config.getDatabaseType() != null) {
            this.databaseType.select(SqlDatabase.getDatabaseNameForDatabaseType(config.getDatabaseType()));
        } else {
            this.databaseType.select(SqlDatabase.getDatabaseNameForDatabaseType(DatabaseType.POSTGRES));
        }
        this.txtDatabaseHost.setValue(config.getDatabaseHost());
        if (config.getDatabasePort() != 0) {
            this.txtDatabasePort.setValue(String.valueOf(config.getDatabasePort()));
        } else {
            int defaultPort = SqlDatabase.getDefaultDatabasePort(DatabaseType.POSTGRES);
            this.txtDatabasePort.setValue(String.valueOf(defaultPort));
        }
        this.txtDatabaseName.setValue(config.getDatabaseName());
        this.txtUserName.setValue(config.getUserName());
        this.txtPassword.setValue(config.getUserPassword());
        this.chckUseSsl.setValue(config.isUseSSL());
        this.txtSqlQuery.setValue(config.getSqlQuery());
        this.txtTargetTableName.setValue(config.getTargetTableName());
        this.txtPrimaryKeys.setValue(RelationalFromSqlHelper.getListAsCommaSeparatedString(config.getPrimaryKeyColumns()));
        this.txtIndexes.setValue(RelationalFromSqlHelper.getListAsCommaSeparatedString(config.getIndexedColumns()));
        this.txtInstanceName.setValue(config.getInstanceName());
        this.txtTruststoreLocation.setValue(config.getTruststoreLocation());
        this.txtTruststorePassword.setValue(config.getTruststorePassword());
    }

    @Override
    protected RelationalFromSqlConfig_V2 getConfiguration() throws DPUConfigException {

        boolean isValid = this.txtDatabaseHost.isValid() && this.txtDatabaseName.isValid() && this.txtDatabasePort.isValid()
                && this.txtUserName.isValid() && this.txtPassword.isValid() && this.txtTargetTableName.isValid()
                && this.txtSqlQuery.isValid();

        if (SqlDatabase.getDatabaseTypeForDatabaseName((String) this.databaseType.getValue()) == DatabaseType.MSSQL) {
            isValid = isValid && this.txtInstanceName.isValid();
        }
        if (!isValid) {
            throw new DPUConfigException(ctx.tr("dialog.errors.params"));
        }

        RelationalFromSqlConfig_V2 config = new RelationalFromSqlConfig_V2();
        config.setDatabaseHost(this.txtDatabaseHost.getValue());
        config.setDatabasePort(Integer.parseInt(this.txtDatabasePort.getValue()));
        config.setDatabaseName(this.txtDatabaseName.getValue());
        config.setUserName(this.txtUserName.getValue());
        config.setUserPassword(this.txtPassword.getValue());
        config.setUseSSL(this.chckUseSsl.getValue());
        config.setSqlQuery(RelationalFromSqlHelper.getNormalizedQuery(this.txtSqlQuery.getValue()));
        config.setTargetTableName(this.txtTargetTableName.getValue());
        config.setPrimaryKeyColumns(getPrimaryKeyColumns());
        config.setIndexedColumns(getIndexedColumns());
        config.setDatabaseType(SqlDatabase.getDatabaseTypeForDatabaseName((String) this.databaseType.getValue()));
        config.setInstanceName(this.txtInstanceName.getValue());
        config.setTruststoreLocation(this.txtTruststoreLocation.getValue());
        config.setTruststorePassword(this.txtTruststorePassword.getValue());

        return config;
    }

}
