package eu.unifiedviews.plugins.transformer.gzipper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.helpers.dataunit.files.FilesHelper;
import eu.unifiedviews.helpers.dataunit.virtualpath.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.test.config.ConfigurationBuilder;
import eu.unifiedviews.plugins.transformer.gzipper.Gzipper;
import eu.unifiedviews.plugins.transformer.gzipper.GzipperConfig_V1;

public class GzipperTest {

    @Test
    public void execute() throws Exception {
        GzipperConfig_V1 config = new GzipperConfig_V1();

        // Prepare DPU.
        Gzipper dpu = new Gzipper();
        dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        // Prepare test environment.
        TestEnvironment environment = new TestEnvironment();

        // Prepare data unit.
        WritableFilesDataUnit filesOutput = environment.createFilesOutput("filesOutput");
        WritableFilesDataUnit filesInput = environment.createFilesInput("filesInput");

        File inputFile = new File(URI.create(filesInput.addNewFile("LICENSE.pdf")));
        try (FileOutputStream fout = new FileOutputStream(inputFile)) {
            IOUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream("LICENSE.pdf"), fout);
        }
        try {
            // Run.
            environment.run(dpu);

            // Get file iterator.
            Set<FilesDataUnit.Entry> outputFiles = FilesHelper.getFiles(filesOutput);
            Assert.assertEquals(1, outputFiles.size());

            FilesDataUnit.Entry entry = outputFiles.iterator().next();

            byte[] outputContent = IOUtils.toByteArray(new GZIPInputStream(new FileInputStream(new File(new URI(entry.getFileURIString())))));
            byte[] expectedContent = IOUtils.toByteArray(Thread.currentThread().getContextClassLoader().getResourceAsStream("LICENSE.pdf"));

            Assert.assertArrayEquals(expectedContent, outputContent);
            
            Assert.assertEquals("LICENSE.pdf.gz", VirtualPathHelpers.getVirtualPath(filesOutput, "LICENSE.pdf"));
        } finally {
            // Release resources.
            environment.release();
        }
    }
}
