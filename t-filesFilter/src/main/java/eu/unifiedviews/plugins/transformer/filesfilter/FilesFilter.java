package eu.unifiedviews.plugins.transformer.filesfilter;

import java.util.List;

import org.openrdf.repository.RepositoryConnection;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.DataUnitUtils;
import eu.unifiedviews.helpers.dataunit.metadata.MetadataVocabulary;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.config.migration.ConfigurationUpdate;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
import eu.unifiedviews.helpers.dpu.rdf.sparql.SparqlUtils;

@DPU.AsTransformer
public class FilesFilter extends AbstractDpu<FilesFilterConfig_V1> {

    protected static final String PATH_PLACEHOLDER = "pathBinding";

    private static final String SPARQL_EXACT_MATCH
            = "INSERT { ?sA1 ?pA1 ?oA1 . ?oB1 ?pB2 ?oB2 . ?oC2 ?pC3 ?oC3 } WHERE { "
            + "{ "
            + "?sA1 ?pA1 ?oA1 . "
            + "?sA1 <" + MetadataVocabulary.UV_SYMBOLIC_NAME + "> \"" + PATH_PLACEHOLDER + "\" . "
            + "} "
            + "UNION "
            + "{ "
            + "?oB1 ?pB2 ?oB2 . "
            + "?sB1 ?pB1 ?oB1 . "
            + "?sB1 <" + MetadataVocabulary.UV_SYMBOLIC_NAME + "> \"" + PATH_PLACEHOLDER + "\" . "
            + "FILTER ((isURI(?oB1) || isBlank(?oB1))) "
            + "} "
            + "UNION "
            + "{ "
            + "?oC2 ?pC3 ?oC3 . "
            + "?oC1 ?pC2 ?oC2 . "
            + "?sC1 ?pC1 ?oC1 . "
            + "?sC1 <" + MetadataVocabulary.UV_SYMBOLIC_NAME + "> \"" + PATH_PLACEHOLDER + "\" . "
            + "FILTER ((isURI(?oC1) || isBlank(?oC1)) && (isURI(?oC2) || isBlank(?oC2))) "
            + "} "
            + "}";

    private static final String SPARQL_REG_EXP
            = "INSERT { ?sA1 ?pA1 ?oA1 . ?oB1 ?pB2 ?oB2 . ?oC2 ?pC3 ?oC3 } WHERE { "
            + "{ "
            + "?sA1 ?pA1 ?oA1 . "
            + "?sA1 <" + MetadataVocabulary.UV_SYMBOLIC_NAME + "> ?path . "
            + "FILTER regex(?path, \"" + PATH_PLACEHOLDER + "\", \"i\" ) "
            + "} "
            + "UNION "
            + "{ "
            + "?oB1 ?pB2 ?oB2 . "
            + "?sB1 ?pB1 ?oB1 . "
            + "?sB1 <" + MetadataVocabulary.UV_SYMBOLIC_NAME + "> ?path . "
            + "FILTER (regex(?path, \"" + PATH_PLACEHOLDER + "\", \"i\" ) && "
            + "((isURI(?oB1) || isBlank(?oB1))) ) "
            + "} "
            + "UNION "
            + "{ "
            + "?oC2 ?pC3 ?oC3 . "
            + "?oC1 ?pC2 ?oC2 . "
            + "?sC1 ?pC1 ?oC1 . "
            + "?sC1 <" + MetadataVocabulary.UV_SYMBOLIC_NAME + "> ?path . "
            + "FILTER (regex(?path, \"" + PATH_PLACEHOLDER + "\", \"i\" ) && "
            + "((isURI(?oC1) || isBlank(?oC1)) && (isURI(?oC2) || isBlank(?oC2))) ) "
            + "} "
            + "}";

    @DataUnit.AsInput(name = "input")
    public FilesDataUnit inFilesData;

    @DataUnit.AsOutput(name = "output")
    public WritableFilesDataUnit outFilesData;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    @ExtensionInitializer.Init(param = "eu.unifiedviews.plugins.transformer.filesfilter.FilesFilterConfig__V1")
    public ConfigurationUpdate _ConfigurationUpdate;

    public FilesFilter() {
        super(FilesFilterVaadinDialog.class, ConfigHistory.noHistory(FilesFilterConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {
        // Get input and output
        final List<RDFDataUnit.Entry> source = faultTolerance.execute(new FaultTolerance.ActionReturn<List<RDFDataUnit.Entry>>() {

                    @Override
                    public List<RDFDataUnit.Entry> action() throws Exception {
                        return DataUnitUtils.getMetadataEntries(inFilesData);
                    }
                }, "filesFilter.error.metadata.read");
        final RDFDataUnit.Entry target = faultTolerance.execute(
                new FaultTolerance.ActionReturn<RDFDataUnit.Entry>() {

                    @Override
                    public RDFDataUnit.Entry action() throws Exception {
                        return DataUnitUtils.getWritableMetadataEntry(outFilesData);
                    }
                }, "filesFilter.error.metadata.write");
        // Prepare SPARQL update query.
        final SparqlUtils.SparqlUpdateObject updateQuery = faultTolerance.execute(new FaultTolerance.ActionReturn<SparqlUtils.SparqlUpdateObject>() {

                    @Override
                    public SparqlUtils.SparqlUpdateObject action() throws Exception {
                        String query;
                        if (config.isUseRegExp()) {
                            query = SPARQL_REG_EXP;
                        } else {
                            query = SPARQL_EXACT_MATCH;
                        }
                        // Do name substitution.
                        query = query.replaceAll(PATH_PLACEHOLDER, config.getObject());
                        return SparqlUtils.createInsert(String.format(query, config.getObject()), source, target);
                    }
                }, "filesFilter.error.sparql.preparation");

        // Execute SPARQL ie. copy metadata that match given conditions.
        faultTolerance.execute(outFilesData, new FaultTolerance.ConnectionAction() {

            @Override
            public void action(RepositoryConnection connection) throws Exception {
                SparqlUtils.execute(connection, updateQuery);
            }
        }, "filesFilter.error.sparql.execution");
    }

}
