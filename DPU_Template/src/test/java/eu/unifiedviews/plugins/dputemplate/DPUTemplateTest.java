package eu.unifiedviews.plugins.dputemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.helpers.dataunit.fileshelper.FilesHelper;
import eu.unifiedviews.helpers.dataunit.maphelper.MapHelpers;

public class DPUTemplateTest {
    private static final Logger LOG = LoggerFactory.getLogger(DPUTemplateTest.class);

    @Test
    public void test1() throws Exception {
        DPUTemplate dpu = new DPUTemplate();
        DPUTemplateConfig_V1 config = new DPUTemplateConfig_V1();
        config.setOutputFilename("list_of_items.csv");
        config.setSkipGraphOnError(false);
        dpu.configureDirectly(config);

        // prepare test environment
        TestEnvironment env = new TestEnvironment();

        // prepare data units
        WritableRDFDataUnit rdfInput = env.createRdfInput("rdfInput", false);
        WritableFilesDataUnit filesOutput = env.createFilesOutput("filesOutput");

        InputStream testOutput = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("onetriple.ttl");

        RepositoryConnection connection = null;
        try {
            connection = rdfInput.getConnection();
            URI graph = rdfInput.addNewDataGraph("sample_graph");
            ValueFactory valueFactory = connection.getValueFactory();
            connection.add(valueFactory.createStatement(
                    valueFactory.createURI("http://example.com/subject"),
                    DCTERMS.TITLE,
                    valueFactory.createLiteral("John")), graph);

            Map<String, String> map = new HashMap<>();
            map.put("-p", "value");
            map.put("-o", "stdout");
            map.put("--verbose", "true");
            MapHelpers.putMap(rdfInput, "sample_graph", "commandline_parameters", map);

            env.run(dpu);

            Set<FilesDataUnit.Entry> outputFiles = FilesHelper.getFiles(filesOutput);
            assertNotNull(outputFiles);
            assertEquals(2, outputFiles.size());
            FilesDataUnit.Entry outputFile = null;
            FilesDataUnit.Entry outputMetaFile = null;
            for (FilesDataUnit.Entry entry : outputFiles) {
                if ("sample_graph".equals(entry.getSymbolicName())) {
                    outputFile = entry;
                } else if ("list_of_items.csv".equals(entry.getSymbolicName())) {
                    outputMetaFile = entry;
                }
            }
            assertNotNull(outputFile);
            assertNotNull(outputMetaFile);
            Map<String, String> map2 = MapHelpers.getMap(filesOutput, outputFile.getSymbolicName(), "commandline_parameters");
            assertNotNull(map2);
            assertEquals(3, map2.size());
            assertEquals("value", map2.get("-p"));
            assertEquals("stdout", map2.get("-o"));
            assertEquals("true", map2.get("--verbose"));
            assertEquals(IOUtils.toString(testOutput, "UTF-8"), IOUtils.toString(java.net.URI.create(outputFile.getFileURIString()), "UTF-8"));

            assertEquals("sample_graph;" + graph.stringValue() + ";" + new File(java.net.URI.create(outputFile.getFileURIString())).getCanonicalPath() + "\n", IOUtils.toString(java.net.URI.create(outputMetaFile.getFileURIString()), "UTF-8"));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            } // release resources
            env.release();
        }
    }
}
