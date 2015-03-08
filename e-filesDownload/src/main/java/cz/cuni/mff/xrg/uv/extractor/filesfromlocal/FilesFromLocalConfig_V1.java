package cz.cuni.mff.xrg.uv.extractor.filesfromlocal;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.VersionedConfig;
import eu.unifiedviews.plugins.extractor.filesdownload.FilesDownloadConfig_V1;
import eu.unifiedviews.plugins.extractor.filesdownload.VfsFile;

/**
 * Configuration class from eu.unifiedviews.plugins.uv-e-filesFromLocal .
 *
 * @author Škoda Petr
 */
public class FilesFromLocalConfig_V1 implements VersionedConfig<FilesDownloadConfig_V1> {

    private String source = "/tmp/";

    public FilesFromLocalConfig_V1() {
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public FilesDownloadConfig_V1 toNextVersion() throws DPUConfigException {
        final FilesDownloadConfig_V1 config = new FilesDownloadConfig_V1();
        final VfsFile vfsFile = new VfsFile();

        final StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append("file://");
        uriBuilder.append(source);

        vfsFile.setUri(uriBuilder.toString());

        config.getVfsFiles().add(vfsFile);
        return config;
    }

}
