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
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

public class RelationalToSqlVaadinDialog extends AbstractDialog<RelationalToSqlConfig_V1> {

    private static final long serialVersionUID = 1680354540248433247L;

    private TextField txtDatabaseURL;

    private TextField txtUserName;

    private PasswordField txtPassword;

    private TextField txtTableName;

    private CheckBox chckUseSsl;

    private CheckBox chckClearTable;

    private CheckBox chckDropTable;

    VerticalLayout mainLayout;

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

        this.txtDatabaseURL = new TextField();
        this.txtDatabaseURL.setCaption(ctx.tr("dialog.dbload.dbURL"));
        this.txtDatabaseURL.setRequired(true);
        this.txtDatabaseURL.setNullRepresentation("");
        this.txtDatabaseURL.setWidth("100%");
        this.mainLayout.addComponent(this.txtDatabaseURL);

        this.txtUserName = new TextField();
        this.txtUserName.setCaption(ctx.tr("dialog.dbload.username"));
        this.txtUserName.setRequired(true);
        this.txtUserName.setNullRepresentation("");
        this.txtUserName.setWidth("100%");
        this.mainLayout.addComponent(this.txtUserName);

        this.txtPassword = new PasswordField();
        this.txtPassword.setCaption(ctx.tr("dialog.dbload.password"));
        this.txtPassword.setRequired(true);
        this.txtPassword.setNullRepresentation("");
        this.txtPassword.setWidth("100%");
        this.mainLayout.addComponent(this.txtPassword);

        this.chckUseSsl = new CheckBox();
        this.chckUseSsl.setCaption(ctx.tr("dialog.dbload.usessl"));
        this.mainLayout.addComponent(this.chckUseSsl);

        this.btnTestConnection = new Button();
        this.btnTestConnection.setCaption(ctx.tr("dialog.extractdb.testdb"));
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
                        RelationalToSqlVaadinDialog.this.lblTestConnection.setValue(ctx.tr("dialog.messages.testsuccess"));
                    } else {
                        RelationalToSqlVaadinDialog.this.lblTestConnection.setValue(ctx.tr("dialog.messages.testfail"));
                    }
                } else {
                    RelationalToSqlVaadinDialog.this.lblTestConnection.setValue(ctx.tr("dialog.messages.dbparams"));
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
            throw new DPUConfigException(ctx.tr("errors.dialog.missing"));
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
