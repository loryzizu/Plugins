package eu.unifiedviews.plugins.loader.database;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;
import eu.unifiedviews.helpers.dpu.config.InitializableConfigDialog;
import eu.unifiedviews.helpers.dpu.localization.Messages;

public class DatabaseVaadinDialog extends BaseConfigDialog<DatabaseConfig_V1> implements InitializableConfigDialog {

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

    public DatabaseVaadinDialog() {
        super(DatabaseConfig_V1.class);
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

        this.txtTableName = new TextField();
        this.txtTableName.setCaption(this.messages.getString("dialog.dbload.tablename"));
        this.txtTableName.setRequired(true);
        this.txtTableName.setNullRepresentation("");
        this.txtTableName.setWidth("100%");

        this.mainLayout.addComponent(this.txtTableName);

        this.chckClearTable = new CheckBox();
        this.chckClearTable.setCaption(this.messages.getString("dialog.dbload.cleartable"));
        this.mainLayout.addComponent(this.chckClearTable);

        this.chckDropTable = new CheckBox();
        this.chckDropTable.setCaption(this.messages.getString("dialog.dbload.droptable"));
        this.mainLayout.addComponent(this.chckDropTable);

        setCompositionRoot(this.mainLayout);
    }

    @Override
    protected DatabaseConfig_V1 getConfiguration() throws DPUConfigException {
        DatabaseConfig_V1 config = new DatabaseConfig_V1();

        config.setDatabaseURL(this.txtDatabaseURL.getValue());
        config.setUserName(this.txtUserName.getValue());
        config.setUserPassword(this.txtPassword.getValue());
        config.setUseSSL(this.chckUseSsl.getValue());
        config.setTableName(this.txtTableName.getValue());
        config.setClearTargetTable(this.chckClearTable.getValue());
        config.setDropTargetTable(this.chckDropTable.getValue());

        return config;
    }

    @Override
    protected void setConfiguration(DatabaseConfig_V1 config) throws DPUConfigException {
        this.txtDatabaseURL.setValue(config.getDatabaseURL());
        this.txtUserName.setValue(config.getUserName());
        this.chckUseSsl.setValue(config.isUseSSL());
        this.txtTableName.setValue(config.getTableName());
        this.chckClearTable.setValue(config.isClearTargetTable());
        this.chckDropTable.setValue(config.isDropTargetTable());
        this.txtPassword.setValue(config.getUserPassword());
    }

}
