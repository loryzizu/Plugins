package eu.unifiedviews.plugins.transformer.relationaltordf;

import java.net.URISyntaxException;

import com.vaadin.data.Property;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.plugins.transformer.relationaltordf.column.ColumnInfo_V1;
import eu.unifiedviews.plugins.transformer.relationaltordf.column.ColumnType;

/**
 * Component for setting basic mapping.
 *
 * @author Škoda Petr
 */
public class PropertyGroup {

    private final TextField columnName;

    private final ComboBox columnType;

    private final TextField uri;

    private final CheckBox typeFromDbf;

    private final TextField language;

    public PropertyGroup(GridLayout propertiesLayout) {
        this.columnName = new TextField();
        this.columnName.setWidth("100%");
        this.columnName.setNullSettingAllowed(true);
        this.columnName.setNullRepresentation("");
        propertiesLayout.addComponent(this.columnName);

        this.columnType = new ComboBox();
        this.columnType.setWidth("7em");
        this.columnType.setNullSelectionAllowed(false);
        for (ColumnType type : ColumnType.values()) {
            this.columnType.addItem(type);
        }
        this.columnType.select(ColumnType.Auto);
        this.columnType.setImmediate(true);
        this.columnType.setNewItemsAllowed(false);
        propertiesLayout.addComponent(this.columnType);

        this.language = new TextField();
        this.language.setWidth("7em");
        this.language.setNullSettingAllowed(true);
        this.language.setNullRepresentation("");
        propertiesLayout.addComponent(this.language);

        this.columnType.addValueChangeListener(new Property.ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                updateEnabled((ColumnType) event.getProperty()
                        .getValue());
            }
        });

        this.typeFromDbf = new CheckBox();
        this.typeFromDbf.setWidth("7em");
        propertiesLayout.addComponent(this.typeFromDbf);

        this.uri = new TextField();
        this.uri.setWidth("100%");
        this.uri.setNullSettingAllowed(true);
        this.uri.setNullRepresentation("");
        propertiesLayout.addComponent(this.uri);

        updateEnabled((ColumnType) this.columnType.getValue());
    }

    public void set(String columnName, ColumnInfo_V1 info) {
        this.columnName.setValue(columnName);
        this.columnType.setValue(info.getType());
        this.uri.setValue(info.getURI());
        this.typeFromDbf.setValue(info.isUseTypeFromDfb());
        this.language.setValue(info.getLanguage());

        updateEnabled(info.getType());
    }

    public ColumnInfo_V1 get() throws DPUConfigException {
        final ColumnInfo_V1 info = new ColumnInfo_V1();

        // validate URI
        try {
            if (this.uri.getValue() != null) {
                new java.net.URI(this.uri.getValue());
            }
        } catch (URISyntaxException ex) {
            throw new DPUConfigException("Invalid 'Property URI'.", ex);
        }

        info.setType((ColumnType) this.columnType.getValue());
        info.setURI(this.uri.getValue());
        if (info.getType() == ColumnType.Auto) {
            info.setUseTypeFromDfb(this.typeFromDbf.getValue());
        } else {
            info.setUseTypeFromDfb(null);
        }

        if (info.getType() == ColumnType.String) {
            info.setLanguage(this.language.getValue());
        } else {
            info.setLanguage(null);
        }

        return info;
    }

    public String getColumnName() {
        return this.columnName.getValue();
    }

    public void clear() {
        this.columnName.setValue(null);
        this.columnType.setValue(ColumnType.Auto);
        this.uri.setValue(null);
        this.typeFromDbf.setValue(false);
        this.language.setValue(null);

        updateEnabled((ColumnType) this.columnType.getValue());
    }

    /**
     * Based on given value set {@link ColumnType} dependent components to
     * enabled/disabled.
     *
     * @param type
     */
    private void updateEnabled(ColumnType type) {
        this.language.setEnabled(type == ColumnType.String);
        this.typeFromDbf.setEnabled(type == ColumnType.Auto);
    }

}
