package eu.unifiedviews.plugins.extractor.filestoscp;

import org.apache.commons.vfs2.util.CryptorFactory;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.cuni.dpu.config.VersionedConfig;
import eu.unifiedviews.plugins.loader.filesupload.FilesUploadConfig_V1;

public class FilesToScpConfig_V1 implements VersionedConfig<FilesUploadConfig_V1> {

    private String hostname = "";

    private Integer port = 22;

    private String username = "";

    private String password = "";

    private String destination = "/";

    /**
     * If true and upload failed, then only warning is published.
     */
    private boolean softFail = true;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
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

    public String getDestination() {
        return destination;
    }

    public void setDestination(String upDestination) {
        this.destination = upDestination;
    }

    public boolean isSoftFail() {
        return softFail;
    }

    public void setSoftFail(boolean softFail) {
        this.softFail = softFail;
    }

    @Override
    public FilesUploadConfig_V1 toNextVersion() throws DPUConfigException {
        final FilesUploadConfig_V1 config = new FilesUploadConfig_V1();
        try {
            config.setPassword(CryptorFactory.getCryptor().encrypt(password));
        } catch (Exception ex) {
            throw new DPUConfigException(ex);
        }
        config.setSoftFail(softFail);
        config.setUsername(username);

        final StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append("sftp://");
        uriBuilder.append(hostname);
        uriBuilder.append(":");
        uriBuilder.append(port);
        uriBuilder.append(destination);
        config.setUri(uriBuilder.toString());
        return config;
    }   

}
