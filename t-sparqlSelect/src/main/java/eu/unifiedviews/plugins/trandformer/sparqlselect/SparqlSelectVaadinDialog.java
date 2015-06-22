package eu.unifiedviews.plugins.trandformer.sparqlselect;

import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

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
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);

        txtTarget = new TextField();
        txtTarget.setWidth("100%");
        txtTarget.setHeight("-1px");
        txtTarget.setCaption(ctx.tr("SparqlSelectVaadinDialog.txtTarget"));
        txtTarget.setRequired(true);
        mainLayout.addComponent(txtTarget);
        mainLayout.setExpandRatio(txtTarget, 0);

        txtQuery = new TextArea();
        txtQuery.setSizeFull();
        txtQuery.setCaption(ctx.tr("SparqlSelectVaadinDialog.txtQuery"));
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
            throw new DPUConfigException(ctx.tr("SparqlSelectVaadinDialog.exception.pathFilled"));
        }
        if (!txtQuery.isValid()) {
            throw new DPUConfigException(ctx.tr("SparqlSelectVaadinDialog.exception.queryFilled"));
        }

        SparqlSelectConfig conf = new SparqlSelectConfig();
        conf.setTargetPath(txtTarget.getValue());
        conf.setQuery(txtQuery.getValue());
        return conf;
    }

    @Override
    public String getDescription() {
        return ctx.tr("SparqlSelectVaadinDialog.description", txtTarget.getValue());
    }

}
