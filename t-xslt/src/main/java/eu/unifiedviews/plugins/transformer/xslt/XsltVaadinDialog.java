package eu.unifiedviews.plugins.transformer.xslt;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cz.cuni.mff.xrg.uv.transformer.xslt.XsltConfig_V2;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

/**
 * @author Škoda Petr
 */
public class XsltVaadinDialog extends AbstractDialog<XsltConfig_V2> {

    private CheckBox checkSkipFileOnError;

    private TextField txtOutputExtension;

    private TextField txtNumberOfExtraThreads;

    private TextArea txtTemplate;

    public XsltVaadinDialog() {
        super(Xslt.class);
    }

    @Override
    public void setConfiguration(XsltConfig_V2 c) throws DPUConfigException {
        this.checkSkipFileOnError.setValue(!c.isFailOnError());
        this.txtOutputExtension.setValue(c.getOutputFileExtension());
        this.txtNumberOfExtraThreads.setValue(Integer.toString(c.getNumberOfExtraThreads()));
        this.txtTemplate.setValue(c.getXsltTemplate());
    }

    @Override
    public XsltConfig_V2 getConfiguration() throws DPUConfigException {
        final XsltConfig_V2 c = new XsltConfig_V2();
        c.setFailOnError(!this.checkSkipFileOnError.getValue());
        c.setOutputFileExtension(this.txtOutputExtension.getValue());
        c.setXsltTemplate(this.txtTemplate.getValue());
        // Parse
        try {
            int value = Integer.parseInt(this.txtNumberOfExtraThreads.getValue());
            if (value < 0) {
                throw new DPUConfigException(ctx.tr("xslt.dialog.extraThreads.negative"));
            }
            c.setNumberOfExtraThreads(value);
        } catch (NumberFormatException ex) {
            throw new DPUConfigException(ctx.tr("xslt.dialog.extraThreads.formatException"));
        }
        // Check file extension.
        if (!c.getOutputFileExtension().isEmpty() && !c.getOutputFileExtension().startsWith(".")) {
            throw new DPUConfigException(ctx.tr("xslt.dialog.template.output.extension.formatException"));
        }
        return c;
    }

    @Override
    public void buildDialogLayout() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        this.checkSkipFileOnError = new CheckBox(ctx.tr("xslt.dialog.skip.on.error"));
        this.checkSkipFileOnError.setWidth("100%");
        mainLayout.addComponent(this.checkSkipFileOnError);
        mainLayout.setExpandRatio(this.checkSkipFileOnError, 0.0f);

        HorizontalLayout textLine = new HorizontalLayout();
        textLine.setWidth("100%");

        this.txtOutputExtension = new TextField(ctx.tr("xslt.dialog.template.output.extension"));
        this.txtOutputExtension.setWidth("50%");
        this.txtOutputExtension.setDescription(ctx.tr("xslt.dialog.template.output.extension.desc"));
        textLine.addComponent(this.txtOutputExtension);

        this.txtNumberOfExtraThreads = new TextField(ctx.tr("xslt.dialog.extraThreads"));
        this.txtNumberOfExtraThreads.setWidth("50%");
        textLine.addComponent(this.txtNumberOfExtraThreads);

        mainLayout.addComponent(textLine);

        this.txtTemplate = new TextArea(ctx.tr("xslt.dialog.template"));
        this.txtTemplate.setSizeFull();
        mainLayout.addComponent(this.txtTemplate);
        mainLayout.setExpandRatio(this.txtTemplate, 1.0f);

        setCompositionRoot(mainLayout);
    }

}
