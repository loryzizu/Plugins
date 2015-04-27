package eu.unifiedviews.plugins.loader.filestoparliament;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.openrdf.rio.RDFFormat;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

@SuppressWarnings("serial")
public class FilesToParliamentVaadinDialog extends AbstractDialog<FilesToParliamentConfig_V1> {
    private ObjectProperty<String> bulkUploadEndpointURL = new ObjectProperty<String>("");

    private NativeSelect selectRdfFormat;

    private static Set<RDFFormat> supportedRdfFormats;

    private static RDFFormat auto;
    static {
        auto = new RDFFormat("Auto", "text/plain",
                Charset.forName("US-ASCII"), "auto", false, false);
        supportedRdfFormats = new LinkedHashSet<>();
        supportedRdfFormats.add(auto);
        supportedRdfFormats.add(RDFFormat.N3);
        supportedRdfFormats.add(RDFFormat.TURTLE);
        supportedRdfFormats.add(RDFFormat.NTRIPLES);
        supportedRdfFormats.add(RDFFormat.RDFXML);
    }

    public FilesToParliamentVaadinDialog() {
        super(FilesToParliament.class);
    }

    @Override
    protected void buildDialogLayout() {
        setSizeFull();

        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setHeight("-1px");
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        mainLayout.setWidth("100%");

        mainLayout.addComponent(createTextField(ctx.tr("FilesToParliamentVaadinDialog.bulkUploadEndpointURL"), bulkUploadEndpointURL));
        selectRdfFormat = new NativeSelect(ctx.tr("FilesToParliamentVaadinDialog.dialog.format"));
        for (RDFFormat item : supportedRdfFormats) {
            selectRdfFormat.addItem(item);
            selectRdfFormat.setItemCaption(item, item.getName());
        }
        selectRdfFormat.setNullSelectionAllowed(false);
        selectRdfFormat.setImmediate(true);
        mainLayout.addComponent(selectRdfFormat);
        setCompositionRoot(mainLayout);
    }

    @Override
    protected FilesToParliamentConfig_V1 getConfiguration() throws DPUConfigException {
        FilesToParliamentConfig_V1 result = new FilesToParliamentConfig_V1();
        result.setBulkUploadEndpointURL(bulkUploadEndpointURL.getValue());
        final RDFFormat format = (RDFFormat) selectRdfFormat.getValue();
        result.setRdfFileFormat(format.getName());
        return result;
    }

    @Override
    protected void setConfiguration(FilesToParliamentConfig_V1 config) throws DPUConfigException {
        bulkUploadEndpointURL.setValue(config.getBulkUploadEndpointURL());
        String format = config.getRdfFileFormat();
        if (auto.getName().equals(format)) {
            selectRdfFormat.setValue(auto);    
        } else {
            selectRdfFormat.setValue(RDFFormat.valueOf(format));    
        }        
    }

    private <T> TextField createTextField(String caption, ObjectProperty<T> property) {
        final TextField result = new TextField(caption, property);
        result.setWidth("100%");
        return result;
    }
}
