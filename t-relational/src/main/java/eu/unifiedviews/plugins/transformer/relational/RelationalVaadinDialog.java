package eu.unifiedviews.plugins.transformer.relational;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;
import eu.unifiedviews.helpers.dpu.config.InitializableConfigDialog;
import eu.unifiedviews.helpers.dpu.localization.Messages;

public class RelationalVaadinDialog extends BaseConfigDialog<RelationalConfig_V1> implements InitializableConfigDialog {

    private static final long serialVersionUID = 7069074978935972335L;

    private Messages messages;

    private VerticalLayout mainLayout;

    private TextField txtTargetTableName;

    private TextArea txtSqlQuery;

    private TextField txtPrimaryKeys;

    private TextField txtIndexedColumns;

    public RelationalVaadinDialog() {
        super(RelationalConfig_V1.class);
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

        this.txtSqlQuery = new TextArea();
        this.txtSqlQuery.setCaption(this.messages.getString("dialog.dbtransform.query"));
        this.txtSqlQuery.setRequired(true);
        this.txtSqlQuery.setNullRepresentation("");
        this.txtSqlQuery.setWidth("100%");
        this.txtSqlQuery.setHeight("150px");
        this.txtSqlQuery.addValidator(createQueryValidator());
        this.mainLayout.addComponent(this.txtSqlQuery);

        this.txtTargetTableName = new TextField();
        this.txtTargetTableName.setCaption(this.messages.getString("dialog.dbtransform.targettable"));
        this.txtTargetTableName.setDescription(this.messages.getString("dialog.dbtransform.tabledescr"));
        this.txtTargetTableName.setRequired(true);
        this.txtTargetTableName.setNullRepresentation("");
        this.txtTargetTableName.setWidth("100%");
        this.mainLayout.addComponent(this.txtTargetTableName);

        this.txtPrimaryKeys = new TextField();
        this.txtPrimaryKeys.setCaption(this.messages.getString("dialog.dbtransform.keys"));
        this.txtPrimaryKeys.setDescription(this.messages.getString("dialog.dbtransform.keysdescr"));
        this.txtPrimaryKeys.setNullRepresentation("");
        this.txtPrimaryKeys.setWidth("100%");
        this.mainLayout.addComponent(this.txtPrimaryKeys);

        this.txtIndexedColumns = new TextField();
        this.txtIndexedColumns.setCaption(this.messages.getString("dialog.dbtransform.indexes"));
        this.txtIndexedColumns.setDescription(this.messages.getString("dialog.dbtransform.indexdescr"));
        this.txtIndexedColumns.setNullRepresentation("");
        this.txtIndexedColumns.setWidth("100%");
        this.mainLayout.addComponent(this.txtIndexedColumns);

        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setContent(this.mainLayout);
        setCompositionRoot(panel);
    }

    private Validator createQueryValidator() {
        Validator validator = new Validator() {

            private static final long serialVersionUID = -186376062628005948L;

            @Override
            public void validate(Object value) throws InvalidValueException {
                String query = ((String) value).trim();
                if (!query.toLowerCase().startsWith("select")) {
                    throw new InvalidValueException(RelationalVaadinDialog.this.messages.getString("dialog.errors.validation.select"));
                }
            }
        };

        return validator;
    }

    private List<String> getPrimaryKeyColumnsAsList() {
        List<String> keyColumns = new ArrayList<>();
        if (this.txtPrimaryKeys.getValue() != null && !this.txtPrimaryKeys.getValue().equals("")) {
            String[] keys = this.txtPrimaryKeys.getValue().trim().split(",");
            for (String key : keys) {
                keyColumns.add(key.trim().toUpperCase());
            }
        }

        return keyColumns;
    }

    private List<String> getIndexedColumnsAsList() {
        List<String> keyColumns = new ArrayList<>();
        if (this.txtIndexedColumns.getValue() != null && !this.txtIndexedColumns.getValue().equals("")) {
            String[] keys = this.txtIndexedColumns.getValue().trim().split(",");
            for (String key : keys) {
                keyColumns.add(key.trim().toUpperCase());
            }
        }

        return keyColumns;
    }

    @Override
    protected void setConfiguration(RelationalConfig_V1 config) throws DPUConfigException {
        this.txtTargetTableName.setValue(config.getTargetTableName());
        this.txtSqlQuery.setValue(config.getSqlQuery());
        this.txtPrimaryKeys.setValue(DatabaseHelper.getListAsCommaSeparatedString(config.getPrimaryKeyColumns()));
        this.txtIndexedColumns.setValue(DatabaseHelper.getListAsCommaSeparatedString(config.getIndexedColumns()));
    }

    @Override
    protected RelationalConfig_V1 getConfiguration() throws DPUConfigException {
        RelationalConfig_V1 config = new RelationalConfig_V1();
        boolean isValid = this.txtTargetTableName.isValid();
        if (!isValid) {
            throw new DPUConfigException(this.messages.getString("errors.dialog.missing"));
        }
        try {
            this.txtSqlQuery.validate();
        } catch (EmptyValueException e) {
            throw new DPUConfigException(this.messages.getString("errors.dialog.missing"));
        } catch (InvalidValueException e) {
            throw new DPUConfigException(e.getMessage());
        }
        config.setTargetTableName(this.txtTargetTableName.getValue());

        config.setSqlQuery(this.txtSqlQuery.getValue());
        config.setPrimaryKeyColumns(getPrimaryKeyColumnsAsList());
        config.setIndexedColumns(getIndexedColumnsAsList());

        return config;
    }

}
