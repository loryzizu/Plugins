package eu.unifiedviews.plugins.loader.relationaltosql;

import com.vaadin.data.Property;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import eu.unifiedviews.plugins.loader.relationaltosql.DatabaseConfig.DatabaseType;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

public class RelationalToSqlVaadinDialog extends AbstractDialog<RelationalToSqlConfig_V1> {

    private static final long serialVersionUID = 1680354540248433247L;

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

    private TextField txtTableName;

    private CheckBox chckClearTable;

    private CheckBox chckDropTable;

    private VerticalLayout mainLayout;

    private Button btnTestConnection;

    private Label lblTestConnection;

    public RelationalToSqlVaadinDialog() {
        super(RelationalToSql.class);
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
        this.databaseType.setCaption(ctx.tr("dialog.dbload.dbtype"));
        this.databaseType.addItems(DatabaseConfig.getSupportedDatabases());
        this.databaseType.setNullSelectionAllowed(false);
        this.databaseType.setImmediate(true);
        DatabaseInfo defaultDb = DatabaseConfig.getDatabaseInfo(DatabaseConfig.DatabaseType.POSTGRES);
        this.databaseType.select(defaultDb);
        this.databaseType.addValueChangeListener(createDatabaseTypeChangeListener());
        this.mainLayout.addComponent(this.databaseType);

        this.txtDatabaseHost = new TextField();
        this.txtDatabaseHost.setCaption(ctx.tr("dialog.dbload.dbhost"));
        this.txtDatabaseHost.setRequired(true);
        this.txtDatabaseHost.setImmediate(true);
        this.txtDatabaseHost.setNullRepresentation("");
        this.txtDatabaseHost.setWidth("100%");
        this.mainLayout.addComponent(this.txtDatabaseHost);

        this.txtDatabasePort = new TextField();
        this.txtDatabasePort.setCaption(ctx.tr("dialog.dbload.dbport"));
        this.txtDatabasePort.setRequired(true);
        this.txtDatabasePort.setImmediate(true);
        this.txtDatabasePort.setWidth("100%");
        this.mainLayout.addComponent(this.txtDatabasePort);

        this.txtDatabaseName = new TextField();
        this.txtDatabaseName.setCaption(ctx.tr("dialog.dbload.dbname"));
        this.txtDatabaseName.setRequired(true);
        this.txtDatabaseName.setImmediate(true);
        this.txtDatabaseName.setNullRepresentation("");
        this.txtDatabaseName.setWidth("100%");
        this.mainLayout.addComponent(this.txtDatabaseName);

        this.txtInstanceName = new TextField();
        this.txtInstanceName.setCaption(ctx.tr("dialog.dbload.instance"));
        this.txtInstanceName.setNullRepresentation("");
        this.txtInstanceName.setImmediate(true);
        this.txtInstanceName.setWidth("100%");
        this.txtInstanceName.setVisible(false);
        this.mainLayout.addComponent(this.txtInstanceName);

        this.txtUserName = new TextField();
        this.txtUserName.setCaption(ctx.tr("dialog.dbload.username"));
        this.txtUserName.setRequired(true);
        this.txtUserName.setImmediate(true);
        this.txtUserName.setNullRepresentation("");
        this.txtUserName.setWidth("100%");
        this.mainLayout.addComponent(this.txtUserName);

        this.txtPassword = new PasswordField();
        this.txtPassword.setCaption(ctx.tr("dialog.dbload.password"));
        this.txtPassword.setRequired(true);
        this.txtPassword.setImmediate(true);
        this.txtPassword.setNullRepresentation("");
        this.txtPassword.setWidth("100%");
        this.mainLayout.addComponent(this.txtPassword);

        this.chckUseSsl = new CheckBox();
        this.chckUseSsl.setCaption(ctx.tr("dialog.dbload.usessl"));
        this.chckUseSsl.addValueChangeListener(createSslValueChangeListener());
        this.mainLayout.addComponent(this.chckUseSsl);

        this.txtTruststoreLocation = new TextField();
        this.txtTruststoreLocation.setCaption(ctx.tr("dialog.dbload.truststore.location"));
        this.txtTruststoreLocation.setDescription(ctx.tr("dialog.dbload.truststore"));
        this.txtTruststoreLocation.setVisible(false);
        this.txtTruststoreLocation.setNullRepresentation("");
        this.txtTruststoreLocation.setWidth("100%");
        this.mainLayout.addComponent(this.txtTruststoreLocation);

        this.txtTruststorePassword = new PasswordField();
        this.txtTruststorePassword.setCaption(ctx.tr("dialog.dbload.truststore.password"));
        this.txtTruststorePassword.setNullRepresentation("");
        this.txtTruststorePassword.setWidth("100%");
        this.txtTruststorePassword.setVisible(false);
        this.mainLayout.addComponent(this.txtTruststorePassword);

        this.btnTestConnection = new Button();
        this.btnTestConnection.setCaption(ctx.tr("dialog.dbload.testdb"));
        this.btnTestConnection.addClickListener(createTestClickListener());
        this.mainLayout.addComponent(this.btnTestConnection);

        this.lblTestConnection = new Label();
        this.lblTestConnection.setWidth("100%");
        this.lblTestConnection.setValue("");
        this.mainLayout.addComponent(this.lblTestConnection);

        this.txtTableName = new TextField();
        this.txtTableName.setCaption(ctx.tr("dialog.dbload.tablename"));
        this.txtTableName.setRequired(true);
        this.txtTableName.setNullRepresentation("");
        this.txtTableName.setImmediate(true);
        this.txtTableName.setWidth("100%");
        this.txtTableName.setDescription(ctx.tr("dialog.dbload.tooltip.tablename"));

        this.mainLayout.addComponent(this.txtTableName);

        this.chckClearTable = new CheckBox();
        this.chckClearTable.setCaption(ctx.tr("dialog.dbload.cleartable"));
        this.mainLayout.addComponent(this.chckClearTable);

        this.chckDropTable = new CheckBox();
        this.chckDropTable.setCaption(ctx.tr("dialog.dbload.droptable"));
        this.mainLayout.addComponent(this.chckDropTable);

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
    private Property.ValueChangeListener createDatabaseTypeChangeListener() {
        Property.ValueChangeListener listener = new Property.ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unqualified-field-access")
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                DatabaseInfo dbInfo = (DatabaseInfo) databaseType.getValue();
                txtDatabasePort.setValue(String.valueOf(dbInfo.getDefaultPort()));
                if (dbInfo.getDatabaseType() == DatabaseType.ORACLE) {
                    txtDatabaseName.setCaption(ctx.tr("dialog.dbload.dbsid"));
                } else {
                    txtDatabaseName.setCaption(ctx.tr("dialog.dbload.dbname"));
                }

                if (dbInfo.getDatabaseType() == DatabaseType.MSSQL) {
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
    private Property.ValueChangeListener createSslValueChangeListener() {
        Property.ValueChangeListener listener = new Property.ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unqualified-field-access")
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
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
                    bTestResult = RelationalToSqlHelper.testDatabaseConnection(getDatabaseConfiguration());
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

    private RelationalToSqlConfig_V1 getDatabaseConfiguration() {
        RelationalToSqlConfig_V1 config = new RelationalToSqlConfig_V1();

        config.setDatabaseHost(this.txtDatabaseHost.getValue());
        config.setDatabasePort(Integer.parseInt(this.txtDatabasePort.getValue()));
        config.setDatabaseName(this.txtDatabaseName.getValue());
        config.setUserName(this.txtUserName.getValue());
        config.setUserPassword(this.txtPassword.getValue());
        config.setUseSSL(this.chckUseSsl.getValue());
        config.setDatabaseType(((DatabaseInfo) this.databaseType.getValue()).getDatabaseType());
        config.setTruststoreLocation(this.txtTruststoreLocation.getValue());
        config.setTruststorePassword(this.txtTruststorePassword.getValue());

        return config;
    }

    /**
     * Check required parameters for GUI database operations, e.g. connection check
     */
    private boolean checkConnectionParametersInput() {
        boolean bResult = this.txtDatabaseHost.isValid() && this.txtDatabaseName.isValid() && this.txtUserName.isValid()
                && this.txtPassword.isValid();

        return bResult;
    }

    @Override
    protected RelationalToSqlConfig_V1 getConfiguration() throws DPUConfigException {
        RelationalToSqlConfig_V1 config = new RelationalToSqlConfig_V1();

        boolean isValid = true;

        if (((DatabaseInfo) this.databaseType.getValue()).getDatabaseType() == DatabaseType.MSSQL) {
            isValid = isValid && this.txtInstanceName.isValid();
        }
        if (!isValid) {
            throw new DPUConfigException(ctx.tr("dialog.errors.params"));
        }

        config.setDatabaseHost(this.txtDatabaseHost.getValue());
        config.setDatabasePort(Integer.parseInt(this.txtDatabasePort.getValue()));
        config.setDatabaseName(this.txtDatabaseName.getValue());
        config.setUserName(this.txtUserName.getValue());
        config.setUserPassword(this.txtPassword.getValue());
        config.setUseSSL(this.chckUseSsl.getValue());
        config.setDatabaseType(((DatabaseInfo) this.databaseType.getValue()).getDatabaseType());
        config.setInstanceName(this.txtInstanceName.getValue());
        config.setTruststoreLocation(this.txtTruststoreLocation.getValue());
        config.setTruststorePassword(this.txtTruststorePassword.getValue());
        config.setTableNamePrefix(this.txtTableName.getValue());
        config.setClearTargetTable(this.chckClearTable.getValue());
        config.setDropTargetTable(this.chckDropTable.getValue());

        return config;
    }

    @Override
    protected void setConfiguration(RelationalToSqlConfig_V1 config) throws DPUConfigException {

        if (config.getDatabaseType() != null) {
            this.databaseType.select(DatabaseConfig.getDatabaseInfo(config.getDatabaseType()));
        } else {
            this.databaseType.select(DatabaseConfig.getDatabaseInfo(DatabaseType.POSTGRES));
        }
        this.txtDatabaseHost.setValue(config.getDatabaseHost());
        if (config.getDatabasePort() != 0) {
            this.txtDatabasePort.setValue(String.valueOf(config.getDatabasePort()));
        } else {
            int defaultPort = DatabaseConfig.getDatabaseInfo(DatabaseType.POSTGRES).getDefaultPort();
            this.txtDatabasePort.setValue(String.valueOf(defaultPort));
        }
        this.txtDatabaseName.setValue(config.getDatabaseName());
        this.txtUserName.setValue(config.getUserName());
        this.txtPassword.setValue(config.getUserPassword());
        this.chckUseSsl.setValue(config.isUseSSL());
        this.txtInstanceName.setValue(config.getInstanceName());
        this.txtTruststoreLocation.setValue(config.getTruststoreLocation());
        this.txtTruststorePassword.setValue(config.getTruststorePassword());

        this.txtTableName.setValue(config.getTableNamePrefix());
        this.chckClearTable.setValue(config.isClearTargetTable());
        this.chckDropTable.setValue(config.isDropTargetTable());
    }

}
