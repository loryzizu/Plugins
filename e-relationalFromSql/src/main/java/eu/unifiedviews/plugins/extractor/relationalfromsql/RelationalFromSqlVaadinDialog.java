package eu.unifiedviews.plugins.extractor.relationalfromsql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;
import eu.unifiedviews.helpers.dpu.config.InitializableConfigDialog;
import eu.unifiedviews.helpers.dpu.localization.Messages;
import eu.unifiedviews.plugins.extractor.relationalfromsql.SqlDatabase.DatabaseType;

public class RelationalFromSqlVaadinDialog extends BaseConfigDialog<RelationalFromSqlConfig_V2> implements InitializableConfigDialog {

    private static final long serialVersionUID = -6978431151165728797L;

    private Messages messages;

    private VerticalLayout mainLayout;

    private NativeSelect databaseType;

    private TextField txtDatabaseHost;

    private TextField txtDatabasePort;

    private TextField txtDatabaseName;

    private TextField txtUserName;

    private PasswordField txtPassword;

    private CheckBox chckUseSsl;

    private TextField txtTargetTableName;

    private TextArea txtSqlQuery;

    private Button btntestConnection;

    private TextField txtPrimaryKeys;

    private Button btnPreview;

    private Button btnCreateQuery;

    public RelationalFromSqlVaadinDialog() {
        super(RelationalFromSqlConfig_V2.class);
    }

    @Override
    public void initialize() {
        this.messages = new Messages(getContext().getLocale(), this.getClass().getClassLoader());

        setWidth("100%");
        setHeight("100%");

        this.mainLayout = new VerticalLayout();
        this.mainLayout.setImmediate(false);
        this.mainLayout.setWidth("100%");
        this.mainLayout.setHeight("-1px");
        this.mainLayout.setSpacing(true);
        this.mainLayout.setMargin(false);

        this.databaseType = new NativeSelect();
        this.databaseType.setCaption(this.messages.getString("dialog.extractdb.dbtype"));
        this.databaseType.addItems(SqlDatabase.getDatabaseTypeNames());
        this.databaseType.setNullSelectionAllowed(false);
        this.databaseType.setImmediate(true);
        String defaultDbName = SqlDatabase.getDatabaseNameForDatabaseType(DatabaseType.POSTGRES);
        this.databaseType.select(defaultDbName);
        this.databaseType.addValueChangeListener(createDatabaseTypeChangeListener());
        this.mainLayout.addComponent(this.databaseType);

        this.txtDatabaseHost = new TextField();
        this.txtDatabaseHost.setCaption(this.messages.getString("dialog.extractdb.dbhost"));
        this.txtDatabaseHost.setRequired(true);
        this.txtDatabaseHost.setNullRepresentation("");
        this.txtDatabaseHost.setWidth("100%");
        this.mainLayout.addComponent(this.txtDatabaseHost);

        this.txtDatabasePort = new TextField();
        this.txtDatabasePort.setCaption(this.messages.getString("dialog.extractdb.dbport"));
        this.txtDatabasePort.setRequired(true);
        this.txtDatabasePort.setWidth("100%");
        this.mainLayout.addComponent(this.txtDatabasePort);

        this.txtDatabaseName = new TextField();
        this.txtDatabaseName.setCaption(this.messages.getString("dialog.extractdb.dbname"));
        this.txtDatabaseName.setRequired(true);
        this.txtDatabaseName.setNullRepresentation("");
        this.txtDatabaseName.setWidth("100%");
        this.mainLayout.addComponent(this.txtDatabaseName);

        this.txtUserName = new TextField();
        this.txtUserName.setCaption(this.messages.getString("dialog.extractdb.username"));
        this.txtUserName.setRequired(true);
        this.txtUserName.setNullRepresentation("");
        this.txtUserName.setWidth("100%");
        this.mainLayout.addComponent(this.txtUserName);

        this.txtPassword = new PasswordField();
        this.txtPassword.setCaption(this.messages.getString("dialog.extractdb.password"));
        this.txtPassword.setRequired(true);
        this.txtPassword.setNullRepresentation("");
        this.txtPassword.setWidth("100%");
        this.mainLayout.addComponent(this.txtPassword);

        this.chckUseSsl = new CheckBox();
        this.chckUseSsl.setCaption(this.messages.getString("dialog.extractdb.usessl"));
        this.mainLayout.addComponent(this.chckUseSsl);

        this.btntestConnection = new Button();
        this.btntestConnection.setCaption(this.messages.getString("dialog.extractdb.testdb"));
        this.btntestConnection.addClickListener(createTestClickListener());
        this.mainLayout.addComponent(this.btntestConnection);

        this.txtTargetTableName = new TextField();
        this.txtTargetTableName.setCaption(this.messages.getString("dialog.extractdb.targettable"));
        this.txtTargetTableName.setDescription(this.messages.getString("dialog.extractdb.tabledescr"));
        this.txtTargetTableName.setRequired(true);
        this.txtTargetTableName.setNullRepresentation("");
        this.txtTargetTableName.setWidth("100%");
        this.mainLayout.addComponent(this.txtTargetTableName);

        this.txtSqlQuery = new TextArea();
        this.txtSqlQuery.setCaption(this.messages.getString("dialog.extractdb.query"));
        this.txtSqlQuery.setRequired(true);
        this.txtSqlQuery.setNullRepresentation("");
        this.txtSqlQuery.setWidth("100%");
        this.txtSqlQuery.setHeight("125px");
        this.txtSqlQuery.setInputPrompt(this.messages.getString("dialog.extractdb.query.prompt"));
        this.mainLayout.addComponent(this.txtSqlQuery);

        this.btnPreview = new Button();
        this.btnPreview.setCaption(this.messages.getString("dialog.extractdb.preview"));
        this.btnPreview.addClickListener(createPreviewInitListener());

        this.btnCreateQuery = new Button();
        this.btnCreateQuery.setCaption(this.messages.getString("dialog.extractdb.createquery"));
        this.btnCreateQuery.addClickListener(createSelectQueryListener());

        HorizontalLayout queryButtons = new HorizontalLayout();
        queryButtons.setMargin(true);
        queryButtons.addComponent(this.btnCreateQuery);
        queryButtons.addComponent(this.btnPreview);
        this.mainLayout.addComponent(queryButtons);

        this.txtPrimaryKeys = new TextField();
        this.txtPrimaryKeys.setCaption(this.messages.getString("dialog.extractdb.keys"));
        this.txtPrimaryKeys.setDescription(this.messages.getString("dialog.extractdb.keysdescr"));
        this.txtPrimaryKeys.setNullRepresentation("");
        this.txtPrimaryKeys.setWidth("100%");
        this.mainLayout.addComponent(this.txtPrimaryKeys);

        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setContent(this.mainLayout);
        setCompositionRoot(panel);
    }

