package eu.unifiedviews.plugins.extractor.filesdownload;

public class VfsFile {

    private boolean displayed = true;

    private String uri = "http://www.zmluvy.gov.sk/data/att/117597_dokument.pdf";

    private String username = "";

    private String password = "";

    private String fileName = "";

    public boolean isDisplayed() {
        return displayed;
    }

    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
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
