package eu.unifiedviews.plugins.loader.relationaltosql;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;
import eu.unifiedviews.helpers.dpu.config.InitializableConfigDialog;
import eu.unifiedviews.helpers.dpu.localization.Messages;

public class RelationalToSqlVaadinDialog extends BaseConfigDialog<RelationalToSqlConfig_V1> implements InitializableConfigDialog {

    private static final long serialVersionUID = 1680354540248433247L;

    private TextField txtDatabaseURL;

    private TextField txtUserName;

    private PasswordField txtPassword;

    private TextField txtTableName;

    private CheckBox chckUseSsl;

    private CheckBox chckClearTable;

    private CheckBox chckDropTable;

    private Messages messages;

    VerticalLayout mainLayout;

    private Button btnTestConnection;

    private Label lblTestConnection;

    public RelationalToSqlVaadinDialog() {
        super(RelationalToSqlConfig_V1.class);
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

        this.txtDatabaseURL = new TextField();
        this.txtDatabaseURL.setCaption(this.messages.getString("dialog.dbload.dbURL"));
        this.txtDatabaseURL.setRequired(true);
        this.txtDatabaseURL.setNullRepresentation("");
        this.txtDatabaseURL.setWidth("100%");
        this.mainLayout.addComponent(this.txtDatabaseURL);

        this.txtUserName = new TextField();
        this.txtUserName.setCaption(this.messages.getString("dialog.dbload.username"));
        this.txtUserName.setRequired(true);
        this.txtUserName.setNullRepresentation("");
        this.txtUserName.setWidth("100%");
        this.mainLayout.addComponent(this.txtUserName);

        this.txtPassword = new PasswordField();
        this.txtPassword.setCaption(this.messages.getString("dialog.dbload.password"));
        this.txtPassword.setRequired(true);
        this.txtPassword.setNullRepresentation("");
        this.txtPassword.setWidth("100%");
        this.mainLayout.addComponent(this.txtPassword);

        this.chckUseSsl = new CheckBox();
        this.chckUseSsl.setCaption(this.messages.getString("dialog.dbload.usessl"));
        this.mainLayout.addComponent(this.chckUseSsl);

        this.btnTestConnection = new Button();
        this.btnTestConnection.setCaption(this.messages.getString("dialog.extractdb.testdb"));
        this.btnTestConnection.addClickListener(createTestClickListener());
        this.mainLayout.addComponent(this.btnTestConnection);

        this.lblTestConnection = new Label();
        this.lblTestConnection.setWidth("100%");
        this.lblTestConnection.setValue("");
        this.mainLayout.addComponent(this.lblTestConnection);

        this.txtTableName = new TextField();
        this.txtTableName.setCaption(this.messages.getString("dialog.dbload.tablename"));
        this.txtTableName.setRequired(true);
        this.txtTableName.setNullRepresentation("");
        this.txtTableName.setWidth("100%");
        this.txtTableName.setDescription(this.messages.getString("dialog.dbload.tooltip.tablename"));

        this.mainLayout.addComponent(this.txtTableName);

        this.chckClearTable = new CheckBox();
        this.chckClearTable.setCaption(this.messages.getString("dialog.dbload.cleartable"));
        this.mainLayout.addComponent(this.chckClearTable);

        this.chckDropTable = new CheckBox();
        this.chckDropTable.setCaption(this.messages.getString("dialog.dbload.droptable"));
        this.mainLayout.addComponent(this.chckDropTable);

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
                RelationalToSqlVaadinDialog.this.lblTestConnection.setValue("");
                if (checkConnectionParametersInput()) {
                    boolean bTestResult = true;
                    try {
                        bTestResult = RelationalToSqlHelper.testDatabaseConnection(getDatabaseConfiguration());
                    } catch (DPUConfigException e) {
                        bTestResult = false;
                    }
                    if (bTestResult) {
                        RelationalToSqlVaadinDialog.this.lblTestConnection.setValue(RelationalToSqlVaadinDialog.this.messages.getString("dialog.messages.testsuccess"));
                    } else {
                        RelationalToSqlVaadinDialog.this.lblTestConnection.setValue(RelationalToSqlVaadinDialog.this.messages.getString("dialog.messages.testfail"));
                    }
                } else {
                    RelationalToSqlVaadinDialog.this.lblTestConnection.setValue(RelationalToSqlVaadinDialog.this.messages.getString("dialog.messages.dbparams"));
                }
            }
        };

        return listener;
    }

    private RelationalToSqlConfig_V1 getDatabaseConfiguration() throws DPUConfigException {
        RelationalToSqlConfig_V1 config = new RelationalToSqlConfig_V1();

        config.setDatabaseURL(this.txtDatabaseURL.getValue());
        config.setUserName(this.txtUserName.getValue());
        config.setUserPassword(this.txtPassword.getValue());
        config.setUseSSL(this.chckUseSsl.getValue());

        return config;
    }

    private boolean checkConnectionParametersInput() {
        boolean bResult = true;
        if (this.txtDatabaseURL.getValue() == null || this.txtDatabaseURL.getValue().equals("")) {
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

    @Override
    protected RelationalToSqlConfig_V1 getConfiguration() throws DPUConfigException {
        RelationalToSqlConfig_V1 config = new RelationalToSqlConfig_V1();

        final boolean isValid = this.txtDatabaseURL.isValid() && this.txtUserName.isValid() && this.txtPassword.isValid()
                && this.txtTableName.isValid();
        if (!isValid) {
            throw new DPUConfigException(this.messages.getString("errors.dialog.missing"));
        }

        config.setDatabaseURL(this.txtDatabaseURL.getValue());
        config.setUserName(this.txtUserName.getValue());
        config.setUserPassword(this.txtPassword.getValue());
        config.setUseSSL(this.chckUseSsl.getValue());
        config.setTableNamePrefix(this.txtTableName.getValue());
        config.setClearTargetTable(this.chckClearTable.getValue());
        config.setDropTargetTable(this.chckDropTable.getValue());

        return config;
    }

    @Override
    protected void setConfiguration(RelationalToSqlConfig_V1 config) throws DPUConfigException {
        this.txtDatabaseURL.setValue(config.getDatabaseURL());
        this.txtUserName.setValue(config.getUserName());
        this.chckUseSsl.setValue(config.isUseSSL());
        this.txtTableName.setValue(config.getTableNamePrefix());
        this.chckClearTable.setValue(config.isClearTargetTable());
        this.chckDropTable.setValue(config.isDropTargetTable());
        this.txtPassword.setValue(config.getUserPassword());
    }

}