    private ValueChangeListener createDatabaseTypeChangeListener() {
        ValueChangeListener listener = new ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unqualified-field-access")
            @Override
            public void valueChange(ValueChangeEvent event) {
                String databaseName = (String) ((databaseType.getValue() != null) ? databaseType.getValue() : databaseType.getNullSelectionItemId());
                int portForSelectedDbType = SqlDatabase.getDefaultDatabasePort(SqlDatabase.getDatabaseTypeForDatabaseName(databaseName));
                txtDatabasePort.setValue(String.valueOf(portForSelectedDbType));
            }
        };
        return listener;
    }

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
        Notification notification = new Notification(this.messages.getString(messageResource), type);
        notification.show(Page.getCurrent());
    }

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

    private Window createSelectTableWindow() {
        final Window window = new Window();
        window.setWidth(250.0f, Unit.PIXELS);
        window.center();
        VerticalLayout layout = new VerticalLayout();
        window.setContent(layout);
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setWidth("100%");
        layout.setHeight("-1px");

        List<String> tables = null;
        try {
            tables = RelationalFromSqlHelper.getTablesInSourceDatabase(getConfigurationInternal());
        } catch (SQLException e) {
            showMessage("dialog.errors.select.tables", Notification.Type.ERROR_MESSAGE);
            return null;
        }
        final ListSelect tableSelect = new ListSelect(this.messages.getString("dialog.extractdb.tables"), tables);
        tableSelect.setRows(7);
        tableSelect.setNullSelectionAllowed(false);
        layout.addComponent(tableSelect);

        Button btnClose = new Button();
        btnClose.setCaption(this.messages.getString("dialog.extractdb.close"));
        btnClose.addClickListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                window.close();
            }
        });

        Button btnCreate = new Button();
        btnCreate.setCaption(this.messages.getString("dialog.extractdb.createsql"));
        btnCreate.addClickListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                String tableName = (String) tableSelect.getValue();
                try {
                    if (checkConnectionParametersInput()) {
                        List<String> tableColumns = RelationalFromSqlHelper.getColumnsForTable(getConfiguration(), tableName);
                        String query = generateSelectForTable(tableName, tableColumns);
                        RelationalFromSqlVaadinDialog.this.txtSqlQuery.setValue(query);
                    } else {
                        showMessage("dialog.messages.dbparams", Notification.Type.WARNING_MESSAGE);
                    }
                } catch (SQLException | DPUConfigException e) {
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

    private Window createPreviewWindow() {
        final Window window = new Window();
        window.setWidth(250.0f, Unit.PIXELS);
        window.center();

        VerticalLayout layout = new VerticalLayout();
        window.setContent(layout);
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setWidth("100%");
        layout.setHeight("-1px");

        final TextField txtLimit = new TextField();
        txtLimit.setCaption(this.messages.getString("dialog.preview.limit"));
        txtLimit.setWidth("100%");
        txtLimit.setValue("100");
        layout.addComponent(txtLimit);

        Button btnPreview = new Button();
        btnPreview.setCaption(this.messages.getString("dialog.extractdb.preview"));
        btnPreview.addClickListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    window.close();
                    DataPreviewWindow dataPreview = new DataPreviewWindow(getConfigurationInternal(), Integer.parseInt(txtLimit.getValue()));
                    UI.getCurrent().addWindow(dataPreview);
                } catch (SQLException e) {
                    showMessage("dialog.errors.preview", Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        layout.addComponent(btnPreview);

        return window;
    }

    private String generateSelectForTable(String tableName, List<String> columns) {
        StringBuilder query = new StringBuilder("SELECT ");
        for (String column : columns) {
            query.append("\t");
            if (column.contains(" ")) {
                query.append("\"");
                query.append(column);
                query.append("\"");
            } else {
                query.append(column);
            }

            query.append(",\n");
        }
        query.setLength(query.length() - 2);
        query.append("\n");
        query.append(" FROM ");
        query.append(tableName);

        return query.toString();
    }

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

    private static String getPrimaryKeysAsCommaSeparatedString(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(key);
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);

        return sb.toString();
    }

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
        this.txtPrimaryKeys.setValue(getPrimaryKeysAsCommaSeparatedString(config.getPrimaryKeyColumns()));
    }

    @Override
    protected RelationalFromSqlConfig_V2 getConfiguration() throws DPUConfigException {

        boolean isValid = this.txtDatabaseHost.isValid() && this.txtDatabaseName.isValid() && this.txtDatabasePort.isValid()
                && this.txtUserName.isValid() && this.txtPassword.isValid();
        if (!isValid) {
            throw new DPUConfigException(this.messages.getString("dialog.errors.params"));
        }

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

        return config;
    }

}
