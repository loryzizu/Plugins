package eu.unifiedviews.plugins.loader.filesupload.test;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.files.FilesDataUnit.Entry;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.helpers.dataunit.fileshelper.FilesHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.plugins.loader.filesupload.FilesUpload;
import eu.unifiedviews.plugins.loader.filesupload.FilesUploadConfig_V1;

public class FilesUploadTest {

    @Test
    public void execute() throws Exception {
        // Prepare config.
        File directory = Files.createTempDirectory(null).toFile();
        directory.mkdir();

        FilesUploadConfig_V1 config = new FilesUploadConfig_V1();
        config.setUri(directory.toURI().toString());

        // Prepare DPU.
        FilesUpload upload = new FilesUpload();
        upload.configureDirectly(config);

        // Prepare test environment.
        TestEnvironment environment = new TestEnvironment();

        // Prepare data unit.
        WritableFilesDataUnit filesInput = environment.createFilesInput("filesInput");
        VirtualPathHelper virtualPathHelper = VirtualPathHelpers.create(filesInput);

        try {
            // Test data.
            filesInput.addExistingFile("test.txt", getClass().getClassLoader().getResource("test.txt").toURI().toString());
            virtualPathHelper.setVirtualPath("test.txt", "/test.txt");

            // Run.
            environment.run(upload);

            // Get file iterator.
            Iterator<Entry> inputEntries = FilesHelper.getFiles(filesInput).iterator();

            // Iterate over files.
            while (inputEntries.hasNext()) {
                Entry entry = inputEntries.next();
                byte[] inputContent = FileUtils.readFileToByteArray(new File(new URI(entry.getFileURIString())));
                byte[] outputContent = FileUtils.readFileToByteArray(new File(directory.getAbsolutePath() + virtualPathHelper.getVirtualPath(entry.getSymbolicName())));

                // Verify result.
                Assert.assertArrayEquals(inputContent, outputContent);
            }
        } finally {
            FileUtils.deleteDirectory(directory);

            // Release resources.
            environment.release();
        }
    }

}
