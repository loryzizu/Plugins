package eu.unifiedviews.plugins.transformer.xslt;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.*;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;
import eu.unifiedviews.helpers.dpu.config.InitializableConfigDialog;
import eu.unifiedviews.helpers.dpu.localization.Messages;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class XSLTVaadinDialog extends
        BaseConfigDialog<XSLTConfig_V1> implements InitializableConfigDialog {

    /**
     *
     */
    private static final long serialVersionUID = 63148374398039L;

    private static final Logger log = LoggerFactory
            .getLogger(XSLTVaadinDialog.class);

    private static final String SKIP_ON_ERROR_LABEL = "dialog.xslt.skiperror";

    private static final String OUTPUT_FILE_EXTENSTION_LABEL = "dialog.xslt.fileextension";

    private Label lFileName;

    private VerticalLayout mainLayout;

    private TextArea taXSLTemplate;

    private UploadInfoWindow uploadInfoWindow;

    private ObjectProperty<Boolean> skipOnError = new ObjectProperty<Boolean>(false);

    private ObjectProperty<String> outputFileExtension = new ObjectProperty<String>("");
    
    private Messages messages;

    // TODO refactor
    static int fl = 0;

    public XSLTVaadinDialog() {
        super(XSLTConfig_V1.class);
    }
    
    @Override
    public void initialize() {
        this.messages = new Messages(getContext().getLocale(), this.getClass().getClassLoader());
        
     // common part: create layout
        mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setMargin(false);
        mainLayout.setSpacing(true);

        mainLayout.addComponent(new CheckBox(this.messages.getString(SKIP_ON_ERROR_LABEL), skipOnError));

        mainLayout.addComponent(new TextField(this.messages.getString(OUTPUT_FILE_EXTENSTION_LABEL), outputFileExtension));

        // top-level component properties
        setWidth("100%");
        setHeight("100%");

        // upload
        final FileUploadReceiver fileUploadReceiver = new FileUploadReceiver();

        // Upload component
        Upload fileUpload = new Upload(this.messages.getString("dialog.xslt.upload"), fileUploadReceiver);
        fileUpload.setImmediate(true);
        fileUpload.setButtonCaption(this.messages.getString("dialog.xslt.uploadbutton"));
        // Upload started event listener
        fileUpload.addStartedListener(new Upload.StartedListener() {
            /**
             *
             */
            private static final long serialVersionUID = -4167203924388153623L;

            @Override
            public void uploadStarted(final Upload.StartedEvent event) {

                if (uploadInfoWindow.getParent() == null) {
                    UI.getCurrent().addWindow(uploadInfoWindow);
                }
                uploadInfoWindow.setClosable(false);

            }
        });
        // Upload received event listener.
        fileUpload.addFinishedListener(new Upload.FinishedListener() {
            /**
             *
             */
            private static final long serialVersionUID = -7276225240612908058L;

            @Override
            public void uploadFinished(final Upload.FinishedEvent event) {

                uploadInfoWindow.setClosable(true);
                uploadInfoWindow.close();
                // If upload wasn't interrupt by user
                if (fl == 0) {

                    String configText = null;
                    try {
                        configText = ((ByteArrayOutputStream) fileUploadReceiver
                                .getOutputStream()).toString("UTF-8");
                    } catch (UnsupportedEncodingException ex) {
                        log.error("Error", ex);
                    }
                    if (configText == null) {
                        log.error("Cannot save XSLT template with UTF-8 encoding");
                        return;
                    }

                    taXSLTemplate.setValue(configText);

                    // to get the current date:
                    DateFormat dateFormat = new SimpleDateFormat(
                            "yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    lFileName.setValue(messages.getString("dialog.xslt.messages.upload", fileUploadReceiver.getFileName(), dateFormat.format(date)));

                    //
                } else {
                    // textFieldPath.setReadOnly(false);
                    taXSLTemplate.setValue("");
                    // textFieldPath.setReadOnly(true);
                    fl = 0;
                }
            }

        });

        // The window with upload information
        uploadInfoWindow = new UploadInfoWindow(fileUpload, this.messages);

        mainLayout.addComponent(fileUpload);

        // label for xslt filename
        lFileName = new Label(this.messages.getString("dialog.xslt.notuploaded"));
        mainLayout.addComponent(lFileName);

        Label lInput = new Label();
        lInput.setValue(this.messages.getString("dialog.xslt.input"));
        mainLayout.addComponent(lInput);

        // ***************
        // TEXT AREA
        // ***************
        // //empty line
        // Label emptyLabel5 = new Label("");
        // emptyLabel4.setHeight("1em");
        // mainLayout.addComponent(emptyLabel5);

        taXSLTemplate = new TextArea();

        //
        taXSLTemplate.setNullRepresentation("");
        taXSLTemplate.setImmediate(false);
        taXSLTemplate.setWidth("100%");
        taXSLTemplate.setHeight("300px");
        taXSLTemplate.setVisible(true);
        // silkConfigTextArea.setInputPrompt(
        // "PREFIX br:<http://purl.org/business-register#>\nMODIFY\nDELETE { ?s pc:contact ?o}\nINSERT { ?s br:contact ?o}\nWHERE {\n\t     ?s a gr:BusinessEntity .\n\t      ?s pc:contact ?o\n}");

        mainLayout.addComponent(taXSLTemplate);
        // mainLayout.setColumnExpandRatio(0, 0.00001f);
        // mainLayout.setColumnExpandRatio(1, 0.99999f);
        
        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setContent(mainLayout);
        setCompositionRoot(panel);
        // setCompositionRoot(mainLayout);
        // setCompositionRoot(p);
    }
    
    @Override
    public void setConfiguration(XSLTConfig_V1 conf)
            throws DPUConfigException {
        // get configuration from the CONFIG object to dialog

        if (!conf.getXslTemplate().isEmpty()) {
            taXSLTemplate.setValue(conf.getXslTemplate());
            lFileName.setValue(conf.getXslTemplateFileNameShownInDialog());
        }
        skipOnError.setValue(conf.isSkipOnError());
        outputFileExtension.setValue(conf.getOutputFileExtension());
    }

    @Override
    public XSLTConfig_V1 getConfiguration()
            throws DPUConfigException {
        // get the conf from the dialog

        // check that certain xslt was uploaded
        if (taXSLTemplate.getValue().trim().isEmpty()) {
            // no config!
            throw new DPUConfigException(this.messages.getString("dialog.xslt.errors.configupload"));

        }

        // prepare output type:
        // TODO storing the textarea content not needed - not readed when the
        // configuration is shown
        XSLTConfig_V1 conf = new XSLTConfig_V1();
        conf.setXslTemplate(taXSLTemplate.getValue());
        conf.setXslTemplateFileNameShownInDialog(lFileName.getValue().trim());
        conf.setSkipOnError(skipOnError.getValue());
        if (!outputFileExtension.getValue().startsWith(".")) {
            throw new DPUConfigException(this.messages.getString("dialog.xslt.errors.extension"));
        }
        conf.setOutputFileExtension(outputFileExtension.getValue());
        return conf;

    }
}

/**
 * Upload selected file to template directory
 */
class FileUploadReceiver implements Upload.Receiver {

    private static final long serialVersionUID = 5099459605355200117L;

    // private static final int searchedByte = '\n';
    // private static int total = 0;
    // private boolean sleep = false;
    // public static String fileName;
    // public static File file;
    // public static Path path;
    // private DPUContext context;

    private String fileName;

    private OutputStream fos;

    public String getFileName() {
        return fileName;
    }

    public OutputStream getOutputStream() {
        return fos;
    }

    /**
     * return an OutputStream
     */
    @Override
    public OutputStream receiveUpload(final String filename,
            final String MIMEType) {

        this.fileName = filename;
        fos = new ByteArrayOutputStream();
        return fos;

    }

}

/**
 * Dialog for uploading status. Appear automatically after file upload start.
 *
 * @author tknap
 */
class UploadInfoWindow extends Window implements Upload.StartedListener,
        Upload.ProgressListener, Upload.FinishedListener {

    private static final long serialVersionUID = 1L;

    private final Label state = new Label();

    private final Label fileName = new Label();

    private final Label textualProgress = new Label();

    private final ProgressIndicator pi = new ProgressIndicator();

    private final Button cancelButton;

    private final Upload upload;
    
    private final Messages messages;

    /**
     * Basic constructor
     *
     * @param upload
     *            . Upload component
     */
    public UploadInfoWindow(Upload nextUpload, Messages messages) {
        super("Status");
        this.messages = messages;
        this.upload = nextUpload;
        this.cancelButton = new Button(this.messages.getString("dialog.xslt.uploadinfo.cancelbtn"));

        setComponent();

    }

    private void setComponent() {
        addStyleName("upload-info");

        setResizable(false);
        setDraggable(false);

        final FormLayout formLayout = new FormLayout();
        setContent(formLayout);
        formLayout.setMargin(true);

        final HorizontalLayout stateLayout = new HorizontalLayout();
        stateLayout.setSpacing(true);
        stateLayout.addComponent(state);

        cancelButton.addClickListener(new Button.ClickListener() {
            /**
             * Upload interruption
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final Button.ClickEvent event) {
                upload.interruptUpload();
                XSLTVaadinDialog.fl = 1;
            }
        });
        cancelButton.setVisible(false);
        cancelButton.setStyleName("small");
        stateLayout.addComponent(cancelButton);

        stateLayout.setCaption(this.messages.getString("dialog.xslt.uploadinfo.state"));
        state.setValue(this.messages.getString("dialog.xslt.uploadinfo.idle"));
        formLayout.addComponent(stateLayout);

        fileName.setCaption(this.messages.getString("dialog.xslt.uploadinfo.filename"));
        formLayout.addComponent(fileName);

        // progress indicator
        pi.setCaption(this.messages.getString("dialog.xslt.uploadinfo.progress"));
        pi.setVisible(false);
        formLayout.addComponent(pi);

        textualProgress.setVisible(false);
        formLayout.addComponent(textualProgress);

        upload.addStartedListener(this);
        upload.addProgressListener(this);
        upload.addFinishedListener(this);
    }

    /**
     * this method gets called immediately after upload is finished
     */
    @Override
    public void uploadFinished(final Upload.FinishedEvent event) {
        state.setValue(this.messages.getString("dialog.xslt.uploadinfo.idle"));
        pi.setVisible(false);
        textualProgress.setVisible(false);
        cancelButton.setVisible(false);

    }

    /**
     * this method gets called immediately after upload is started
     */
    @Override
    public void uploadStarted(final Upload.StartedEvent event) {

        pi.setValue(0f);
        pi.setVisible(true);
        pi.setPollingInterval(500); // hit server frequantly to get
        textualProgress.setVisible(true);
        // updates to client
        state.setValue(this.messages.getString("dialog.xslt.uploadinfo.uploading"));
        fileName.setValue(event.getFilename());

        cancelButton.setVisible(true);
    }

    /**
     * this method shows update progress
     */
    @Override
    public void updateProgress(final long readBytes, final long contentLength) {
        // this method gets called several times during the update
        pi.setValue(new Float(readBytes / (float) contentLength));
        textualProgress.setValue(this.messages.getString("dialog.xslt.uploadinfo.textprogress", 
                readBytes / 1024, contentLength / 1024));
    }
}
