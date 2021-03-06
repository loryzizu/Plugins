package eu.unifiedviews.plugins.extractor.filesdownload;

import eu.unifiedviews.helpers.dpu.ontology.EntityDescription;

@EntityDescription.Entity(type = FilesDownloadVocabulary.STR_FILE_CLASS)
public class VfsFile {

    @EntityDescription.Property(uri = FilesDownloadVocabulary.STR_FILE_URI)
    private String uri = "";

    @EntityDescription.Property(uri = FilesDownloadVocabulary.STR_FILE_USERNAME)
    private String username = "";

    @EntityDescription.Property(uri = FilesDownloadVocabulary.STR_FILE_PASSWORD)
    private String password = "";

    @EntityDescription.Property(uri = FilesDownloadVocabulary.STR_FILE_NAME)
    private String fileName = "";

    public VfsFile() {
    }

    public VfsFile(VfsFile vfsFile) {
        uri = vfsFile.getUri();
        username = vfsFile.getUsername();
        password = vfsFile.getPassword();
        fileName = vfsFile.getFileName();
    }

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
