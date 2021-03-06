package eu.unifiedviews.plugins.transformer.relationaltordf.mapper;

import java.util.List;
import java.util.Map;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.extension.rdf.simple.WritableSimpleRdf;
import eu.unifiedviews.plugins.transformer.relationaltordf.TabularOntology;
import eu.unifiedviews.plugins.transformer.relationaltordf.column.ValueGenerator;

/**
 * Parse table data into rdf. Before usage this class must be configured by
 * {@link TableToRdfConfigurator}.
 * 
 * @author Škoda Petr
 */
public class TableToRdf {

    private static final Logger LOG = LoggerFactory.getLogger(TableToRdf.class);

    /**
     * Data output.
     */
    final WritableSimpleRdf outRdf;

    final ValueFactory valueFactory;

    final TableToRdfConfig config;

    ValueGenerator[] infoMap = null;

    ValueGenerator keyColumn = null;

    String baseUri = null;

    Map<String, Integer> nameToIndex = null;

    URI rowClass = null;

    private final URI typeUri;

    URI tableSubject = null;

    boolean tableInfoGenerated = false;

    public TableToRdf(TableToRdfConfig config, WritableSimpleRdf outRdf,
            ValueFactory valueFactory) {
        this.config = config;
        this.outRdf = outRdf;
        this.valueFactory = valueFactory;
        this.typeUri = valueFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    }

    public void paserRow(List<Object> row, int rowNumber) throws DPUException {
        if (row.size() < this.nameToIndex.size()) {
            LOG.warn("Row is smaller ({} instead of {}) - ignore.",
                    row.size(), this.nameToIndex.size());
            return;
        }

        //
        // get subject - key
        //
        final URI subj = prepareUri(row, rowNumber);
        if (subj == null) {
            LOG.error("Row ({}) has null key, row skipped.", rowNumber);
        }
        //
        // parse the line, based on configuration
        //
        for (ValueGenerator item : this.infoMap) {
            final URI predicate = item.getUri();
            final Value value = item.generateValue(row, this.valueFactory);
            if (value == null) {
                if (this.config.ignoreBlankCells) {
                    // ignore
                } else {
                    // insert blank cell URI
                    this.outRdf.add(subj, predicate, TabularOntology.BLANK_CELL);
                }
            } else {
                // insert value
                this.outRdf.add(subj, predicate, value);
            }
        }
        // add row data - number, class, connection to table
        if (this.config.generateRowTriple) {
            this.outRdf.add(subj, TabularOntology.ROW_NUMBER, this.valueFactory.createLiteral(rowNumber));
        }
        if (this.rowClass != null) {
            this.outRdf.add(subj, this.typeUri, this.rowClass);
        }
        if (this.tableSubject != null) {
            this.outRdf.add(this.tableSubject, TabularOntology.TABLE_HAS_ROW, subj);
        }
        // Add table statistict only for the first time.
        if (!this.tableInfoGenerated && this.tableSubject != null) {
            this.tableInfoGenerated = true;
            if (this.config.generateTableClass) {
                this.outRdf.add(this.tableSubject, RDF.TYPE, TabularOntology.TABLE_CLASS);
            }
        }
    }

    /**
     * Set subject that will be used as table subject.
     *
     * @param newTableSubject
     *            Null to turn this functionality off.
     */
    public void setTableSubject(URI newTableSubject) {
        this.tableSubject = newTableSubject;
        this.tableInfoGenerated = false;
    }

    /**
     * Return key for given row.
     *
     * @param row
     * @param rowNumber
     * @return
     */
    protected URI prepareUri(List<Object> row, int rowNumber) {
        if (this.keyColumn == null) {
            return this.valueFactory.createURI(this.baseUri + Integer.toString(rowNumber));
        } else {
            return (URI) this.keyColumn.generateValue(row, this.valueFactory);
        }
    }

}
