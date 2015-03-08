package eu.unifiedviews.plugins.loader.filestolocalfs;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import eu.unifiedviews.helpers.dpu.config.VersionedConfig;
import eu.unifiedviews.plugins.loader.filesupload.FilesUploadConfig_V1;

public class FilesToLocalFSConfig_V1 implements VersionedConfig<FilesUploadConfig_V1> {

    private static final long serialVersionUID = -3161162556703740405L;

    private String destination = "/tmp";

    private boolean moveFiles = false;

    private boolean replaceExisting = false;

    private boolean skipOnError = false;

    // DPUTemplateConfig must provide public non-parametric constructor
    public FilesToLocalFSConfig_V1() {
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isMoveFiles() {
        return moveFiles;
    }

    public void setMoveFiles(boolean moveFiles) {
        this.moveFiles = moveFiles;
    }

    public boolean isReplaceExisting() {
        return replaceExisting;
    }

    public void setReplaceExisting(boolean replaceExisting) {
        this.replaceExisting = replaceExisting;
    }

    public boolean isSkipOnError() {
        return skipOnError;
    }

    public void setSkipOnError(boolean skipOnError) {
        this.skipOnError = skipOnError;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }

    @Override
    public FilesUploadConfig_V1 toNextVersion() {
        final FilesUploadConfig_V1 config = new FilesUploadConfig_V1();
        config.setPassword("");
        config.setSoftFail(skipOnError);
        config.setMoveFiles(moveFiles);
        config.setUsername("");

        final StringBuilder uriStr = new StringBuilder();
        uriStr.append("file:/");
        if (!destination.startsWith("/")) {
            uriStr.append("/");
        }
        uriStr.append(destination);

        config.setUri(uriStr.toString());
        return config;
    }

}
