package eu.unifiedviews.plugins.transformer.unzipper;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.files.FilesDataUnitUtils;
import eu.unifiedviews.helpers.dataunit.files.FilesHelper;
import eu.unifiedviews.helpers.dpu.test.config.ConfigurationBuilder;

/**
 * TODO Add fault tolerance tests!
 */
@RunWith(PowerMockRunner.class)
public class UnzipperTest {

    private static final Logger LOG = LoggerFactory.getLogger(UnzipperTest.class);

    private UnZipper dpu;

    private TestEnvironment env;

    private WritableFilesDataUnit input;

    private WritableFilesDataUnit output;

    public static final String UNZIPPED_FILE = "content.txt";

    public static final String ZIPPED_FILE = "archive.zip";

    public static final String ZIPPED_ENCRYPTED_FILE = "archive_encrypted.zip";

    public static final String ZIPPED_CORRUPTED_FILE = "archive_corrupted.zip";

    @Before
    public void before() throws Exception {
        UnZipperConfig_V1 config = new UnZipperConfig_V1();

        dpu = new UnZipper();
        dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        env = new TestEnvironment();
        input = env.createFilesInput("input");
        output = env.createFilesOutput("output");
    }

    @After
    public void after() throws Exception {
        env.release();
    }

    @Test
    public void validArchivePass() throws Exception {
        URI resource = this.getClass().getClassLoader().getResource(UNZIPPED_FILE).toURI();
        File inputFile = new File(resource);
        String fileContent = readFile(inputFile);

        addFileToInput(ZIPPED_FILE);

        env.run(dpu);
        String unzippedFileContent = readFileFromOutput();

        assertEquals("Content of unzipped file by DPU and resource file should match!", fileContent, unzippedFileContent);
    }

    @Test(expected = DPUException.class)
    public void encryptedArchiveFail() throws Exception {
        addFileToInput(ZIPPED_ENCRYPTED_FILE);
        env.run(dpu);
  }

    @Test(expected = DPUException.class)
    public void corruptedFileFail() throws Exception {
        addFileToInput(ZIPPED_CORRUPTED_FILE);
        env.run(dpu);
    }

    private void addFileToInput(final String filename) throws Exception {
        FilesDataUnitUtils.addFile(input,
                new File(this.getClass().getClassLoader().getResource(filename).toURI()),
                ZIPPED_FILE);
    }

    /**
     * Reads and joins content from output data unit.
     * 
     * @return Joined content.
     */
    private String readFileFromOutput() {
        StringBuilder sb = new StringBuilder();
        try {
            for (FilesDataUnit.Entry entry : FilesHelper.getFiles(output)) {
                File file = new File(java.net.URI.create(entry.getFileURIString()));
                sb.append(readFile(file));
            }
        } catch (DataUnitException ex) {
            LOG.error("Can't read file from output.", ex);
        }
        return sb.toString();
    }

    /**
     * Reads content form resource file.
     * 
     * @param input
     *            Name of resource file.
     * @return Contents of file.
     */
    private String readFile(File input) {
        try (FileInputStream inputStream = new FileInputStream(input)) {
            return IOUtils.toString(inputStream);
        } catch (IOException ex) {
            LOG.error("Can't read file.", ex);
        }
        return null;
    }
}
