package eu.unifiedviews.plugins.extractor.sparqlendpoint;

import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;
import eu.unifiedviews.helpers.dpu.vaadin.validator.UrlValidator;

/**
 * Vaadin configuration dialog for SparqlEndpoint.
 *
 * @author Petr Škoda
 */
public class SparqlEndpointVaadinDialog extends AbstractDialog<SparqlEndpointConfig_V1> {

    private TextField txtEndpoint;
    
    private TextArea txtQuery;

    public SparqlEndpointVaadinDialog() {
        super(SparqlEndpoint.class);
    }

    @Override
    public void setConfiguration(SparqlEndpointConfig_V1 c) throws DPUConfigException {
        txtEndpoint.setValue(c.getEndpoint());
        txtQuery.setValue(c.getQuery());
    }

    @Override
    public SparqlEndpointConfig_V1 getConfiguration() throws DPUConfigException {
        if (!txtEndpoint.isValid()) {
            throw new DPUConfigException(ctx.tr("SparqlEndpoint.dialog.error.wrongEndpoint"));
        }
        if (!txtQuery.isValid() && !ctx.isTemplate()) {
            throw new DPUConfigException(ctx.tr("SparqlEndpoint.dialog.error.emptyQuery"));
        }
        final SparqlEndpointConfig_V1 c = new SparqlEndpointConfig_V1();

        c.setEndpoint(txtEndpoint.getValue());
        c.setQuery(txtQuery.getValue());

        return c;
    }

    @Override
    public void buildDialogLayout() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        txtEndpoint = new TextField(ctx.tr("SparqlEndpoint.dialog.endpoit"));
        txtEndpoint.setWidth("100%");
        txtEndpoint.setRequired(true);
        txtEndpoint.addValidator(new UrlValidator(false));
        mainLayout.addComponent(txtEndpoint);
        mainLayout.setExpandRatio(txtEndpoint, 0);

        txtQuery = new TextArea(ctx.tr("SparqlEndpoint.dialog.query"));
        txtQuery.setSizeFull();
        txtQuery.setRequired(true);
        mainLayout.addComponent(txtQuery);
        mainLayout.setExpandRatio(txtQuery, 1.0f);

        setCompositionRoot(mainLayout);
    }
}
