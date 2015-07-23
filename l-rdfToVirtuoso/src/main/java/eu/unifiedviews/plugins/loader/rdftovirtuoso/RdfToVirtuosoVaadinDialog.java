package eu.unifiedviews.plugins.loader.rdftovirtuoso;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.Validator;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.UserDialogContext;
import eu.unifiedviews.helpers.dpu.vaadin.validator.UrlValidator;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class RdfToVirtuosoVaadinDialog extends AbstractDialog<RdfToVirtuosoConfig_V1> {

    private static final long serialVersionUID = -5666076909428L;

    private ObjectProperty<String> virtuosoUrl = new ObjectProperty<String>("");

    private ObjectProperty<String> username = new ObjectProperty<String>("");

    private ObjectProperty<String> password = new ObjectProperty<String>("");

    private ObjectProperty<Boolean> clearDestinationGraph = new ObjectProperty<Boolean>(false);

    private ObjectProperty<String> targetGraphName = new ObjectProperty<String>("");

    private ObjectProperty<Integer> threadCount = new ObjectProperty<Integer>(1);

    private ObjectProperty<Boolean> perGraph = new ObjectProperty<Boolean>(
            false);

    private ObjectProperty<Boolean> skipOnError = new ObjectProperty<Boolean>(
            false);

    TextField targerGraphNameTextField;

    public RdfToVirtuosoVaadinDialog() {
        super(RdfToVirtuoso.class);
    }

    public void outerBuildDialogLayout(UserDialogContext ctx) {
        this.ctx = ctx;
        buildDialogLayout();
    }

    @Override
    protected void buildDialogLayout() {
        setSizeFull();
        FormLayout mainLayout = new FormLayout();

        // top-level component properties
        setWidth("100%");
        setHeight("100%");

        final PasswordField passwordField = new PasswordField(ctx.tr("RdfToVirtuosoVaadinDialog.password"), password);
        passwordField.setWidth("100%");

        mainLayout.addComponent(createTextField(ctx.tr("RdfToVirtuosoVaadinDialog.virtuosoUrl"), virtuosoUrl));
        mainLayout.addComponent(createTextField(ctx.tr("RdfToVirtuosoVaadinDialog.username"), username));
        mainLayout.addComponent(passwordField);
        mainLayout.addComponent(new CheckBox(ctx.tr("RdfToVirtuosoVaadinDialog.clearDestinationGraph"), clearDestinationGraph));
        targerGraphNameTextField = createTextField(ctx.tr("RdfToVirtuosoVaadinDialog.targetGraphName"), targetGraphName);
        final CheckBox perGraphCheckbox = new CheckBox(ctx.tr("RdfToVirtuosoVaadinDialog.perGraph"), perGraph);
        perGraphCheckbox.addValueChangeListener(new ValueChangeListener() {

            private static final long serialVersionUID = 6044061864546491939L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                targerGraphNameTextField.setEnabled(!perGraphCheckbox.getValue());
            }
        });
        targerGraphNameTextField.addValidator(new Validator() {

            @Override
            public void validate(Object value) throws InvalidValueException {
                if (value == null || StringUtils.isBlank((String) value)) {
                    if (!perGraphCheckbox.getValue()) {
                        throw new InvalidValueException(ctx.tr(RdfToVirtuosoVaadinDialog.this.getClass().getSimpleName() + ".exception.target.graph.name.empty"));
                    }
                }
            }
        });
        targerGraphNameTextField.addValidator(new UrlValidator(true, ctx.getDialogMasterContext().getDialogContext().getLocale()));
        targerGraphNameTextField.setImmediate(true);

        mainLayout.addComponent(perGraphCheckbox);
        mainLayout.addComponent(targerGraphNameTextField);
        mainLayout.addComponent(createTextField(ctx.tr(this.getClass().getSimpleName() + ".threadCount"), threadCount));
        mainLayout.addComponent(new CheckBox(ctx.tr(this.getClass().getSimpleName() + ".skipOnError"), skipOnError));

        setCompositionRoot(mainLayout);
    }

    private <T> TextField createTextField(String caption, ObjectProperty<T> property) {
        final TextField result = new TextField(caption, property);
        result.setWidth("100%");
        return result;
    }

    @Override
    public void setConfiguration(RdfToVirtuosoConfig_V1 conf) throws DPUConfigException {
        virtuosoUrl.setValue(conf.getVirtuosoUrl());
        username.setValue(conf.getUsername());
        password.setValue(conf.getPassword());
        clearDestinationGraph.setValue(conf.isClearDestinationGraph());
        perGraph.setValue(StringUtils.isEmpty(conf.getTargetGraphName()));
        targetGraphName.setValue(conf.getTargetGraphName());
        threadCount.setValue(conf.getThreadCount());
        skipOnError.setValue(conf.isSkipOnError());
    }

    @Override
    public RdfToVirtuosoConfig_V1 getConfiguration() throws DPUConfigException {
        RdfToVirtuosoConfig_V1 conf = new RdfToVirtuosoConfig_V1();
        conf.setVirtuosoUrl(virtuosoUrl.getValue());
        conf.setUsername(username.getValue());
        conf.setPassword(password.getValue());
        conf.setClearDestinationGraph(clearDestinationGraph.getValue());
        if (perGraph.getValue()) {
            conf.setTargetGraphName("");
        } else {
            if (!targerGraphNameTextField.isValid()) {
                throw new DPUConfigException(ctx.tr(this.getClass().getSimpleName() + ".validation.exception"));
            }
            conf.setTargetGraphName(targetGraphName.getValue());
        }
        conf.setThreadCount(threadCount.getValue());
        conf.setSkipOnError(skipOnError.getValue());
        return conf;
    }
}
