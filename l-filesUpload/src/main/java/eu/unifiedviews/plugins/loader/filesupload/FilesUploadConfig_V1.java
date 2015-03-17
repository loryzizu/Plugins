package eu.unifiedviews.plugins.loader.filesupload;

import eu.unifiedviews.helpers.dpu.config.VersionedConfig;
import eu.unifiedviews.helpers.dpu.ontology.EntityDescription;

@EntityDescription.Entity(type = FilesUploadVocabulary.STR_CONFIG_CLASS)
public class FilesUploadConfig_V1 implements VersionedConfig<FilesUploadConfig_V1> {

    @EntityDescription.Property(uri = FilesUploadVocabulary.STR_CONFIG_URI)
    private String uri = "ftps://server:/path/";

    @EntityDescription.Property(uri = FilesUploadVocabulary.STR_CONFIG_USERNAME)
    private String username = "";

    @EntityDescription.Property(uri = FilesUploadVocabulary.STR_CONFIG_PASSWORD)
    private String password = "";

    private boolean softFail = false;

    private boolean moveFiles = false;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSoftFail() {
        return softFail;
    }

    public void setSoftFail(boolean softFail) {
        this.softFail = softFail;
    }

    public boolean isMoveFiles() {
        return moveFiles;
    }

    public void setMoveFiles(boolean moveFiles) {
        this.moveFiles = moveFiles;
    }

    @Override
    public FilesUploadConfig_V1 toNextVersion() {
        // This is just a small trick to force history of configuration.
        return this;
    }

}
