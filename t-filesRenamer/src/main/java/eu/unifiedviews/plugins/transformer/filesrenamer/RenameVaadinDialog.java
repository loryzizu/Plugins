package eu.unifiedviews.plugins.transformer.filesrenamer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Table;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;
import eu.unifiedviews.helpers.dpu.config.InitializableConfigDialog;
import eu.unifiedviews.helpers.dpu.localization.Messages;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class RenameVaadinDialog extends
        BaseConfigDialog<RenameConfig_V1> implements InitializableConfigDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 63148374398039L;

    private static final Logger log = LoggerFactory
            .getLogger(RenameVaadinDialog.class);

    private RenamerComponent component;

    /**
     * Data for example.
     */
    private String[] exampleArray = { "/example/log1.txt", "/example/log2.txt", "/example/data.zip", "/example/noextension" };

    public RenameVaadinDialog() {
        super(RenameConfig_V1.class);
    }

    public void initialize() {
        Messages messages = new Messages(this.getContext().getLocale(), this.getClass().getClassLoader());

        setWidth("100%");
        setHeight("100%");

        this.component = new RenamerComponent(messages);
        recomputeExampleTable();

        // register listeners for input controller, and recompute example table if any change occurs
        component.getMaskTextField().addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                recomputeExampleTable();
            }
        });
        component.getMaskTextField().setImmediate(true);

        component.getExtensionMaskTextField().addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                recomputeExampleTable();
            }
        });
        component.getExtensionMaskTextField().setImmediate(true);

        component.getCounterStartStepper().addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                recomputeExampleTable();
            }
        });
        component.getCounterStartStepper().setImmediate(true);

        component.getCounterStepStepper().addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                recomputeExampleTable();
            }
        });
        component.getCounterStepStepper().setImmediate(true);

        component.getCounterDigitsStepper().addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                recomputeExampleTable();
            }
        });
        component.getCounterDigitsStepper().setImmediate(true);

        setCompositionRoot(component);
    }

    @Override
    public void setConfiguration(RenameConfig_V1 config)
            throws DPUConfigException {
        component.setConfig(config);
    }

    @Override
    public RenameConfig_V1 getConfiguration()
            throws DPUConfigException {
        return component.getConfig();
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder();

        return desc.toString();
    }

    /**
     * Recreate example table with changed configuration.
     */
    private void recomputeExampleTable() {
        Table table = component.getTable();
        table.removeAllItems();

        RenamerEngine engine = new RenamerEngine();
        engine.setConfig(component.getConfig());
        engine.initialize();
        for (int i = 0; i < exampleArray.length; i++) {
            table.addItem(new Object[] { exampleArray[i], engine.renameNext(exampleArray[i]) }, i);
        }
    }
}
