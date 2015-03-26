package eu.unifiedviews.plugins.transformer.filtervalidxml;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.vaadin.ui.Upload;

/**
 * Upload selected file to template directory
 */
class FileUploadReceiver implements Upload.Receiver {

    private static final long serialVersionUID = 6156476495613361078L;

    private String fileName;

    private OutputStream fos;

    public String getFileName() {
        return fileName;
    }

    public OutputStream getOutputStream() {
        return fos;
    }

    @Override
    public OutputStream receiveUpload(final String filename,
            final String MIMEType) {

        this.fileName = filename;
        fos = new ByteArrayOutputStream();
        return fos;

    }

}
