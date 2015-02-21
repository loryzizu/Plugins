package eu.unifiedviews.plugins.trandformer.sparqlselect;

import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.cuni.dpu.vaadin.AbstractDialog;

public class SparqlSelectVaadinDialog extends AbstractDialog<SparqlSelectConfig> {

    private TextField txtTarget;

    private TextArea txtQuery;

    public SparqlSelectVaadinDialog() {
        super(SparqlSelect.class);
    }

    @Override
    protected void buildDialogLayout() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setSizeFull();

        txtTarget = new TextField();
        txtTarget.setWidth("100%");
        txtTarget.setHeight("-1px");
        txtTarget.setCaption("Target path:");
        txtTarget.setRequired(true);
        mainLayout.addComponent(txtTarget);
        mainLayout.setExpandRatio(txtTarget, 0);

        txtQuery = new TextArea();
        txtQuery.setSizeFull();
        txtQuery.setCaption("SPARQL query:");
        txtTarget.setRequired(true);
        mainLayout.addComponent(txtQuery);
        mainLayout.setExpandRatio(txtQuery, 1);

        setCompositionRoot(mainLayout);
    }

    @Override
    protected void setConfiguration(SparqlSelectConfig conf) throws DPUConfigException {
        txtTarget.setValue(conf.getTargetPath());
        txtQuery.setValue(conf.getQuery());
    }

    @Override
    protected SparqlSelectConfig getConfiguration() throws DPUConfigException {
        if (!txtTarget.isValid()) {
            throw new DPUConfigException("Target path must be filled.");
        }
        if (!txtQuery.isValid()) {
            throw new DPUConfigException("SPARQL query must be filled.");
        }

        SparqlSelectConfig conf = new SparqlSelectConfig();
        conf.setTargetPath(txtTarget.getValue());
        conf.setQuery(txtQuery.getValue());
        return conf;
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder();

        desc.append("Export data as csv into ");
        desc.append(txtTarget.getValue());

        return desc.toString();
    }

}
