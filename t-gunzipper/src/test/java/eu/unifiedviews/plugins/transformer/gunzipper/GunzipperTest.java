package eu.unifiedviews.plugins.transformer.gunzipper;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.helpers.dataunit.files.FilesHelper;

public class GunzipperTest {
    @Test
    public void testSmallFile() throws Exception {
        // Prepare DPU.
        Gunzipper dpu = new Gunzipper();

        // Prepare test environment.
        TestEnvironment environment = new TestEnvironment();

        // Prepare data unit.
        WritableFilesDataUnit filesOutput = environment.createFilesOutput("filesOutput");
        WritableFilesDataUnit filesInput = environment.createFilesInput("filesInput");
        
        File inputFile = new File(URI.create(filesInput.addNewFile("LICENSE")));
        try (FileOutputStream fout = new FileOutputStream(inputFile)) {
            IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream("LICENSE.gz"), fout);
        }
        try {
            // Run.
            environment.run(dpu);

            // Get file iterator.
            Set<FilesDataUnit.Entry> outputFiles = FilesHelper.getFiles(filesOutput);
            Assert.assertEquals(1, outputFiles.size());
            
            FilesDataUnit.Entry entry = outputFiles.iterator().next();
            byte[] outputContent = FileUtils.readFileToByteArray(new File(new URI(entry.getFileURIString())));
            byte[] expectedContent = IOUtils.toByteArray(Thread.currentThread().getContextClassLoader().getResourceAsStream("LICENSE"));
            
            Assert.assertArrayEquals(expectedContent, outputContent);
        } finally {
            // Release resources.
            environment.release();
        }
    }
}
