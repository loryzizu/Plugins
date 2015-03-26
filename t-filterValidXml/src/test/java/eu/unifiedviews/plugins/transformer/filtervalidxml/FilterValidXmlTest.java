package eu.unifiedviews.plugins.transformer.filtervalidxml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import eu.unifiedviews.helpers.dataunit.files.FilesHelper;
import eu.unifiedviews.helpers.dpu.localization.Messages;
import eu.unifiedviews.helpers.dpu.test.config.ConfigurationBuilder;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit.Entry;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;

public class FilterValidXmlTest {

    private FilterValidXml dpu;

    private TestEnvironment env;

    private WritableFilesDataUnit input;

    private WritableFilesDataUnit outputValid;

    private WritableFilesDataUnit outputInvalid;

    private static Messages messages;
    @Before
    public void before() throws Exception {
        // prepare DPU
        FilterValidXmlConfig_V1 config = new FilterValidXmlConfig_V1();

        dpu = new FilterValidXml();
        dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());
        env = new TestEnvironment();
        messages = new Messages(env.getContext().getLocale(), getClass().getClassLoader());
        input = env.createFilesInput("input");
        outputValid = env.createFilesOutput("outputValid");
        outputInvalid = env.createFilesOutput("outputInvalid");
    }

    @After
    public void after() throws Exception {
        env.release();
    }

    @Test
    public void validXmlPasses() throws Exception {
        addFileToInput("valid.xml");
        env.run(dpu);
        assertInputOutputIdentical();
    }

    @Test
    public void notWellFormedXmlIsReported() throws Exception {
        addFileToInput("not-well-formed.xml");

        env.run(dpu);
        assertOutputContainsFile(outputInvalid, "not-well-formed.xml");
    }

    @Test
    public void validXmlWithXsdPasses() throws Exception {
        addFileToInput("valid.xml");

        FilterValidXmlConfig_V1 config = new FilterValidXmlConfig_V1();
        config.setXsdContents(resourceAsString("maven-4.0.0.xsd"));
        dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        env.run(dpu);
        assertInputOutputIdentical();
    }

    @Test
    public void doesNotConformToXsdIsReported() throws Exception {
        addFileToInput("invalid-by-xsd.xml");

        FilterValidXmlConfig_V1 config = new FilterValidXmlConfig_V1();
        config.setXsdContents(resourceAsString("maven-4.0.0.xsd"));
        dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        env.run(dpu);
        assertOutputContainsFile(outputInvalid, "invalid-by-xsd.xml");
    }

    @Test
    public void invalidXsdIsReported() throws Exception {
        addFileToInput("valid.xml");

        FilterValidXmlConfig_V1 config = new FilterValidXmlConfig_V1();
        config.setXsdContents(resourceAsString("invalid.xsd"));
        dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        env.run(dpu);
        assertOutputContainsFile(outputInvalid, "valid.xml");
    }

    @Test
    public void invalidXsltIsReported() throws Exception {
        addFileToInput("valid.xml");

        FilterValidXmlConfig_V1 config = new FilterValidXmlConfig_V1();
        config.setXsltContents(resourceAsString("invalid.xslt"));
        dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        env.run(dpu);
        assertOutputContainsFile(outputInvalid, "valid.xml");
    }

    @Test
    public void validXmlWithXsltPasses() throws Exception {
        addFileToInput("valid.xml");

        FilterValidXmlConfig_V1 config = new FilterValidXmlConfig_V1();
        config.setXsltContents(resourceAsString("always-good.xslt"));
        dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        env.run(dpu);
        assertInputOutputIdentical();
    }

    @Test
    public void invalidXmlWithXsltIsReported() throws Exception {
        addFileToInput("valid-for-xslt.xml");

        FilterValidXmlConfig_V1 config = new FilterValidXmlConfig_V1();
        config.setXsltContents(resourceAsString("always-bad.xslt"));
        dpu.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        env.run(dpu);
        assertOutputContainsFile(outputInvalid, "valid-for-xslt.xml");
    }

    private void addFileToInput(final String filename) throws Exception {
        input.addExistingFile("test", Thread.currentThread().getContextClassLoader().getResource(filename).toString());
    }

    private String resourceAsString(final String filename) {
        String result = "";

        try {
            result = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(filename), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void assertInputOutputIdentical() throws Exception {
        List<FilesDataUnit.Entry> inputList = new ArrayList<FilesDataUnit.Entry>(FilesHelper.getFiles(input));
        List<FilesDataUnit.Entry> outputList = new ArrayList<FilesDataUnit.Entry>(FilesHelper.getFiles(outputValid));

        Collections.sort(inputList, new SymbolicNameComparator());
        Collections.sort(outputList, new SymbolicNameComparator());

        // iterate over files
        Iterator<FilesDataUnit.Entry> inputIterator = inputList.iterator();
        Iterator<FilesDataUnit.Entry> outputIterator = outputList.iterator();

        while (inputIterator.hasNext()) {
            String inputContents = IOUtils.toString(URI.create(inputIterator.next().getFileURIString()), "UTF-8");
            String outputContents = IOUtils.toString(URI.create(outputIterator.next().getFileURIString()), "UTF-8");

            // verify result
            assertEquals(inputContents, outputContents);
        }
    }

    private void assertOutputContainsFile(WritableFilesDataUnit output, String symbolicName) throws DataUnitException{
        List<FilesDataUnit.Entry> outputList = new ArrayList<FilesDataUnit.Entry>(FilesHelper.getFiles(output));
        boolean contains = false;
        for(Entry entry : outputList) {
            if(entry.getFileURIString().endsWith(symbolicName)){
                contains = true;
            }
        }
        assertEquals("Should return true.", true, contains);
    }

    /**
     * Sort FilesDataUnit.Entry List alphabetically based on its symbolic names.
     * Fails the running unit-test if it cannot retrieve either symbolic name.
     * 
     * @author Viktor Lieskovsky
     */
    private class SymbolicNameComparator implements Comparator<FilesDataUnit.Entry> {

        @Override
        public int compare(final Entry o1, final Entry o2) {
            String symbolicName1 = null, symbolicName2 = null;

            try {
                symbolicName1 = o1.getSymbolicName();
                symbolicName2 = o2.getSymbolicName();
            } catch (DataUnitException e) {
                e.printStackTrace();
                fail("Cannot get symbolic name: " + e.getMessage());
            }

            return symbolicName1.compareTo(symbolicName2);
        }

    }
}
