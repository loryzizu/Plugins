package eu.unifiedviews.plugins.extractor.sparqlendpoint;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.parser.QueryParserUtil;

import com.vaadin.data.Validator;
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
        if (!txtQuery.isValid()) {
            throw new DPUConfigException(ctx.tr("SparqlEndpoint.query.invalid"));
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

        txtEndpoint = new TextField(ctx.tr("SparqlEndpoint.dialog.endpoint"));
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

        txtQuery.addValidator(new Validator() {

            @Override
            public void validate(Object value) throws Validator.InvalidValueException {
                final String valueStr = (String) value;
                if (value == null || valueStr.isEmpty()) {
                    throw new InvalidValueException(ctx.tr("SparqlEndpoint.query.empty"));
                }

                try {
                    QueryParserUtil.parseQuery(QueryLanguage.SPARQL, valueStr, null);
                } catch (MalformedQueryException ex) {
                    throw new InvalidValueException(ctx.tr("SparqlEndpoint.query.invalid") + " " + ex.getMessage());
                }
            }
        });

        setCompositionRoot(mainLayout);
    }
}
