package eu.unifiedviews.plugins.extractor.relationalfromsql;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;
import eu.unifiedviews.helpers.dpu.config.InitializableConfigDialog;
import eu.unifiedviews.helpers.dpu.localization.Messages;

public class RelationalFromSqlVaadinDialog extends BaseConfigDialog<RelationalFromSqlConfig_V1> implements InitializableConfigDialog {

    private static final long serialVersionUID = 325928627730169135L;

    private Messages messages;

    private VerticalLayout mainLayout;

    private TextField txtDatabaseURL;

    private TextField txtUserName;

    private PasswordField txtPassword;

    private CheckBox chckUseSsl;

    private TextField txtTargetTableName;

    private TextArea txtSqlQuery;

    private Button btntestConnection;

    private Label lblTestConnection;

    private TextField txtPrimaryKeys;

    public RelationalFromSqlVaadinDialog() {
        super(RelationalFromSqlConfig_V1.class);
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
        this.txtDatabaseURL.setCaption(this.messages.getString("dialog.extractdb.dbURL"));
        this.txtDatabaseURL.setRequired(true);
        this.txtDatabaseURL.setNullRepresentation("");
        this.txtDatabaseURL.setWidth("100%");
        this.txtDatabaseURL.setDescription(this.messages.getString("dialog.extractdb.urldescription"));
        this.txtDatabaseURL.setInputPrompt(this.messages.getString("dialog.extractdb.urlprompt"));
        this.mainLayout.addComponent(this.txtDatabaseURL);

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

        this.txtPrimaryKeys = new TextField();
        this.txtPrimaryKeys.setCaption(this.messages.getString("dialog.dbtransform.keys"));
        this.txtPrimaryKeys.setDescription(this.messages.getString("dialog.dbtransform.keysdescr"));
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
    protected void setConfiguration(RelationalFromSqlConfig_V1 config) throws DPUConfigException {
        this.txtDatabaseURL.setValue(config.getDatabaseURL());
        this.txtUserName.setValue(config.getUserName());
        this.txtPassword.setValue(config.getUserPassword());
        this.chckUseSsl.setValue(config.isUseSSL());
        this.txtSqlQuery.setValue(config.getSqlQuery());
        this.txtTargetTableName.setValue(config.getTargetTableName());
        this.txtPrimaryKeys.setValue(getPrimaryKeysAsCommaSeparatedString(config.getPrimaryKeyColumns()));
    }

    @Override
    protected RelationalFromSqlConfig_V1 getConfiguration() throws DPUConfigException {
        RelationalFromSqlConfig_V1 config = new RelationalFromSqlConfig_V1();
        config.setDatabaseURL(this.txtDatabaseURL.getValue());
        config.setUserName(this.txtUserName.getValue());
        config.setUserPassword(this.txtPassword.getValue());
        config.setUseSSL(this.chckUseSsl.getValue());
        config.setSqlQuery(this.txtSqlQuery.getValue());
        config.setTargetTableName(this.txtTargetTableName.getValue());
        config.setPrimaryKeyColumns(getPrimaryKeyColumns());

        return config;
    }

}
