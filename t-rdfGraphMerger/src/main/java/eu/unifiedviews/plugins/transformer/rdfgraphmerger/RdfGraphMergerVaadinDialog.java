package eu.unifiedviews.plugins.transformer.rdfgraphmerger;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;
import eu.unifiedviews.helpers.dpu.vaadin.validator.UrlValidator;

/**
 * DPU's configuration dialog.
 */
public class RdfGraphMergerVaadinDialog extends AbstractDialog<RdfGraphMergerConfig_V1> {
    public static final String VIRTUAL_GRAPH_FIELD_NAME = "virtualGraph";

    /**
     * 
     */
    private static final long serialVersionUID = 2412930986305283577L;

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

        mainLayout.setMargin(true);
        mainLayout.setHeight(null);

        // top-level component properties
        setWidth("100%");
        setHeight("100%");
        binder = new BeanFieldGroup<RdfGraphMergerConfig_V1>(RdfGraphMergerConfig_V1.class);
        RdfGraphMergerConfig_V1 config = new RdfGraphMergerConfig_V1();
        binder.setItemDataSource(config);
        TextField component = new TextField(ctx.tr(this.getClass().getSimpleName() + "." + VIRTUAL_GRAPH_FIELD_NAME + ".caption"));
        binder.bind(component, VIRTUAL_GRAPH_FIELD_NAME);
        component.setSizeFull();
        component.setInputPrompt(ctx.tr(this.getClass().getSimpleName() + "." + VIRTUAL_GRAPH_FIELD_NAME + ".inputPrompt"));
        component.setNullRepresentation("");
        component.setConverter(new StringToUriConverter());
        component.setConversionError(ctx.tr(this.getClass().getSimpleName() + ".exception.uri.conversion"));
        component.addValidator(new UrlValidator(true, ctx.getDialogMasterContext().getDialogContext().getLocale()));
        component.setImmediate(true);
        component.setLocale(ctx.getDialogMasterContext().getDialogContext().getLocale());

        mainLayout.addComponent(component);
        setCompositionRoot(mainLayout);
    }

}
