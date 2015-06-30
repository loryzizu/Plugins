package eu.unifiedviews.plugins.transformer.rdfvalidator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import eu.unifiedviews.helpers.dataunit.rdf.RDFHelper;
import eu.unifiedviews.helpers.dpu.test.config.ConfigurationBuilder;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.turtle.TurtleWriter;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.plugins.transformer.rdfvalidator.RdfValidator;
import eu.unifiedviews.plugins.transformer.rdfvalidator.RdfValidatorConfig_V1;

public class RdfValidatorTest {

    @Test
    public void testAsk() throws Exception {
        // Prepare config.
        RdfValidatorConfig_V1 config = new RdfValidatorConfig_V1();
        config.setFailExecution(false);
        config.setOutputGraphSymbolicName("output1");
        config.setPerGraph(false);
        config.setQuery("ASK { ?s ?p ?o }");

        // Prepare DPU.
        RdfValidator rdfDataValidator = new RdfValidator();
        rdfDataValidator.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        // Prepare test environment.
        TestEnvironment environment = new TestEnvironment();

        // Prepare data unit.
        WritableRDFDataUnit input = environment.createRdfInput("rdfInput", false);
        WritableRDFDataUnit output = environment.createRdfOutput("rdfOutput", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");

        RepositoryConnection connection = null;
        try {
            connection = input.getConnection();
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, "", RDFFormat.TURTLE, graph);
            ByteArrayOutputStream inputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(inputBos), graph);
            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // Run.
            environment.run(rdfDataValidator);

            // verify result
            assertTrue(connection.size(graph) == connection.size(RDFHelper.getGraphsURIArray(output)));
            ByteArrayOutputStream outputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(outputBos), RDFHelper.getGraphsURIArray(output));

