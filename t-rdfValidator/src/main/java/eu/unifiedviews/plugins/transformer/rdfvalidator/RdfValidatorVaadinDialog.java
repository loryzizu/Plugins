package eu.unifiedviews.plugins.transformer.rdfvalidator;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

/**
 * DPU's configuration dialog.
 */
public class RdfValidatorVaadinDialog extends AbstractDialog<RdfValidatorConfig_V1> {

    /**
     *
     */
    private static final long serialVersionUID = 518622055536470336L;

    private ObjectProperty<Boolean> failExecution = new ObjectProperty<Boolean>(Boolean.TRUE);

    private ObjectProperty<String> query = new ObjectProperty<String>("");

    private ObjectProperty<Boolean> perGraph = new ObjectProperty<Boolean>(Boolean.TRUE);

    private ObjectProperty<String> outputGraphSymbolicName = new ObjectProperty<String>("");

    public RdfValidatorVaadinDialog() {
        super(RdfValidator.class);
    }

    @Override
    protected void buildDialogLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");

        TextArea txtQuery = new TextArea(ctx.tr("dialog.query"), query);
        txtQuery.setWidth("100%");
        txtQuery.setSizeFull();
        txtQuery.setNullRepresentation("");
        txtQuery.setNullSettingAllowed(true);
        mainLayout.addComponent(txtQuery);
        mainLayout.setExpandRatio(txtQuery, 1.0f);

        VerticalLayout bottomLayout = new VerticalLayout();

        bottomLayout.addComponent(new CheckBox(ctx.tr("dialog.messageType.fail"), failExecution));

        CheckBox chkPerGraph = new CheckBox(ctx.tr("dialog.perGraph"), perGraph);
        bottomLayout.addComponent(chkPerGraph);

        final TextField txtOutputGraphSymbolicName = new TextField(ctx.tr("dialog.outputGraphSymbolicName"), outputGraphSymbolicName);
        bottomLayout.addComponent(txtOutputGraphSymbolicName);

        chkPerGraph.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (Boolean.TRUE.equals(event.getProperty().getValue())) {
                    txtOutputGraphSymbolicName.setEnabled(true);
                } else {
                    txtOutputGraphSymbolicName.setEnabled(false);
                }
            }
        });

        mainLayout.addComponent(bottomLayout);
        mainLayout.setExpandRatio(bottomLayout, 0.1f);
        setCompositionRoot(mainLayout);
    }

    @Override
    public void setConfiguration(RdfValidatorConfig_V1 c) throws DPUConfigException {
        failExecution.setValue(c.isFailExecution());
        query.setValue(c.getQuery());
        perGraph.setValue(c.isPerGraph());
        outputGraphSymbolicName.setValue(c.getOutputGraphSymbolicName());
    }

    @Override
    public RdfValidatorConfig_V1 getConfiguration() throws DPUConfigException {
        final RdfValidatorConfig_V1 c = new RdfValidatorConfig_V1();
        c.setFailExecution(failExecution.getValue());
        c.setQuery(query.getValue());
        c.setPerGraph(perGraph.getValue());
        c.setOutputGraphSymbolicName(outputGraphSymbolicName.getValue());
        return c;
    }
}
