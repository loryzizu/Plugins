package cz.cuni.mff.xrg.uv.extractor.scp;

import org.apache.commons.vfs2.util.CryptorFactory;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.VersionedConfig;
import eu.unifiedviews.plugins.extractor.filesdownload.FilesDownloadConfig_V1;
import eu.unifiedviews.plugins.extractor.filesdownload.VfsFile;

/**
 * Configuration class from cz.cuni.mff.xrg.uv.e-filesFromScp .
 *
 * @author Škoda Petr
 */
public class FilesFromScpConfig_V1 implements VersionedConfig<FilesDownloadConfig_V1> {

    private String hostname = "";

    private Integer port = 22;

    private String username = "";

    private String password = "";

    private String source = "~/";

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

    public String getSource() {
        return source;
    }

    public void setSource(String upDestination) {
        this.source = upDestination;
    }

    public boolean isSoftFail() {
        return softFail;
    }

    public void setSoftFail(boolean softFail) {
        this.softFail = softFail;
    }

    @Override
    public FilesDownloadConfig_V1 toNextVersion() throws DPUConfigException {
        final FilesDownloadConfig_V1 config = new FilesDownloadConfig_V1();
        final VfsFile vfsFile = new VfsFile();

        config.setSoftFail(softFail);

        final StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append("sftp://");
        uriBuilder.append(hostname);
        uriBuilder.append(":");
        uriBuilder.append(port);
        uriBuilder.append(source);

        vfsFile.setUri(uriBuilder.toString());
        vfsFile.setUsername(username);
        try {
            vfsFile.setPassword(CryptorFactory.getCryptor().encrypt(password));
        } catch (Exception ex) {
            throw new DPUConfigException(ex);
        }

        config.getVfsFiles().add(vfsFile);
        return config;
    }

}
