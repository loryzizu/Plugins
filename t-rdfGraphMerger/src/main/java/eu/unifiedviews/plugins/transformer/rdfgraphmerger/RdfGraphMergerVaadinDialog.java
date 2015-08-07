package eu.unifiedviews.plugins.transformer.rdfgraphmerger;

import java.lang.reflect.Field;
import java.net.URI;

import com.vaadin.data.Validatable;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;
import eu.unifiedviews.helpers.dpu.vaadin.validator.UrlValidator;

/**
 * DPU's configuration dialog.
 */
public class RdfGraphMergerVaadinDialog extends AbstractDialog<RdfGraphMergerConfig_V1> {

    BeanFieldGroup<RdfGraphMergerConfig_V1> binder;
    public RdfGraphMergerVaadinDialog() {
        super(RdfGraphMerger.class);
    }

    @Override
    public void setConfiguration(RdfGraphMergerConfig_V1 conf) throws DPUConfigException {
        binder.setItemDataSource(conf);
    }

    @Override
    public RdfGraphMergerConfig_V1 getConfiguration() throws DPUConfigException {
        try {
            if (binder.isValid()) {
                binder.commit();
            } else {
                throw new DPUConfigException(ctx.tr(this.getClass().getSimpleName() + ".validation.exception"));
            }
        } catch (CommitException ex) {
            throw new DPUConfigException(ctx.tr(this.getClass().getSimpleName() + ".validation.exception"));
        }
        return binder.getItemDataSource().getBean();
    }

    @Override
    protected void buildDialogLayout() {
        // common part: create layout
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setImmediate(true);
        mainLayout.setWidth("100%");
        mainLayout.setHeight(null);

        // top-level component properties
        setWidth("100%");
        setHeight("100%");
        binder = new BeanFieldGroup<RdfGraphMergerConfig_V1>(RdfGraphMergerConfig_V1.class);
        RdfGraphMergerConfig_V1 config = new RdfGraphMergerConfig_V1();
        binder.setItemDataSource(config);
        for (Field f : RdfGraphMergerConfig_V1.class.getDeclaredFields()) {
            Component component = null;
            if ("description".equals(f.getName())) {
                component = binder.buildAndBind(ctx.tr(this.getClass().getSimpleName() + "." + f.getName() + ".caption"), f.getName(), TextArea.class);
            } else {
                component = binder.buildAndBind(ctx.tr(this.getClass().getSimpleName() + "." + f.getName() + ".caption"), f.getName());
            }
            component.setSizeFull();
            if (AbstractTextField.class.isAssignableFrom(component.getClass())) {
                ((AbstractTextField) component).setInputPrompt(ctx.tr(this.getClass().getSimpleName() + "." + f.getName() + ".inputPrompt"));
                ((AbstractTextField) component).setNullRepresentation("");
                if (URI.class.isAssignableFrom(f.getType())) {
                    ((AbstractTextField) component).setConverter(new StringToUriConverter());
                    ((AbstractTextField) component).setConversionError(ctx.tr(this.getClass().getSimpleName() + ".exception.uri.conversion"));
                }
            }

            if (Validatable.class.isAssignableFrom(component.getClass())) {
                if (URI.class.isAssignableFrom(f.getType())) {
                    ((Validatable) component).addValidator(new UrlValidator(true, ctx.getDialogMasterContext().getDialogContext().getLocale()));
                }
            }
            if (AbstractComponent.class.isAssignableFrom(component.getClass())) {
                ((AbstractComponent) component).setImmediate(true);
                ((AbstractComponent) component).setLocale(ctx.getDialogMasterContext().getDialogContext().getLocale());
            }
            if (DateField.class.isAssignableFrom(component.getClass())) {
                ((DateField) component).setParseErrorMessage(ctx.tr(this.getClass().getSimpleName() + ".exception.date.conversion"));
            }
            mainLayout.addComponent(component);
        }
        setCompositionRoot(mainLayout);
    }

}
