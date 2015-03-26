package eu.unifiedviews.plugins.transformer.filtervalidxml;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.*;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;

import eu.unifiedviews.dpu.config.DPUConfigException;

public class FilterValidXmlVaadinDialog extends AbstractDialog<FilterValidXmlConfig_V1> {

    private static final long serialVersionUID = 8747029940403211381L;

    private final Logger log = LoggerFactory.getLogger(FilterValidXmlVaadinDialog.class);

    private TextArea xsdTextArea;

    private Label xsdUploadLabel;

    private TextArea xsltTextArea;

    private Label xsltUploadLabel;

    private CheckBox failOnValidationCheckbox;

    public FilterValidXmlVaadinDialog() {
        super(FilterValidXml.class);
    }

    @Override protected void buildDialogLayout() {
        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setContent(buildMainLayout());

        setWidth("100%");
        setHeight("100%");
        setCompositionRoot(panel);
    }

    private VerticalLayout buildMainLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);

        buildXsdUpload(mainLayout);
        buildXsltUpload(mainLayout);
        buildFailOnValidation(mainLayout);

        return mainLayout;
    }

    private void buildFailOnValidation(VerticalLayout mainLayout) {
       failOnValidationCheckbox = new CheckBox(ctx.tr("dialog.failOnValidationError"));
        mainLayout.addComponent(failOnValidationCheckbox);
    }

    private void buildXsdUpload(final Layout layout) {
        final FileUploadReceiver uploadReceiver = new FileUploadReceiver();
        Upload upload = new Upload(ctx.tr("dialog.xsd.upload"), uploadReceiver);
        upload.setImmediate(true);
        upload.setButtonCaption(ctx.tr("dialog.xsd.upload.button"));
        layout.addComponent(upload);

        final UploadInfoWindow uploadInfoWindow = new UploadInfoWindow(upload);

        xsdUploadLabel = new Label(ctx.tr("dialog.xsd.upload.label.nofile"));
        layout.addComponent(xsdUploadLabel);

        Label textAreaLabel = new Label();
        textAreaLabel.setValue(ctx.tr("dialog.xsd.upload.textarea"));
        layout.addComponent(textAreaLabel);

        xsdTextArea = new TextArea();
        xsdTextArea.setNullRepresentation("");
        xsdTextArea.setImmediate(true);
        xsdTextArea.setWidth("100%");
        xsdTextArea.setHeight("300px");
        xsdTextArea.setVisible(true);
        layout.addComponent(xsdTextArea);

        // add upload listeners
        upload.addStartedListener(new Upload.StartedListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void uploadStarted(final StartedEvent event) {
                if (uploadInfoWindow.getParent() == null) {
                    UI.getCurrent().addWindow(uploadInfoWindow);
                }
                uploadInfoWindow.setClosable(false);
            }
        });

        upload.addSucceededListener(new Upload.SucceededListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void uploadSucceeded(final SucceededEvent event) {
                uploadInfoWindow.setClosable(true);
                uploadInfoWindow.close();

                String xsdFileContents = null;

                try {
                    xsdFileContents = ((ByteArrayOutputStream) uploadReceiver
                            .getOutputStream()).toString("UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    log.error("Problem with XSD file encoding", ex);
                }

                if (xsdFileContents == null) {
                    log.error("Cannot save XSD template with UTF-8 encoding");
                    return;
                }

                xsdTextArea.setValue(xsdFileContents);

                DateFormat dateFormat = new SimpleDateFormat(ctx.tr("general.dateformat"));
                xsdUploadLabel.setValue(ctx.tr("dialog.xsd.upload.label",
                        uploadReceiver.getFileName(),
                        dateFormat.format(new Date())));
            }
        });

        upload.addFailedListener(new Upload.FailedListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void uploadFailed(final FailedEvent event) {
                uploadInfoWindow.setClosable(true);
                uploadInfoWindow.close();

                log.error("XSD file upload failed: ", event.getReason());
            }
        });

    }

    private void buildXsltUpload(final Layout layout) {
        final FileUploadReceiver uploadReceiver = new FileUploadReceiver();
        Upload upload = new Upload(ctx.tr("dialog.xslt.upload"), uploadReceiver);
        upload.setImmediate(true);
        upload.setButtonCaption(ctx.tr("dialog.xslt.upload.button"));
        layout.addComponent(upload);

        final UploadInfoWindow uploadInfoWindow = new UploadInfoWindow(upload);

        xsltUploadLabel = new Label(ctx.tr("dialog.xslt.upload.label.nofile"));
        layout.addComponent(xsltUploadLabel);

        Label textAreaLabel = new Label();
        textAreaLabel.setValue(ctx.tr("dialog.xslt.upload.textarea"));
        layout.addComponent(textAreaLabel);

        xsltTextArea = new TextArea();
        xsltTextArea.setNullRepresentation("");
        xsltTextArea.setImmediate(true);
        xsltTextArea.setWidth("100%");
        xsltTextArea.setHeight("300px");
        xsltTextArea.setVisible(true);
        layout.addComponent(xsltTextArea);

        // add upload listeners
        upload.addStartedListener(new Upload.StartedListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void uploadStarted(final StartedEvent event) {
                if (uploadInfoWindow.getParent() == null) {
                    UI.getCurrent().addWindow(uploadInfoWindow);
                }
                uploadInfoWindow.setClosable(false);
            }
        });

        upload.addSucceededListener(new Upload.SucceededListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void uploadSucceeded(final SucceededEvent event) {
                uploadInfoWindow.setClosable(true);
                uploadInfoWindow.close();

                String xsltFileContents = null;

                try {
                    xsltFileContents = ((ByteArrayOutputStream) uploadReceiver
                            .getOutputStream()).toString("UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    log.error("Problem with XSD file encoding", ex);
                }

                if (xsltFileContents == null) {
                    log.error("Cannot save XSD template with UTF-8 encoding");
                    return;
                }

                xsltTextArea.setValue(xsltFileContents);

                DateFormat dateFormat = new SimpleDateFormat(ctx.tr("general.dateformat"));
                xsltUploadLabel.setValue(ctx.tr("dialog.xslt.upload.label",
                        uploadReceiver.getFileName(),
                        dateFormat.format(new Date())));
            }
        });

        upload.addFailedListener(new Upload.FailedListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void uploadFailed(final FailedEvent event) {
                uploadInfoWindow.setClosable(true);
                uploadInfoWindow.close();

                log.error("XSLT file upload failed: ", event.getReason());
            }
        });

    }

    @Override
    protected void setConfiguration(final FilterValidXmlConfig_V1 c) throws DPUConfigException {
        xsdTextArea.setValue(c.getXsdContents());
        xsdUploadLabel.setValue(c.getXsdFileUploadLabel());

        xsltTextArea.setValue(c.getXsltContents());
        xsltUploadLabel.setValue(c.getXsltFileUploadLabel());

        failOnValidationCheckbox.setValue(c.isFailPipelineOnValidationError());
    }

    @Override
    protected FilterValidXmlConfig_V1 getConfiguration() throws DPUConfigException {
        final FilterValidXmlConfig_V1 config = new FilterValidXmlConfig_V1();

        config.setXsdContents(xsdTextArea.getValue());
        config.setXsdFileUploadLabel(xsdUploadLabel.getValue());

        config.setXsltContents(xsltTextArea.getValue());
        config.setXsltFileUploadLabel(xsltUploadLabel.getValue());

        config.setFailPipelineOnValidationError(failOnValidationCheckbox.getValue());

        return config;
    }

    @Override
    public String getDescription() {
        StringBuilder desc = new StringBuilder();

        desc.append(ctx.tr("dialog.description.1"));

        if (!xsdTextArea.getValue().isEmpty()) {
            desc.append(ctx.tr("dialog.description.2"));
        }

        if (!xsltTextArea.getValue().isEmpty()) {
            desc.append(ctx.tr("dialog.description.3"));
        }

        if(failOnValidationCheckbox.getValue()) {
            desc.append(ctx.tr("dialog.description.4"));
        }

        return desc.toString();
    }
}
