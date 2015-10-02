package eu.unifiedviews.plugins.transformer.filestordft;

import org.openrdf.rio.RDFFormat;

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class FilesToRDFVaadinDialog extends AbstractDialog<FilesToRDFConfig_V1> {

    private static final long serialVersionUID = -5668436075836909428L;

    private final ObjectProperty<Integer> commitSize = new ObjectProperty<>(0);

    private ComboBox comboFailPolicy;

    private ComboBox comboOutputGraph;

    private ComboBox comboTypeOfGraph;

    private TextField txtSymbolicName;

    public FilesToRDFVaadinDialog() {
        super(FilesToRDF.class);
    }

    @Override
    protected void buildDialogLayout() {
        // top-level component properties
        setSizeFull();

        Panel panel = new Panel();
        panel.setSizeFull();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(false);
        mainLayout.setSpacing(true);
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");

        mainLayout.addComponent(new TextField(ctx.tr("FilesToRDFVaadinDialog.commitSize"), commitSize));

        comboFailPolicy = new ComboBox(ctx.tr("FilesToRDFVaadinDialog.comboFailPolicy"));
        comboFailPolicy.addItem(FilesToRDFConfig_V1.SKIP_CONTINUE_NEXT_FILE_ERROR_HANDLING);
        comboFailPolicy.setItemCaption(FilesToRDFConfig_V1.SKIP_CONTINUE_NEXT_FILE_ERROR_HANDLING, ctx.tr("FilesToRDFVaadinDialog.comboFailPolicy.SKIP_CONTINUE_NEXT_FILE_ERROR_HANDLING"));
        comboFailPolicy.addItem(FilesToRDFConfig_V1.STOP_EXTRACTION_ERROR_HANDLING);
        comboFailPolicy.setItemCaption(FilesToRDFConfig_V1.STOP_EXTRACTION_ERROR_HANDLING, ctx.tr("FilesToRDFVaadinDialog.comboFailPolicy.STOP_EXTRACTION_ERROR_HANDLING"));
        comboFailPolicy.setInvalidAllowed(false);
        comboFailPolicy.setNullSelectionAllowed(false);
        mainLayout.addComponent(comboFailPolicy);

        HorizontalLayout bottomLayout = new HorizontalLayout();
        comboOutputGraph = new ComboBox(ctx.tr("FilesToRDFVaadinDialog.comboOutputGraph"));
        comboOutputGraph.addItem(FilesToRDFConfig_V1.USE_INPUT_SYMBOLIC_NAME);
        comboOutputGraph.setItemCaption(FilesToRDFConfig_V1.USE_INPUT_SYMBOLIC_NAME, ctx.tr("FilesToRDFVaadinDialog.comboOutputGraph.USE_INPUT_SYMBOLIC_NAME"));
        comboOutputGraph.addItem(FilesToRDFConfig_V1.USE_FIXED_SYMBOLIC_NAME);
        comboOutputGraph.setItemCaption(FilesToRDFConfig_V1.USE_FIXED_SYMBOLIC_NAME, ctx.tr("FilesToRDFVaadinDialog.comboOutputGraph.USE_FIXED_SYMBOLIC_NAME"));
        comboOutputGraph.setInvalidAllowed(false);
        comboOutputGraph.setNullSelectionAllowed(false);
        comboOutputGraph.setImmediate(true);

        comboTypeOfGraph = new ComboBox(ctx.tr("FilesToRDFVaadinDialog.comboTypeOfGraph"));
        comboTypeOfGraph.addItem("AUTO");
        for (RDFFormat o : RDFFormat.values()) {
            comboTypeOfGraph.addItem(o.getDefaultMIMEType());
            comboTypeOfGraph.setItemCaption(o.getDefaultMIMEType(), o.getName());
        }
        comboTypeOfGraph.setInvalidAllowed(false);
        comboTypeOfGraph.setNullSelectionAllowed(false);
        comboTypeOfGraph.setImmediate(true);

        bottomLayout.addComponent(comboTypeOfGraph);
        bottomLayout.addComponent(comboOutputGraph);
        mainLayout.addComponent(bottomLayout);

        txtSymbolicName = new TextField(ctx.tr("FilesToRDFVaadinDialog.txtSymbolicName"));
        txtSymbolicName.setDescription(ctx.tr("FilesToRDFVaadinDialog.txtSymbolicName.description"));
        txtSymbolicName.setWidth("100%");
        txtSymbolicName.setInputPrompt("auto");
        txtSymbolicName.setNullSettingAllowed(true);
        txtSymbolicName.setNullRepresentation("");
        mainLayout.addComponent(txtSymbolicName);

        comboOutputGraph.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                txtSymbolicName.setEnabled(FilesToRDFConfig_V1.USE_FIXED_SYMBOLIC_NAME.equals(event.getProperty().getValue()));
            }
        });

        panel.setContent(mainLayout);
        setCompositionRoot(panel);
    }

    @Override
    public void setConfiguration(FilesToRDFConfig_V1 conf) throws DPUConfigException {
        commitSize.setValue(conf.getCommitSize());
        comboFailPolicy.setValue(conf.getFatalErrorHandling());
        comboOutputGraph.setValue(conf.getOutputNaming());
        comboTypeOfGraph.setValue(conf.getOutputType());
        txtSymbolicName.setValue(conf.getOutputSymbolicName());
        txtSymbolicName.setEnabled(FilesToRDFConfig_V1.USE_FIXED_SYMBOLIC_NAME.equals(comboOutputGraph.getValue()));
    }

    @Override
    public FilesToRDFConfig_V1 getConfiguration() throws DPUConfigException {
        FilesToRDFConfig_V1 conf = new FilesToRDFConfig_V1();
        conf.setCommitSize(commitSize.getValue());
        conf.setFatalErrorHandling(comboFailPolicy.getValue().toString());
        conf.setOutputNaming(comboOutputGraph.getValue().toString());
        conf.setOutputType(comboTypeOfGraph.getValue().toString());
        conf.setOutputSymbolicName(txtSymbolicName.getValue());
        return conf;
    }

}
