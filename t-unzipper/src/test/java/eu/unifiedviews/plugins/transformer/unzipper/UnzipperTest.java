package eu.unifiedviews.plugins.transformer.unzipper;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.helpers.dataunit.fileshelper.FilesHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ FilesHelper.class, VirtualPathHelpers.class })
public class UnzipperTest {

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
        // prepare DPU
        UnZipperConfig_V1 config = new UnZipperConfig_V1();

        dpu = new UnZipper();
        dpu.configureDirectly(config);

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

    @Test
    public void encryptedArchiveFail() throws Exception {
        addFileToInput(ZIPPED_ENCRYPTED_FILE);
        env.run(dpu);

        assertEquals("Execution should have ended with error, because archive was encrypted!", true, env.getContext().isPublishedError());
    }

    @Test
    public void corruptedFileFail() {
        try {
            addFileToInput(ZIPPED_CORRUPTED_FILE);
            env.run(dpu);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals("Execution should have ended with error, because archive was corrupted!", true, env.getContext().isPublishedError());
    }

    @Test
    public void dataUnitIteratorFails() {
        PowerMockito.mockStatic(FilesHelper.class);
        try {
            PowerMockito.doThrow(new DataUnitException("")).when(FilesHelper.class, "getFiles", any());
            env.run(dpu);
            assertEquals("Error shoud have been published!", true, env.getContext().isPublishedError());
        } catch (Exception ex) {
        }
    }

    @Test
    public void virtualPathHelperFails() {
        PowerMockito.mockStatic(VirtualPathHelpers.class);
        try {
            PowerMockito.doThrow(new DataUnitException("")).when(VirtualPathHelpers.class, "getVirtualPath", any(), anyString());
            addFileToInput(ZIPPED_FILE);
            env.run(dpu);
            assertEquals("Error shoud have been published!", true, env.getContext().isPublishedError());
        } catch (Exception ex) {
        }
    }

    private void addFileToInput(final String filename) throws Exception {
        input.addExistingFile(filename, this.getClass().getClassLoader().getResource(filename).toString());
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
        } catch (DataUnitException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    /**
     * Reads content form resource file.
     * 
     * @param input
     *            Name of resourcer file.
     * @return Contents of file.
     */
    private String readFile(File input) {
        try (FileInputStream inputStream = new FileInputStream(input)) {
            return IOUtils.toString(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
