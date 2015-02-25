package eu.unifiedviews.plugins.extractor.filesdownload;

import java.util.Arrays;
import java.util.List;

import eu.unifiedviews.helpers.dpu.config.VersionedConfig;
import eu.unifiedviews.helpers.dpu.ontology.EntityDescription;

@EntityDescription.Entity(type = FilesDownloadVocabulary.STR_CONFIG_CLASS)
public class FilesDownloadConfig_V1 implements VersionedConfig<FilesDownloadConfig_V1>{

    @EntityDescription.Property(uri = FilesDownloadVocabulary.STR_CONFIG_HAS_FILE)
    private List<VfsFile> vfsFiles = Arrays.asList(new VfsFile());

    private boolean softFail = false;

    public List<VfsFile> getVfsFiles() {
        return vfsFiles;
    }

    public void setVfsFiles(List<VfsFile> vfsFiles) {
        this.vfsFiles = vfsFiles;
    }

    public boolean isSoftFail() {
        return softFail;
    }

    public void setSoftFail(boolean softFail) {
        this.softFail = softFail;
    }

    @Override
    public FilesDownloadConfig_V1 toNextVersion() {
        // This is just a trick to enable use history with non-linear history, ie. many to one history.
        return this;
    }

}
