package eu.unifiedviews.plugins.extractor.filesdownload;

import java.util.Arrays;
import java.util.List;

public class FilesDownloadConfig_V1 {

    private List<VfsFile> vfsFiles = Arrays.asList(new VfsFile());

    public List<VfsFile> getVfsFiles() {
        return vfsFiles;
    }

    public void setVfsFiles(List<VfsFile> vfsFiles) {
        this.vfsFiles = vfsFiles;
    }

}
