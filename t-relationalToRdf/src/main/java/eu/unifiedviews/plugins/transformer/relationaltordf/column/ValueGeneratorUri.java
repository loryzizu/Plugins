package eu.unifiedviews.plugins.transformer.relationaltordf.column;

import java.util.List;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;

/**
 * Create URI.
 */
public class ValueGeneratorUri extends ValueGeneratorReplace {

    public ValueGeneratorUri(URI uri, String template) {
        super(uri, template);
    }

    @Override
    public Value generateValue(List<Object> row, ValueFactory valueFactory) {
        final String rawResult = super.process(row);
        if (rawResult == null) {
            return null;
        }

        // the replace thing is done as a part of ValueGeneratorReplace
        return valueFactory.createURI(rawResult);
    }

}
