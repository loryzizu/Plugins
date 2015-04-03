package eu.unifiedviews.plugins.extractor.httpdownload;

import java.net.URL;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.VersionedConfig;
import eu.unifiedviews.plugins.extractor.filesdownload.FilesDownloadConfig_V1;
import eu.unifiedviews.plugins.extractor.filesdownload.VfsFile;

/**
 * Configuration class from eu.unifiedviews.plugins.uv-e-httpDownload .
 *
 */
public class HttpDownloadConfig_V1 implements VersionedConfig<FilesDownloadConfig_V1> {

    private URL URL = null;

    private String target = "/file";

    /**
     * Number of attempts to try before failure, -1 for infinite.
     */
    private int retryCount = -1;

    private int retryDelay = 1000;

    public URL getURL() {
        return URL;
    }

    public void setURL(URL URL) {
        this.URL = URL;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(int retryDelay) {
        this.retryDelay = retryDelay;
    }

    @Override
    public FilesDownloadConfig_V1 toNextVersion() throws DPUConfigException {
        final FilesDownloadConfig_V1 config = new FilesDownloadConfig_V1();
        final VfsFile vfsFile = new VfsFile();

        // TODO Add support for fault tolerance.
        if (URL != null) {
            vfsFile.setUri(URL.toString());
        } else {
            vfsFile.setUri("");
        }
        vfsFile.setFileName(target);

        config.getVfsFiles().add(vfsFile);
        return config;
    }

}
