package eu.unifiedviews.plugins.transformer.zipper;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

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
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.fileshelper.FilesHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ FilesHelper.class, VirtualPathHelpers.class })
public class ZipperTest {

    private Zipper dpu;

    private TestEnvironment env;

    private WritableFilesDataUnit input;

    private WritableFilesDataUnit output;

    public static final String TXT_FILE = "fileToZip.txt";

    @Before
    public void before() throws Exception {
        // prepare DPU
        ZipperConfig_V1 config = new ZipperConfig_V1();

        dpu = new Zipper();
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
    public void validZipPasses() throws Exception {
        String resource = this.getClass().getClassLoader().getResource(TXT_FILE).getFile();
        File inputFile = new File(resource);
        String fileContent = readFile(inputFile);

        addFileToInput(TXT_FILE);
        env.run(dpu);

        FilesDataUnit.Entry entry = output.getIteration().next();
        File zipArchive = new File(java.net.URI.create(entry.getFileURIString()));
        String unzippedFileContent = readZippedFile(TXT_FILE, zipArchive);

        assertEquals("Content of file before and after zipping should match!", fileContent, unzippedFileContent);
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
            PowerMockito.doThrow(new DataUnitException("")).when(VirtualPathHelpers.class, "setVirtualPath", any(), anyString(), anyString());
            addFileToInput(TXT_FILE);
            env.run(dpu);
        } catch (Exception ex) {
            assertEquals("Exception should be of DPUException instance", true, ex instanceof DPUException);
        }
    }

    private void addFileToInput(final String filename) throws Exception {
        input.addExistingFile(filename, this.getClass().getClassLoader().getResource(filename).toString());
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

    /**
     * Method reads ONE! file from zipped archive.
     * Archive must contain at least one file.
     * 
     * @param fileName
     *            Name of file inside of an archive.
     * @param zipFile
     *            Archive file.
     * @return Contents of zipped file.
     */
    private String readZippedFile(String fileName, File zipFile) {
        try {
            Path baseDirectory = zipFile.toPath().getParent();
            ZipFile archive = new ZipFile(zipFile);

            archive.extractAll(baseDirectory.toString());
            Path extractedFile = baseDirectory.resolve(fileName);

            return readFile(extractedFile.toFile());
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return null;
    }

}
