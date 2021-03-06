package eu.unifiedviews.plugins.transformer.filesrenamer;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

public class RenamerVocabulary {

    public static final String TEMP = "http://localhost/ontology/temp/";

    public static final URI TEMP_SYMBOLIC_NAME;

    public static final URI TEMP_VIRTUAL_PATH;

    static {
        final ValueFactory valueFactory = ValueFactoryImpl.getInstance();

        TEMP_SYMBOLIC_NAME = valueFactory.createURI(TEMP + "symbolicName");
        TEMP_VIRTUAL_PATH = valueFactory.createURI(TEMP + "virtualPath");
    }

}
