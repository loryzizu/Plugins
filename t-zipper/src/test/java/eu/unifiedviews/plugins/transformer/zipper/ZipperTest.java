package eu.unifiedviews.plugins.transformer.zipper;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.MetadataDataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.helpers.dataunit.files.FilesDataUnitUtils;
import eu.unifiedviews.helpers.dataunit.metadata.MetadataUtils;
import eu.unifiedviews.helpers.dpu.test.config.ConfigurationBuilder;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MetadataUtils.class })
public class ZipperTest {

    private static final Logger LOG = LoggerFactory.getLogger(ZipperTest.class);

    private static final String TXT_FILE = "fileToZip.txt";

    private Zipper dpu;

    private TestEnvironment env;

    private WritableFilesDataUnit input;

    private WritableFilesDataUnit output;

    /**
     * Used to count number of failures in mocks.
     */
    private Integer failCounter;

    @Before
    public void before() throws Exception {
        final ZipperConfig_V1 config = new ZipperConfig_V1();

        dpu = new Zipper();
        dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        env = new TestEnvironment();
        input = env.createFilesInput("input");
        output = env.createFilesOutput("output");

        failCounter = 0;
    }

    @After
    public void after() throws Exception {
        env.release();
    }

    @Test
    public void validZipPasses() throws Exception {
        executeValidZipPasses();
    }

    /**
     * We let fail a call of MetadataUtils.getFirst that is used to get VirtualPath for incoming files.
     * 
     * @throws Exception
     */
    @Test
    public void metadataDataUnitFailsTwice() throws Exception {
        PowerMockito.mockStatic(MetadataUtils.class);
        PowerMockito.when(MetadataUtils.getFirst(Mockito.any(MetadataDataUnit.class),
                Mockito.any(MetadataDataUnit.Entry.class), Mockito.anyString())).then(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                // Fail twice to test fault tolerance.
                if (failCounter < 2) {
                    ++failCounter;
                    throw new java.sql.BatchUpdateException();
                } else {
                    return TXT_FILE;
                }
            }
        });
        // Execute DPU's code.
        executeValidZipPasses();
    }

    private void executeValidZipPasses() throws Exception {
        URI resource = this.getClass().getClassLoader().getResource(TXT_FILE).toURI();
        File inputFile = new File(resource);
        String fileContent = readFile(inputFile);

        FilesDataUnitUtils.addFile(input, inputFile, TXT_FILE);

        env.run(dpu);

        FilesDataUnit.Entry entry = output.getIteration().next();
        File zipArchive = new File(java.net.URI.create(entry.getFileURIString()));

        String unzippedFileContent = readZippedFile(TXT_FILE, zipArchive);
        assertEquals("Content of file before and after zipping should match!", fileContent, unzippedFileContent);
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
            LOG.error("", ex);
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
        } catch (ZipException ex) {
            LOG.error("", ex);
        }
        return null;
    }

}
