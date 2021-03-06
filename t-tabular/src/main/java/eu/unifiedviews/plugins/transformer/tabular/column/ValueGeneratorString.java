package eu.unifiedviews.plugins.transformer.tabular.column;

import java.util.List;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;

import cz.cuni.mff.xrg.uv.transformer.tabular.column.ValueGeneratorReplace;

/**
 * Generate values as string with language tag or without it if not specified.
 *
 * @author Škoda Petr
 */
public class ValueGeneratorString extends ValueGeneratorReplace {

    /**
     * Value of language that that will be attached to string.
     */
    private final String language;

    public ValueGeneratorString(URI uri, String template, String language) {
        super(uri, template);
        this.language = language;
    }

    @Override
    public Value generateValue(List<Object> row, ValueFactory valueFactory) {
        final String rawResult = super.process(row);
        if (rawResult == null) {
            return null;
        }

        if (language != null) {
            return valueFactory.createLiteral(rawResult, language);
        } else {
            return valueFactory.createLiteral(rawResult);
        }
    }

}
