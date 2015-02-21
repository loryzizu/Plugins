package eu.unifiedviews.plugins.transformer.metadata;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;

import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.openrdf.model.vocabulary.FOAF;

import eu.unifiedviews.helpers.cuni.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.cuni.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.cuni.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.cuni.dpu.exec.AutoInitializer;
import eu.unifiedviews.helpers.cuni.extensions.FaultTolerance;
import eu.unifiedviews.helpers.cuni.migration.ConfigurationUpdate;
import eu.unifiedviews.helpers.cuni.rdf.EntityBuilder;
import eu.unifiedviews.helpers.cuni.rdf.simple.WritableSimpleRdf;
import eu.unifiedviews.helpers.cuni.rdf.sparql.SparqlUtils;
import eu.unifiedviews.helpers.dataunit.DataUnitUtils;
import eu.unifiedviews.helpers.dataunit.rdf.RdfDataUnitUtils;

@DPU.AsTransformer
public class Metadata extends AbstractDpu<MetadataConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(Metadata.class);

    @DataUnit.AsInput(name = "data", optional = true)
    public RDFDataUnit inRdfData;

    @DataUnit.AsOutput(name = "metadata")
    public WritableRDFDataUnit outRdfData;

    @AutoInitializer.Init(param = "outRdfData")
    public WritableSimpleRdf rdfData;

    @AutoInitializer.Init
    public FaultTolerance faultTolerance;

    @AutoInitializer.Init(param = "eu.unifiedviews.plugins.transformer.metadata.MetadataConfig__V1")
    public ConfigurationUpdate _ConfigurationUpdate;

    public Metadata() {
        super(MetadataVaadinDialog.class, ConfigHistory.noHistory(MetadataConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {
        final Date dateStart = new Date();

        generateMetadata();

        final Date dateEnd = new Date();
        ContextUtils.sendShortInfo(ctx, "Done in %d ms", (dateEnd.getTime() - dateStart.getTime()));
    }

    private void generateMetadata() throws DPUException {
        final ValueFactory valueFactory = faultTolerance.execute(
                new FaultTolerance.ActionReturn<ValueFactory>() {

                    @Override
                    public ValueFactory action() throws Exception {
                        return rdfData.getValueFactory();
                    }
                });
        // Set output graph.
        final RDFDataUnit.Entry entry = faultTolerance.execute(new FaultTolerance.ActionReturn<RDFDataUnit.Entry>() {

            @Override
            public RDFDataUnit.Entry action() throws Exception {
                return RdfDataUnitUtils.addGraph(outRdfData, MetadataVocabulary.STR_METADATA_GRAPH);
            }
        });
        faultTolerance.execute(new FaultTolerance.Action() {

            @Override
            public void action() throws Exception {
                rdfData.setOutput(entry);
            }
        });
        // Prepare dataset entity and fill it with data.
        final EntityBuilder dataset = new EntityBuilder(valueFactory.createURI(config.getDatasetURI()),
                valueFactory);
        dataset.property(RDF.TYPE, MetadataVocabulary.VOID_DATASET_CLASS);
        dataset.property(RDF.TYPE, MetadataVocabulary.DCAT_DATASET_CLASS);

        final EntityBuilder distro = new EntityBuilder(valueFactory.createURI(config.getDistroURI()),
                valueFactory);
        distro.property(RDF.TYPE, MetadataVocabulary.DCAT_DISTRO_CLASS);

        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // Build metadata ...
        if (!StringUtils.isBlank(config.getComsodeDatasetId())) {
            dataset.property(valueFactory.createURI("http://comsode.eu/ontology/dataset"),
                    valueFactory.createURI("http://comsode.eu/resource/dataset/" + config
                            .getComsodeDatasetId()));
        }
        if (config.isIsQb()) {
            dataset.property(RDF.TYPE, MetadataVocabulary.QB_DATA_SET);
        }
        // Description.
        if (!StringUtils.isBlank(config.getDesc_cs())) {
            dataset.property(DCTERMS.DESCRIPTION, valueFactory.createLiteral(config.getDesc_cs(),
                    config.getLanguage_cs()));
            distro.property(DCTERMS.DESCRIPTION, valueFactory.createLiteral(config.getDesc_cs(),
                    config.getLanguage_cs()));
        }
        if (!StringUtils.isBlank(config.getDesc_en())) {
            dataset.property(DCTERMS.DESCRIPTION, valueFactory.createLiteral(config.getDesc_en(), "en"));
            distro.property(DCTERMS.DESCRIPTION, valueFactory.createLiteral(config.getDesc_en(), "en"));
        }
        // Title.
        if (!StringUtils.isBlank(config.getTitle_cs())) {
            dataset.property(DCTERMS.TITLE, valueFactory.createLiteral(config.getTitle_cs(),
                    config.getLanguage_cs()));
            distro.property(DCTERMS.TITLE, valueFactory.createLiteral(config.getTitle_cs(),
                    config.getLanguage_cs()));
        }
        if (!StringUtils.isBlank(config.getTitle_en())) {
            dataset.property(DCTERMS.TITLE, valueFactory.createLiteral(config.getTitle_en(), "en"));
            distro.property(DCTERMS.TITLE, valueFactory.createLiteral(config.getTitle_en(), "en"));
        }
        //
        if (!StringUtils.isBlank(config.getDataDump())) {
            dataset.property(valueFactory.createURI(MetadataVocabulary.VOID + "dataDump"),
                    valueFactory.createURI(config.getDataDump()));

            distro.property(MetadataVocabulary.DCAT_DOWNLOAD_URL, valueFactory.createURI(config.getDataDump()));
            distro.property(MetadataVocabulary.DCAT_MEDIA_TYPE, valueFactory.createLiteral(config.getMime()));
        }
        //
        if (!StringUtils.isBlank(config.getSparqlEndpoint())) {
            dataset.property(valueFactory.createURI(MetadataVocabulary.VOID + "sparqlEndpoint"),
                    valueFactory.createURI(config.getSparqlEndpoint()));
        }
        // Lists ...
        for (String author : config.getAuthors()) {
            dataset.property(DCTERMS.CREATOR, valueFactory.createURI(author));
        }
        for (String publisherName : config.getPublishers()) {
            final EntityBuilder publisher = new EntityBuilder(valueFactory.createURI(publisherName),
                    valueFactory);
            publisher.property(DCTERMS.TYPE, FOAF.AGENT);
            rdfData.add(publisher.asStatements());
            // TODO Add more data about publisher?

            dataset.property(DCTERMS.PUBLISHER, publisher);
        }
        for (String licence : config.getLicenses()) {
            dataset.property(DCTERMS.LICENSE, valueFactory.createURI(licence));
            distro.property(DCTERMS.LICENSE, valueFactory.createURI(licence));
        }
        for (String resourceUri : config.getExampleResources()) {
            dataset.property(MetadataVocabulary.VOID_EXAMPLE_RESOURCE, valueFactory.createURI(resourceUri));
        }
        for (String source : config.getSources()) {
            dataset.property(DCTERMS.SOURCE, valueFactory.createURI(source));
        }
        for (String keyword : config.getKeywords()) {
            dataset.property(MetadataVocabulary.DCAT_KEYWORD, valueFactory.createLiteral(keyword));
        }
        for (String language : config.getLanguages()) {
            dataset.property(DCTERMS.LANGUAGE, valueFactory.createURI(language));
        }
        for (String temeUri : config.getThemes()) {
            final EntityBuilder theme = new EntityBuilder(valueFactory.createURI(temeUri),
                    valueFactory);
            theme.property(RDF.TYPE, SKOS.CONCEPT);
            theme.property(SKOS.IN_SCHEME,
                    valueFactory.createURI("http://linked.opendata.cz/resource/catalog/Themes"));
            rdfData.add(theme.asStatements());

            dataset.property(MetadataVocabulary.DCAT_THEME, theme);
        }
        // ...
        if (config.isUseNow()) {
            dataset.property(DCTERMS.MODIFIED, valueFactory.createLiteral(dateFormat.format(new Date()),
                    MetadataVocabulary.XSD_DATE));
            distro.property(DCTERMS.MODIFIED, valueFactory.createLiteral(dateFormat.format(new Date()),
                    MetadataVocabulary.XSD_DATE));
        } else {
            dataset.property(DCTERMS.MODIFIED, valueFactory.createLiteral(dateFormat.format(config
                    .getModified()),
                    MetadataVocabulary.XSD_DATE));
            distro.property(DCTERMS.MODIFIED, valueFactory.createLiteral(dateFormat.format(new Date()),
                    MetadataVocabulary.XSD_DATE));
        }
        //
        dataset.property(MetadataVocabulary.DCAT_DISTRIBUTION, distro);
        // Check for cancel.
        if (ctx.canceled()) {
            return;
        }
        // Compute statistics.
        ContextUtils.sendShortInfo(ctx, "Computing statistic ...");
        dataset.property(MetadataVocabulary.VOID_TRIPLES, valueFactory.createLiteral(
                executeCountQuery("SELECT (COUNT (*) as ?count) WHERE {?s ?p ?o}", "count")));
        if (ctx.canceled()) {
            return;
        }
        dataset.property(MetadataVocabulary.VOID_ENTITIES, valueFactory.createLiteral(
                executeCountQuery("SELECT (COUNT (distinct ?s) as ?count) WHERE {?s a ?t}", "count")));
        if (ctx.canceled()) {
            return;
        }
        dataset.property(MetadataVocabulary.VOID_CLASSES, valueFactory.createLiteral(
                executeCountQuery("SELECT (COUNT (distinct ?t) as ?count) WHERE {?s a ?t}", "count")));
        if (ctx.canceled()) {
            return;
        }
        dataset.property(MetadataVocabulary.VOID_PROPERTIES, valueFactory.createLiteral(
                executeCountQuery("SELECT (COUNT (distinct ?p) as ?count) WHERE {?s ?p ?o}", "count")));
        if (ctx.canceled()) {
            return;
        }
        dataset.property(MetadataVocabulary.VOID_D_SUBJECTS, valueFactory.createLiteral(
                executeCountQuery("SELECT (COUNT (distinct ?s) as ?count) WHERE {?s ?p ?o}", "count")));
        if (ctx.canceled()) {
            return;
        }
        dataset.property(MetadataVocabulary.VOID_D_OBJECTS, valueFactory.createLiteral(
                executeCountQuery("SELECT (COUNT (distinct ?o) as ?count) WHERE {?s ?p ?o}", "count")));
        rdfData.add(dataset.asStatements());
        rdfData.add(distro.asStatements());
        //
        ContextUtils.sendShortInfo(ctx, "Computing statistic ... done");



    }





//
//
//        //Void dataset and DCAT dataset
//        String ns_dcat = "http://www.w3.org/ns/dcat#";
//        String ns_foaf = "http://xmlns.com/foaf/0.1/";
//        String ns_void = "http://rdfs.org/ns/void#";
//        String ns_qb = "http://purl.org/linked-data/cube#";
//
////        final ValueFactory valueFactory;
////        valueFactory = outConnection.getValueFactory();
//        URI foaf_agent = valueFactory.createURI(ns_foaf + "Agent");
//        URI qb_DataSet = valueFactory.createURI(ns_qb + "DataSet");
//        URI dcat_keyword = valueFactory.createURI(ns_dcat + "keyword");
//        URI dcat_distribution = valueFactory.createURI(ns_dcat + "distribution");
//        URI dcat_downloadURL = valueFactory.createURI(ns_dcat + "downloadURL");
//        URI dcat_mediaType = valueFactory.createURI(ns_dcat + "mediaType");
//        URI dcat_theme = valueFactory.createURI(ns_dcat + "theme");
//        URI xsd_date = valueFactory.createURI("http://www.w3.org/2001/XMLSchema#date");
//        URI dcat_distroClass = valueFactory.createURI(ns_dcat + "Distribution");
//        URI dcat_datasetClass = valueFactory.createURI(ns_dcat + "Dataset");
//        URI void_datasetClass = valueFactory.createURI(ns_void + "Dataset");
//        URI void_triples = valueFactory.createURI(ns_void + "triples");
//        URI void_entities = valueFactory.createURI(ns_void + "entities");
//        URI void_classes = valueFactory.createURI(ns_void + "classes");
//        URI void_properties = valueFactory.createURI(ns_void + "properties");
//        URI void_dSubjects = valueFactory.createURI(ns_void + "distinctSubjects");
//        URI void_dObjects = valueFactory.createURI(ns_void + "distinctObjects");
//
//        URI datasetURI = valueFactory.createURI(config.getDatasetURI().toString());
//        URI distroURI = valueFactory.createURI(config.getDistroURI().toString());
//
//        URI exResURI = valueFactory.createURI(ns_void + "exampleResource");

//        outConnection.add(datasetURI, RDF.TYPE, void_datasetClass, outGraphURI);
//        outConnection.add(datasetURI, RDF.TYPE, dcat_datasetClass, outGraphURI);
//        if (config.getComsodeDatasetId() != null && !config.getComsodeDatasetId().isEmpty()) {
//            outConnection.add(datasetURI,
//                    valueFactory.createURI("http://comsode.eu/ontology/dataset"),
//                    valueFactory.createURI("http://comsode.eu/resource/dataset/" + config.getComsodeDatasetId()),
//                    outGraphURI);
//        }
//        if (config.isIsQb()) {
//            outConnection.add(datasetURI, RDF.TYPE, qb_DataSet, outGraphURI);
//        }
//        if (config.getDesc_cs() != null && !config.getDesc_cs().isEmpty()) {
//            outConnection.add(datasetURI, DCTERMS.DESCRIPTION, valueFactory.createLiteral(config.getDesc_cs(), config.getLanguage_cs()), outGraphURI);
//            outConnection.add(distroURI, DCTERMS.DESCRIPTION, valueFactory.createLiteral(config.getDesc_cs(), config.getLanguage_cs()), outGraphURI);
//        }
//        if (config.getDesc_en() != null && !config.getDesc_en().isEmpty()) {
//            outConnection.add(datasetURI, DCTERMS.DESCRIPTION, valueFactory.createLiteral(config.getDesc_en(), "en"), outGraphURI);
//            outConnection.add(distroURI, DCTERMS.DESCRIPTION, valueFactory.createLiteral(config.getDesc_en(), "en"), outGraphURI);
//        }
//        if (config.getTitle_cs() != null && !config.getTitle_cs().isEmpty()) {
//            outConnection.add(datasetURI, DCTERMS.TITLE, valueFactory.createLiteral(config.getTitle_cs(), config.getLanguage_cs()), outGraphURI);
//            outConnection.add(distroURI, DCTERMS.TITLE, valueFactory.createLiteral(config.getTitle_cs(), config.getLanguage_cs()), outGraphURI);
//        }
//        if (config.getTitle_en() != null && !config.getTitle_en().isEmpty()) {
//            outConnection.add(datasetURI, DCTERMS.TITLE, valueFactory.createLiteral(config.getTitle_en(), "en"), outGraphURI);
//            outConnection.add(distroURI, DCTERMS.TITLE, valueFactory.createLiteral(config.getTitle_en(), "en"), outGraphURI);
//        }
//        if (config.getDataDump() != null && !config.getDataDump().isEmpty()) {
//            outConnection.add(datasetURI, valueFactory.createURI(ns_void + "dataDump"), valueFactory.createURI(config.getDataDump().toString()), outGraphURI);
//            outConnection.add(distroURI, dcat_downloadURL, valueFactory.createURI(config.getDataDump().toString()), outGraphURI);
//            outConnection.add(distroURI, dcat_mediaType, valueFactory.createLiteral(config.getMime()), outGraphURI);
//        }
//        if (config.getSparqlEndpoint() != null && !config.getSparqlEndpoint().isEmpty()) {
//            outConnection.add(datasetURI, valueFactory.createURI(ns_void + "sparqlEndpoint"), valueFactory.createURI(config.getSparqlEndpoint().toString()), outGraphURI);
//        }
//        for (String u : config.getAuthors()) {
//            outConnection.add(datasetURI, DCTERMS.CREATOR, valueFactory.createURI(u), outGraphURI);
//        }
//        for (String u : config.getPublishers()) {
//            URI publisherURI = valueFactory.createURI(u.toString());
//            outConnection.add(datasetURI, DCTERMS.PUBLISHER, publisherURI, outGraphURI);
//            outConnection.add(publisherURI, RDF.TYPE, foaf_agent, outGraphURI);
//            //TODO: more publisher data?
//        }
//        for (String u : config.getLicenses()) {
//            outConnection.add(datasetURI, DCTERMS.LICENSE, valueFactory.createURI(u), outGraphURI);
//        }
//        for (String u : config.getExampleResources()) {
//            outConnection.add(datasetURI, exResURI, valueFactory.createURI(u), outGraphURI);
//        }
//        for (String u : config.getSources()) {
//            outConnection.add(datasetURI, DCTERMS.SOURCE, valueFactory.createURI(u), outGraphURI);
//        }
//        for (String u : config.getKeywords()) {
//            outConnection.add(datasetURI, dcat_keyword, valueFactory.createLiteral(u), outGraphURI);
//        }
//        for (String u : config.getLanguages()) {
//            outConnection.add(datasetURI, DCTERMS.LANGUAGE, valueFactory.createURI(u), outGraphURI);
//        }
//        for (String u : config.getThemes()) {
//            URI themeURI = valueFactory.createURI(u.toString());
//            outConnection.add(datasetURI, dcat_theme, themeURI, outGraphURI);
//            outConnection.add(themeURI, RDF.TYPE, SKOS.CONCEPT, outGraphURI);
//            outConnection.add(themeURI, SKOS.IN_SCHEME, valueFactory.createURI("http://linked.opendata.cz/resource/catalog/Themes"), outGraphURI);
//        }
//        if (config.isUseNow()) {
//            outConnection.add(datasetURI, DCTERMS.MODIFIED, valueFactory.createLiteral(dateFormat.format(new Date()), xsd_date), outGraphURI);
//        } else {
//            outConnection.add(datasetURI, DCTERMS.MODIFIED, valueFactory.createLiteral(dateFormat.format(config.getModified()), xsd_date), outGraphURI);
//        }
//        outConnection.add(datasetURI, dcat_distribution, distroURI, outGraphURI);
//        if (context.canceled()) {
//            context.sendMessage(DPUContext.MessageType.INFO, "DPU has been cancelled.");
//            return;
//        }
        // DCAT Distribution
//        outConnection.add(distroURI, RDF.TYPE, dcat_distroClass, outGraphURI);
//
//        for (String u : config.getLicenses()) {
//            outConnection.add(distroURI, DCTERMS.LICENSE, valueFactory.createURI(u), outGraphURI);
//        }
//
//        if (config.isUseNow()) {
//            outConnection.add(distroURI, DCTERMS.MODIFIED, valueFactory.createLiteral(dateFormat.format(new Date()), xsd_date), outGraphURI);
//        } else {
//            outConnection.add(distroURI, DCTERMS.MODIFIED, valueFactory.createLiteral(dateFormat.format(config.getModified()), xsd_date), outGraphURI);
//        }
        // Now compute statistics on input data
//        if (inRdfData != null) {
//            context.sendMessage(DPUContext.MessageType.INFO, "Starting statistics computation");
//
//            final DatasetImpl dataset = new DatasetImpl();
//            for (URI uri : RDFHelper.getGraphsURIArray(inRdfData)) {
//                dataset.addDefaultGraph(uri);
//            }
//
//            executeCountQuery("SELECT (COUNT (*) as ?count) WHERE {?s ?p ?o}", void_triples, datasetURI,
//                    dataset);
//            executeCountQuery("SELECT (COUNT (distinct ?s) as ?count) WHERE {?s a ?t}", void_entities,
//                    datasetURI, dataset);
//            executeCountQuery("SELECT (COUNT (distinct ?t) as ?count) WHERE {?s a ?t}", void_classes,
//                    datasetURI, dataset);
//            executeCountQuery("SELECT (COUNT (distinct ?p) as ?count) WHERE {?s ?p ?o}", void_properties,
//                    datasetURI, dataset);
//            executeCountQuery("SELECT (COUNT (distinct ?s) as ?count) WHERE {?s ?p ?o}", void_dSubjects,
//                    datasetURI, dataset);
//            executeCountQuery("SELECT (COUNT (distinct ?o) as ?count) WHERE {?s ?p ?o}", void_dObjects,
//                    datasetURI, dataset);
//
//            // done computing statistics
//            if (context.canceled()) {
//                context.sendMessage(DPUContext.MessageType.INFO, "DPU has been cancelled.");
//            } else {
//                context.sendMessage(DPUContext.MessageType.INFO, "Statistics computation done");
//            }
//        }
//
//    }

    private Integer executeCountQuery(final String queryAsString, String bindingName) throws DPUException {
        // Prepare SPARQL update query.
        final SparqlUtils.SparqlSelectObject query = faultTolerance.execute(
                new FaultTolerance.ActionReturn<SparqlUtils.SparqlSelectObject>() {

                    @Override
                    public SparqlUtils.SparqlSelectObject action() throws Exception {
                        return SparqlUtils.createSelect(queryAsString,
                                DataUnitUtils.getEntries(inRdfData, RDFDataUnit.Entry.class));
                    }
                });
        final SparqlUtils.QueryResultCollector result = new SparqlUtils.QueryResultCollector();
        faultTolerance.execute(inRdfData, new FaultTolerance.ConnectionAction() {

            @Override
            public void action(RepositoryConnection connection) throws Exception {
                result.prepare();
                SparqlUtils.execute(connection, ctx, query, result);
            }
        });
        if (result.getResults().size() == 1) {
            try {
                return Integer.parseInt(result.getResults().get(0).get(bindingName).stringValue());
            } catch (NumberFormatException ex) {
                throw new DPUException(ex);
            }
        } else {
            throw new DPUException("Unexpected number of results!");
        }
    }

//    void executeCountQuery(String countQuery, URI property, URI datasetURI, Dataset dataset) {
//        if (context.canceled() || inConnection == null) {
//            // end now
//            return;
//        }
//
//        final ValueFactory valueFactory = inConnection.getValueFactory();
//        URI xsd_integer = valueFactory.createURI("http://www.w3.org/2001/XMLSchema#integer");
//        try {
//            TupleQuery query = inConnection.prepareTupleQuery(QueryLanguage.SPARQL, countQuery);
//            query.setDataset(dataset);
//            TupleQueryResult res = query.evaluate();
//
//            int number = Integer.parseInt(res.next().getValue("count").stringValue());
//            outConnection.add(datasetURI, property, valueFactory.createLiteral(Integer.toString(number),
//                    xsd_integer), outGraphURI);
//        } catch (MalformedQueryException ex) {
//            context.sendMessage(DPUContext.MessageType.ERROR, "Wrong query format", "", ex);
//        } catch (NumberFormatException ex) {
//            context.sendMessage(DPUContext.MessageType.ERROR, "Query result is not a number", "", ex);
//        } catch (QueryEvaluationException | RepositoryException ex) {
//            context.sendMessage(DPUContext.MessageType.ERROR, "Query failed", "", ex);
//        }
//
//    }

}
