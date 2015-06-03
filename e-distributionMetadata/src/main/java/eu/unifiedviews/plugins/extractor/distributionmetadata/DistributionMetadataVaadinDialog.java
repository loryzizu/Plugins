package eu.unifiedviews.plugins.extractor.distributionmetadata;

import java.lang.reflect.Field;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

public class DistributionMetadataVaadinDialog extends AbstractDialog<DistributionMetadataConfig_V1> {

    private static final long serialVersionUID = 7003725620084616056L;

    private DistributionMetadataConfig_V1 config = null; //new DistributionMetadataConfig_V1();

    BeanFieldGroup<DistributionMetadataConfig_V1> binder;

    private TextField dcatAccessURL = new TextField();

    private TextField dctermsDescription = new TextField();

    private TextField dctermsFormat = new TextField();

    private TextField dctermsLicense = new TextField();

    private TextField dcatDownloadURL = new TextField();

    private TextField dcatMediaType = new TextField();

    private DateField dctermsIssued = new DateField();

    private TextField dctermsTitle = new TextField();

    private TextField wdrsDescribedBy = new TextField();

    private TextField podDistributionDescribedByType = new TextField();

    private TextField dctermsTemporal = new TextField();

    private TextField voidExampleResource = new TextField();

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
        mainLayout.setMargin(false);
        //mainLayout.setSpacing(true);

        // top-level component properties
        setWidth("100%");
        setHeight("100%");
        for (Field f : this.getClass().getDeclaredFields()) {
            if (Component.class.isAssignableFrom(f.getType())) {
                try {
                    ((Component) f.get(this)).setCaption(ctx.tr(this.getClass().getSimpleName() + "." + f.getName() + ".caption"));
                    ((Component) f.get(this)).setSizeFull();
                    if (AbstractTextField.class.isAssignableFrom(f.getType())) {
                        ((AbstractTextField) f.get(this)).setInputPrompt(ctx.tr(this.getClass().getSimpleName() + "." + f.getName() + ".inputPrompt"));
                        ((AbstractTextField) f.get(this)).setNullRepresentation("");
                    }
                    mainLayout.addComponent((Component) f.get(this));
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }

        setCompositionRoot(mainLayout);
    }

    @Override
    public void setConfiguration(DistributionMetadataConfig_V1 conf) throws DPUConfigException {
        if (config == null) {
            config = conf;
            binder = new BeanFieldGroup<DistributionMetadataConfig_V1>(DistributionMetadataConfig_V1.class);
            binder.setItemDataSource(config);
            binder.buildAndBindMemberFields(this);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public DistributionMetadataConfig_V1 getConfiguration() throws DPUConfigException {
        try {
            binder.commit();
        } catch (CommitException ex) {
            throw new DPUConfigException(ex);
        }
        return config;
    }

}
