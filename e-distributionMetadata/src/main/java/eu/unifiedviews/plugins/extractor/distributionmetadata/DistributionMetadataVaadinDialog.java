package eu.unifiedviews.plugins.extractor.distributionmetadata;

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

public class DistributionMetadataVaadinDialog extends AbstractDialog<DistributionMetadataConfig_V1> {

    private static final long serialVersionUID = 7003725620084616056L;

    BeanFieldGroup<DistributionMetadataConfig_V1> binder;

    public DistributionMetadataVaadinDialog() {
        super(DistributionMetadata.class);
    }

    @Override
    protected void buildDialogLayout() {
        // common part: create layout
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setImmediate(true);
        mainLayout.setWidth("100%");
        mainLayout.setHeight(null);
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);

        // top-level component properties
        setWidth("100%");
        setHeight("100%");
        binder = new BeanFieldGroup<DistributionMetadataConfig_V1>(DistributionMetadataConfig_V1.class);
        DistributionMetadataConfig_V1 config = new DistributionMetadataConfig_V1();
        binder.setItemDataSource(config);
        for (Field f : DistributionMetadataConfig_V1.class.getDeclaredFields()) {
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
                    ((AbstractTextField) component).setConversionError(ctx.tr("DistributionMetadataVaadinDialog.exception.uri.conversion"));
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
                ((DateField) component).setParseErrorMessage(ctx.tr("DistributionMetadataVaadinDialog.exception.date.conversion"));
            }
            mainLayout.addComponent(component);
        }
        setCompositionRoot(mainLayout);
    }

    @Override
    public void setConfiguration(DistributionMetadataConfig_V1 conf) throws DPUConfigException {
        binder.setItemDataSource(conf);
    }

    @Override
    public DistributionMetadataConfig_V1 getConfiguration() throws DPUConfigException {
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

}