            assertEquals(inputBos.toString("UTF-8"), outputBos.toString("UTF-8"));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {

                }
            }
            // Release resources.
            environment.release();
        }
    }

    @Test(expected = DPUException.class)
    public void testAskFailExec() throws Exception {
        // Prepare config.
        RdfValidatorConfig_V1 config = new RdfValidatorConfig_V1();
        config.setFailExecution(true);
        config.setOutputGraphSymbolicName("output1");
        config.setPerGraph(false);
        config.setQuery("ASK { ?s ?p ?o }");

        // Prepare DPU.
        RdfValidator rdfDataValidator = new RdfValidator();
        rdfDataValidator.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());
        // Prepare test environment.
        TestEnvironment environment = new TestEnvironment();

        // Prepare data unit.
        WritableRDFDataUnit input = environment.createRdfInput("rdfInput", false);
        WritableRDFDataUnit output = environment.createRdfOutput("rdfOutput", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");

        RepositoryConnection connection = null;
        try {
            connection = input.getConnection();
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, "", RDFFormat.TURTLE, graph);
            ByteArrayOutputStream inputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(inputBos), graph);
            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // Run.
            environment.run(rdfDataValidator);

            // verify result
            assertTrue(connection.size(graph) == connection.size(RDFHelper.getGraphsURIArray(output)));
            ByteArrayOutputStream outputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(outputBos), RDFHelper.getGraphsURIArray(output));

            assertEquals(inputBos.toString("UTF-8"), outputBos.toString("UTF-8"));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {

                }
            }
            // Release resources.
            environment.release();
        }
    }

    @Test
    public void testAskPerGraph() throws Exception {
        // Prepare config.
        RdfValidatorConfig_V1 config = new RdfValidatorConfig_V1();
        config.setFailExecution(false);
        config.setOutputGraphSymbolicName("output1");
        config.setPerGraph(true);
        config.setQuery("ASK { ?s ?p ?o }");

        // Prepare DPU.
        RdfValidator rdfDataValidator = new RdfValidator();
        rdfDataValidator.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        // Prepare test environment.
        TestEnvironment environment = new TestEnvironment();

        // Prepare data unit.
        WritableRDFDataUnit input = environment.createRdfInput("rdfInput", false);
        WritableRDFDataUnit output = environment.createRdfOutput("rdfOutput", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");

        RepositoryConnection connection = null;
        try {
            connection = input.getConnection();
            input.addNewDataGraph("testEmpty");
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, "", RDFFormat.TURTLE, graph);
            ByteArrayOutputStream inputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(inputBos), graph);
            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // Run.
            environment.run(rdfDataValidator);

            // verify result
            Map<String, RDFDataUnit.Entry> outputGraphs = RDFHelper.getGraphsMap(output);

            assertTrue(connection.size(graph) == connection.size(outputGraphs.get("test").getDataGraphURI()));
            ByteArrayOutputStream outputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(outputBos), outputGraphs.get("test").getDataGraphURI());

            assertEquals(inputBos.toString("UTF-8"), outputBos.toString("UTF-8"));

            assertTrue(connection.size(outputGraphs.get("testEmpty").getDataGraphURI()) == 0);
            assertEquals(2, outputGraphs.keySet().size());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {

                }
            }
            // Release resources.
            environment.release();
        }
    }

    @Test
    public void testAskEmptyQuery() throws Exception {
        // Prepare config.
        RdfValidatorConfig_V1 config = new RdfValidatorConfig_V1();
        config.setFailExecution(false);
        config.setOutputGraphSymbolicName("output1");
        config.setPerGraph(true);
        config.setQuery("ASK { ?s ?s ?s }");

        // Prepare DPU.
        RdfValidator rdfDataValidator = new RdfValidator();
        rdfDataValidator.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        // Prepare test environment.
        TestEnvironment environment = new TestEnvironment();

        // Prepare data unit.
        WritableRDFDataUnit input = environment.createRdfInput("rdfInput", false);
        WritableRDFDataUnit output = environment.createRdfOutput("rdfOutput", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");

        RepositoryConnection connection = null;
        try {
            connection = input.getConnection();
            input.addNewDataGraph("testEmpty");
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, "", RDFFormat.TURTLE, graph);
            ByteArrayOutputStream inputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(inputBos), graph);
            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // Run.
            environment.run(rdfDataValidator);

            // verify result
            Map<String, RDFDataUnit.Entry> outputGraphs = RDFHelper.getGraphsMap(output);

            assertEquals(0, connection.size(outputGraphs.get("test").getDataGraphURI()));
            assertTrue(connection.size(outputGraphs.get("testEmpty").getDataGraphURI()) == 0);
            assertEquals(2, outputGraphs.keySet().size());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {

                }
            }
            // Release resources.
            environment.release();
        }
    }
    
    
    @Test
    public void test() throws Exception {
        // Prepare config.
        RdfValidatorConfig_V1 config = new RdfValidatorConfig_V1();
        config.setFailExecution(false);
        config.setOutputGraphSymbolicName("output1");
        config.setPerGraph(false);
        config.setQuery("SELECT { ?s ?p ?o } WHERE {?s ?p ?o }");

        // Prepare DPU.
        RdfValidator rdfDataValidator = new RdfValidator();
        rdfDataValidator.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        // Prepare test environment.
        TestEnvironment environment = new TestEnvironment();

        // Prepare data unit.
        WritableRDFDataUnit input = environment.createRdfInput("rdfInput", false);
        WritableRDFDataUnit output = environment.createRdfOutput("rdfOutput", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");

        RepositoryConnection connection = null;
        try {
            connection = input.getConnection();
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, "", RDFFormat.TURTLE, graph);
            ByteArrayOutputStream inputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(inputBos), graph);
            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // Run.
            environment.run(rdfDataValidator);

            // verify result
            assertTrue(connection.size(graph) == connection.size(RDFHelper.getGraphsURIArray(output)));
            ByteArrayOutputStream outputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(outputBos), RDFHelper.getGraphsURIArray(output));

            assertEquals(inputBos.toString("UTF-8"), outputBos.toString("UTF-8"));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {

                }
            }
            // Release resources.
            environment.release();
        }
    }

    @Test(expected = DPUException.class)
    public void testFailExec() throws Exception {
        // Prepare config.
        RdfValidatorConfig_V1 config = new RdfValidatorConfig_V1();
        config.setFailExecution(true);
        config.setOutputGraphSymbolicName("output1");
        config.setPerGraph(false);
        config.setQuery("SELECT { ?s ?p ?o } WHERE {?s ?p ?o }");

        // Prepare DPU.
        RdfValidator rdfDataValidator = new RdfValidator();
        rdfDataValidator.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());
        // Prepare test environment.
        TestEnvironment environment = new TestEnvironment();

        // Prepare data unit.
        WritableRDFDataUnit input = environment.createRdfInput("rdfInput", false);
        WritableRDFDataUnit output = environment.createRdfOutput("rdfOutput", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");

        RepositoryConnection connection = null;
        try {
            connection = input.getConnection();
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, "", RDFFormat.TURTLE, graph);
            ByteArrayOutputStream inputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(inputBos), graph);
            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // Run.
            environment.run(rdfDataValidator);

            // verify result
            assertTrue(connection.size(graph) == connection.size(RDFHelper.getGraphsURIArray(output)));
            ByteArrayOutputStream outputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(outputBos), RDFHelper.getGraphsURIArray(output));

            assertEquals(inputBos.toString("UTF-8"), outputBos.toString("UTF-8"));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {

                }
            }
            // Release resources.
            environment.release();
        }
    }

    @Test
    public void testPerGraph() throws Exception {
        // Prepare config.
        RdfValidatorConfig_V1 config = new RdfValidatorConfig_V1();
        config.setFailExecution(false);
        config.setOutputGraphSymbolicName("output1");
        config.setPerGraph(true);
        config.setQuery("SELECT { ?s ?p ?o } WHERE {?s ?p ?o }");

        // Prepare DPU.
        RdfValidator rdfDataValidator = new RdfValidator();
        rdfDataValidator.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        // Prepare test environment.
        TestEnvironment environment = new TestEnvironment();

        // Prepare data unit.
        WritableRDFDataUnit input = environment.createRdfInput("rdfInput", false);
        WritableRDFDataUnit output = environment.createRdfOutput("rdfOutput", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");

        RepositoryConnection connection = null;
        try {
            connection = input.getConnection();
            input.addNewDataGraph("testEmpty");
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, "", RDFFormat.TURTLE, graph);
            ByteArrayOutputStream inputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(inputBos), graph);
            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // Run.
            environment.run(rdfDataValidator);

            // verify result
            Map<String, RDFDataUnit.Entry> outputGraphs = RDFHelper.getGraphsMap(output);

            assertTrue(connection.size(graph) == connection.size(outputGraphs.get("test").getDataGraphURI()));
            ByteArrayOutputStream outputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(outputBos), outputGraphs.get("test").getDataGraphURI());

            assertEquals(inputBos.toString("UTF-8"), outputBos.toString("UTF-8"));

            assertTrue(connection.size(outputGraphs.get("testEmpty").getDataGraphURI()) == 0);
            assertEquals(2, outputGraphs.keySet().size());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {

                }
            }
            // Release resources.
            environment.release();
        }
    }

    @Test
    public void testEmptyQuery() throws Exception {
        // Prepare config.
        RdfValidatorConfig_V1 config = new RdfValidatorConfig_V1();
        config.setFailExecution(false);
        config.setOutputGraphSymbolicName("output1");
        config.setPerGraph(true);
        config.setQuery("SELECT {?s ?p ?o } WHERE {?s ?s ?s }");

        // Prepare DPU.
        RdfValidator rdfDataValidator = new RdfValidator();
        rdfDataValidator.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        // Prepare test environment.
        TestEnvironment environment = new TestEnvironment();

        // Prepare data unit.
        WritableRDFDataUnit input = environment.createRdfInput("rdfInput", false);
        WritableRDFDataUnit output = environment.createRdfOutput("rdfOutput", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");

        RepositoryConnection connection = null;
        try {
            connection = input.getConnection();
            input.addNewDataGraph("testEmpty");
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, "", RDFFormat.TURTLE, graph);
            ByteArrayOutputStream inputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(inputBos), graph);
            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // Run.
            environment.run(rdfDataValidator);

            // verify result
            Map<String, RDFDataUnit.Entry> outputGraphs = RDFHelper.getGraphsMap(output);

            assertEquals(0, connection.size(outputGraphs.get("test").getDataGraphURI()));
            assertTrue(connection.size(outputGraphs.get("testEmpty").getDataGraphURI()) == 0);
            assertEquals(2, outputGraphs.keySet().size());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {

                }
            }
            // Release resources.
            environment.release();
        }
    }

    @Test(expected = DPUException.class)
    public void testInvalidConfig() throws Exception {
        // Prepare config.
        RdfValidatorConfig_V1 config = new RdfValidatorConfig_V1();
        config.setFailExecution(false);
        config.setOutputGraphSymbolicName("");
        config.setPerGraph(false);
        config.setQuery("");

        // Prepare DPU.
        RdfValidator rdfDataValidator = new RdfValidator();
        rdfDataValidator.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        // Prepare test environment.
        TestEnvironment environment = new TestEnvironment();

        // Prepare data unit.
        WritableRDFDataUnit input = environment.createRdfInput("rdfInput", false);
        WritableRDFDataUnit output = environment.createRdfOutput("rdfOutput", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");

        RepositoryConnection connection = null;
        try {
            connection = input.getConnection();
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, "", RDFFormat.TURTLE, graph);
            ByteArrayOutputStream inputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(inputBos), graph);
            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // Run.
            environment.run(rdfDataValidator);

            // verify result
            assertTrue(connection.size(graph) == connection.size(RDFHelper.getGraphsURIArray(output)));
            ByteArrayOutputStream outputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(outputBos), RDFHelper.getGraphsURIArray(output));

            assertEquals(inputBos.toString("UTF-8"), outputBos.toString("UTF-8"));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {

                }
            }
            // Release resources.
            environment.release();
        }
    }

    @Test(expected = DPUException.class)
    public void testInvalidConfig1() throws Exception {
        // Prepare config.
        RdfValidatorConfig_V1 config = new RdfValidatorConfig_V1();
        config.setFailExecution(false);
        config.setOutputGraphSymbolicName("");
        config.setPerGraph(false);
        config.setQuery("SELECT ");

        // Prepare DPU.
        RdfValidator rdfDataValidator = new RdfValidator();
        rdfDataValidator.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        // Prepare test environment.
        TestEnvironment environment = new TestEnvironment();

        // Prepare data unit.
        WritableRDFDataUnit input = environment.createRdfInput("rdfInput", false);
        WritableRDFDataUnit output = environment.createRdfOutput("rdfOutput", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");

        RepositoryConnection connection = null;
        try {
            connection = input.getConnection();
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, "", RDFFormat.TURTLE, graph);
            ByteArrayOutputStream inputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(inputBos), graph);
            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // Run.
            environment.run(rdfDataValidator);

            // verify result
            assertTrue(connection.size(graph) == connection.size(RDFHelper.getGraphsURIArray(output)));
            ByteArrayOutputStream outputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(outputBos), RDFHelper.getGraphsURIArray(output));

            assertEquals(inputBos.toString("UTF-8"), outputBos.toString("UTF-8"));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {

                }
            }
            // Release resources.
            environment.release();
        }
    }

    @Test(expected = DPUException.class)
    public void testInvalidConfig2() throws Exception {
        // Prepare config.
        RdfValidatorConfig_V1 config = new RdfValidatorConfig_V1();
        config.setFailExecution(false);
        config.setOutputGraphSymbolicName(null);
        config.setPerGraph(false);
        config.setQuery("SELECT ");

        // Prepare DPU.
        RdfValidator rdfDataValidator = new RdfValidator();
        rdfDataValidator.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        // Prepare test environment.
        TestEnvironment environment = new TestEnvironment();

        // Prepare data unit.
        WritableRDFDataUnit input = environment.createRdfInput("rdfInput", false);
        WritableRDFDataUnit output = environment.createRdfOutput("rdfOutput", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");

        RepositoryConnection connection = null;
        try {
            connection = input.getConnection();
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, "", RDFFormat.TURTLE, graph);
            ByteArrayOutputStream inputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(inputBos), graph);
            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // Run.
            environment.run(rdfDataValidator);

            // verify result
            assertTrue(connection.size(graph) == connection.size(RDFHelper.getGraphsURIArray(output)));
            ByteArrayOutputStream outputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(outputBos), RDFHelper.getGraphsURIArray(output));

            assertEquals(inputBos.toString("UTF-8"), outputBos.toString("UTF-8"));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {

                }
            }
            // Release resources.
            environment.release();
        }
    }

    @Test(expected = DPUException.class)
    public void testInvalidConfig3() throws Exception {
        // Prepare config.
        RdfValidatorConfig_V1 config = new RdfValidatorConfig_V1();
        config.setFailExecution(false);
        config.setOutputGraphSymbolicName(null);
        config.setPerGraph(true);
        config.setQuery("ASK");

        // Prepare DPU.
        RdfValidator rdfDataValidator = new RdfValidator();
        rdfDataValidator.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        // Prepare test environment.
        TestEnvironment environment = new TestEnvironment();

        // Prepare data unit.
        WritableRDFDataUnit input = environment.createRdfInput("rdfInput", false);
        WritableRDFDataUnit output = environment.createRdfOutput("rdfOutput", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");

        RepositoryConnection connection = null;
        try {
            connection = input.getConnection();
            URI graph = input.addNewDataGraph("test");
            connection.add(inputStream, "", RDFFormat.TURTLE, graph);
            ByteArrayOutputStream inputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(inputBos), graph);
            // some triples has been loaded
            assertTrue(connection.size(graph) > 0);
            // Run.
            environment.run(rdfDataValidator);

            // verify result
            System.out.println(connection.size(graph));
            System.out.println(connection.size(RDFHelper.getGraphsURIArray(output)));
            assertTrue(connection.size(graph) == connection.size(RDFHelper.getGraphsURIArray(output)));

            ByteArrayOutputStream outputBos = new ByteArrayOutputStream();
            connection.export(new TurtleWriter(outputBos), RDFHelper.getGraphsURIArray(output));

            assertEquals(inputBos.toString("UTF-8"), outputBos.toString("UTF-8"));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException ex) {

                }
            }
            // Release resources.
            environment.release();
        }
    }
}
