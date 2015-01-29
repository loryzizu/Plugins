package eu.unifiedviews.plugins.extractor.filesdownload.test;

import java.io.File;
import java.net.URI;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.files.FilesDataUnit.Entry;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.helpers.dataunit.fileshelper.FilesHelper;
import eu.unifiedviews.plugins.extractor.filesdownload.FilesDownload;
import eu.unifiedviews.plugins.extractor.filesdownload.FilesDownloadConfig_V1;

public class FilesDownloadTest {

    @Test
    public void execute() throws Exception {
        // Prepare config.
        FilesDownloadConfig_V1 config = new FilesDownloadConfig_V1();
        config.getVfsFiles().get(0).setUri(getClass().getClassLoader().getResource("test").toURI().toString());

        // Prepare DPU.
        FilesDownload download = new FilesDownload();
        download.configureDirectly(config);

        // Prepare test environment.
        TestEnvironment environment = new TestEnvironment();

        // Prepare data unit.
        WritableFilesDataUnit filesOutput = environment.createFilesOutput("filesOutput");

        try {
            // Run.
            environment.run(download);

            // Get file iterator.
            Iterator<Entry> outputEntries = FilesHelper.getFiles(filesOutput).iterator();

            // Iterate over files.
            while (outputEntries.hasNext()) {
                byte[] outputContent = FileUtils.readFileToByteArray(new File(new URI(outputEntries.next().getFileURIString())));

                // Verify result.
                Assert.assertEquals(5, outputContent.length);
            }
        } finally {
            // Release resources.
            environment.release();
        }
    }

}
