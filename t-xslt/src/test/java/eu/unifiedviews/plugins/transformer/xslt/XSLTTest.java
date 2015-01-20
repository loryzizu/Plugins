package eu.unifiedviews.plugins.transformer.xslt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.fileshelper.FilesHelper;
import eu.unifiedviews.helpers.dataunit.maphelper.MapHelper;
import eu.unifiedviews.helpers.dataunit.maphelper.MapHelpers;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;

public class XSLTTest {

    private XSLT dpu;

    private TestEnvironment env;

    private WritableFilesDataUnit input;

    private WritableFilesDataUnit output;

    @Before
    public void before() throws Exception {
        // prepare DPU
        XSLTConfig_V1 config = new XSLTConfig_V1();

        dpu = new XSLT();
        dpu.configureDirectly(config);

        env = new TestEnvironment();
        input = env.createFilesInput("filesInput");
        output = env.createFilesOutput("filesOutput");
    }

    @After
    public void after() throws Exception {
        env.release();
    }

    @Test
    public void passForValidXmlAndTemplate() throws Exception {
        addFileToInput("valid-for-xslt.xml");

        XSLTConfig_V1 config = new XSLTConfig_V1();
        config.setXslTemplate(resourceAsString("produce-list.xslt"));
        dpu.configureDirectly(config);

        env.run(dpu);

        assertNonEmptyOutput();
    }

    @Test
    public void throwExceptionWhenInvalidTemplateSpecified() throws Exception {
        addFileToInput("valid.xml");

        XSLTConfig_V1 config = new XSLTConfig_V1();
        config.setXslTemplate(resourceAsString("invalid.xslt"));
        dpu.configureDirectly(config);

        try {
            env.run(dpu);
            fail("Should throw DPUException: template is invalid");
        } catch (DPUException e) {
            // good job!
        }
    }

    @Test
    public void throwExceptionWhenNoTemplateIsSpecified() throws Exception {
        addFileToInput("valid.xml");

        try {
            env.run(dpu);
            fail("Should throw DPUException: no template specified");
        } catch (DPUException e) {
            // good job!
        }
    }

    @Test
    public void throwExceptionWhenNonValidXmlIsSpecified() throws Exception {
        addFileToInput("not-well-formed.xml");

        try {
            env.run(dpu);
            fail("Should throw DPUException: invalid XML on input");
        } catch (DPUException e) {
            // good job!
        }
    }

    @Test
    public void checkTemplateVersion2IsSupported() throws Exception {
        addFileToInput("document-for-template-version-2.xml");

        XSLTConfig_V1 config = new XSLTConfig_V1();
        config.setXslTemplate(resourceAsString("template-version-2.xslt"));
        dpu.configureDirectly(config);

        env.run(dpu);

        assertNonEmptyOutput();
    }

    @Test
    public void checkCorrectOutputFileExtension() throws Exception {
        addFileToInput("valid-for-xslt.xml");

        XSLTConfig_V1 config = new XSLTConfig_V1();
        config.setXslTemplate(resourceAsString("produce-list.xslt"));
        config.setOutputFileExtension(".extension");
        dpu.configureDirectly(config);

        env.run(dpu);

        VirtualPathHelper helper = VirtualPathHelpers.create(output);
        List<FilesDataUnit.Entry> outputList = new ArrayList<>(FilesHelper.getFiles(output));
        Iterator<FilesDataUnit.Entry> outputIterator = outputList.iterator();

        while (outputIterator.hasNext()) {
            String symbolicName = outputIterator.next().getSymbolicName();
            assertEquals("test.extension", helper.getVirtualPath(symbolicName));
        }
    }

    @Test
    public void checkParamsFromMapAreInOutput() throws Exception {
        // map of parameters
        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("one", "1");
        parameterMap.put("two", "2");
        String mapName = "map-name";

        // store map in input
        MapHelper mapHelper = MapHelpers.create(input);
        List<FilesDataUnit.Entry> inputList = new ArrayList<>(FilesHelper.getFiles(input));
        Iterator<FilesDataUnit.Entry> inputIterator = inputList.iterator();

        while (inputIterator.hasNext()) {
            String symbolicName = inputIterator.next().getSymbolicName();
            mapHelper.putMap(symbolicName, mapName, parameterMap);
        }

        // configure DPU
        XSLTConfig_V1 config = new XSLTConfig_V1();
        config.setXslTemplate(resourceAsString("output-params.xslt"));
        config.setXsltParametersMapName(mapName);
        dpu.configureDirectly(config);

        // run DPU
        env.run(dpu);

        // check output for parameters
        List<FilesDataUnit.Entry> outputList = new ArrayList<>(FilesHelper.getFiles(output));
        Iterator<FilesDataUnit.Entry> outputIterator = outputList.iterator();

        while (outputIterator.hasNext()) {
            String outputContents = IOUtils.toString(URI.create(outputIterator.next().getFileURIString()), "UTF-8");

            // verify result
            assertTrue("Output contents should contain string '1'", outputContents.contains("1"));
            assertTrue("Output contents should contain string '2'", outputContents.contains("2"));
        }
    }

    private void assertNonEmptyOutput() throws Exception {
        List<FilesDataUnit.Entry> outputList = new ArrayList<>(FilesHelper.getFiles(output));

        // iterate over files
        Iterator<FilesDataUnit.Entry> outputIterator = outputList.iterator();

        while (outputIterator.hasNext()) {
            String outputContents = IOUtils.toString(URI.create(outputIterator.next().getFileURIString()), "UTF-8");

            // verify result
            assertTrue("Transformation should generate output", !outputContents.isEmpty());
        }
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

}
