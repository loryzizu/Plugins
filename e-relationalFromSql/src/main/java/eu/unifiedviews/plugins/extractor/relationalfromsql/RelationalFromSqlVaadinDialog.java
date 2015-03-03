package eu.unifiedviews.plugins.extractor.relationalfromsql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;
import eu.unifiedviews.helpers.dpu.config.InitializableConfigDialog;
import eu.unifiedviews.helpers.dpu.localization.Messages;

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

    private Label lblTestConnection;

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
        this.txtDatabasePort.setNullRepresentation("");
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

        this.lblTestConnection = new Label();
        this.lblTestConnection.setWidth("100%");
        this.lblTestConnection.setValue("");
        this.mainLayout.addComponent(this.lblTestConnection);

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

        this.btnCreateQuery = new Button();
        this.btnCreateQuery.setCaption(this.messages.getString("dialog.extractdb.createquery"));
        this.btnCreateQuery.addClickListener(createCreateQueryListener());

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

    private ClickListener createTestClickListener() {
        ClickListener listener = new ClickListener() {

            private static final long serialVersionUID = -3540329527677997780L;

            @Override
            public void buttonClick(ClickEvent event) {
                RelationalFromSqlVaadinDialog.this.lblTestConnection.setValue("");
                if (checkConnectionParametersInput()) {
                    boolean bTestResult = true;
                    try {
                        bTestResult = RelationalFromSqlHelper.testDatabaseConnection(getConfiguration());
                    } catch (DPUConfigException e) {
                        bTestResult = false;
                    }
                    if (bTestResult) {
                        RelationalFromSqlVaadinDialog.this.lblTestConnection.setValue(RelationalFromSqlVaadinDialog.this.messages.getString("dialog.messages.testsuccess"));
                    } else {
                        RelationalFromSqlVaadinDialog.this.lblTestConnection.setValue(RelationalFromSqlVaadinDialog.this.messages.getString("dialog.messages.testfail"));
                    }
                } else {
                    RelationalFromSqlVaadinDialog.this.lblTestConnection.setValue(RelationalFromSqlVaadinDialog.this.messages.getString("dialog.messages.dbparams"));
                }
            }
        };

        return listener;
    }

    private ClickListener createCreateQueryListener() {
        ClickListener listener = new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                UI.getCurrent().addWindow(createSelectTableWindow());
            }
        };
        return listener;
    }

    private Window createSelectTableWindow() {
        final Window window = new Window();
        window.setWidth(200.0f, Unit.PIXELS);
        window.center();
        VerticalLayout layout = new VerticalLayout();
        window.setContent(layout);
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setWidth("100%");
        layout.setHeight("-1px");

        List<String> tables = null;
        try {
            tables = RelationalFromSqlHelper.getTablesInSourceDatabase(getConfiguration());
        } catch (SQLException | DPUConfigException e) {
            // TODO: handle
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
                    List<String> tableColumns = RelationalFromSqlHelper.getColumnsForTable(getConfiguration(), tableName);
                    String query = generateSelectForTable(tableName, tableColumns);
                    RelationalFromSqlVaadinDialog.this.txtSqlQuery.setValue(query);
                } catch (SQLException | DPUConfigException e) {
                    //TODO: handle
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

    private String generateSelectForTable(String tableName, List<String> columns) {
        StringBuilder query = new StringBuilder("SELECT ");
        for (String column : columns) {
            query.append("\t");
            query.append(column);
            query.append(",\n");
        }
        query.setLength(query.length() - 2);
        query.append("\n");
        query.append(" FROM ");
        query.append(tableName);

        return query.toString();
    }

    private boolean checkConnectionParametersInput() {
        boolean bResult = true;
        if (this.txtDatabaseHost.getValue() == null || this.txtDatabaseHost.getValue().equals("")) {
            bResult = false;
        }
        if (this.txtUserName.getValue() == null || this.txtUserName.getValue().equals("")) {
            bResult = false;
        }
        if (this.txtPassword.getValue() == null || this.txtPassword.getValue().equals("")) {
            bResult = false;
        }

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

    @Override
    protected void setConfiguration(RelationalFromSqlConfig_V2 config) throws DPUConfigException {
        this.databaseType.select(SqlDatabase.getDatabaseNameForDatabaseType(config.getDatabaseType()));
        this.txtDatabaseHost.setValue(config.getDatabaseHost());
        this.txtDatabasePort.setValue(String.valueOf(config.getDatabasePort()));
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
