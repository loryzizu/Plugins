package cz.cuni.mff.xrg.uv.extractor.httpdownload;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.VersionedConfig;
import eu.unifiedviews.plugins.extractor.filesdownload.FilesDownloadConfig_V1;
import eu.unifiedviews.plugins.extractor.filesdownload.VfsFile;

/**
 * Configuration class from cz.cuni.mff.xrg.uv.e-HttpDownload .
 *
 * @author Škoda Petr
 */
public class HttpDownloadConfig_V2 implements VersionedConfig<FilesDownloadConfig_V1> {

    private List<DownloadInfo_V1> toDownload = new LinkedList<>();

    public HttpDownloadConfig_V2() {

    }

    public List<DownloadInfo_V1> getToDownload() {
        return toDownload;
    }

    public void setToDownload(List<DownloadInfo_V1> toDownload) {
        this.toDownload = toDownload;
    }

    @Override
    public FilesDownloadConfig_V1 toNextVersion() throws DPUConfigException {
        final FilesDownloadConfig_V1 config = new FilesDownloadConfig_V1();

        for (DownloadInfo_V1 item : toDownload) {
            final VfsFile vfsFile = new VfsFile();
            // Get VirtualPath or prepare it in same way as e-HttpDownload does.
            String virtualPath = item.getVirtualPath();
            if (virtualPath == null || virtualPath.isEmpty()) {
                // Just use something as virtual path.
                final String uriTail = item.getUri().substring(item.getUri().lastIndexOf("/") + 1);
                try {
                    virtualPath = URLEncoder.encode(uriTail, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    throw new DPUConfigException("UTF-8 is not supported!", ex);
                }
            }
            vfsFile.setUri(item.getUri());
            vfsFile.setFileName(virtualPath);
            config.getVfsFiles().add(vfsFile);
        }
        return config;
    }

}
