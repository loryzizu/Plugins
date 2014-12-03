package eu.unifiedviews.plugins.loader.filestolocalfs;

import org.openrdf.model.Namespace;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;

public class DCAT {

    /** http://www.w3.org/ns/dcat# */
    public static final String NAMESPACE = "http://www.w3.org/ns/dcat#";

    /**
     * Recommended prefix for the RDF namespace: "rdf"
     */
    public static final String PREFIX = "dcat";

    /**
     * An immutable {@link Namespace} constant that represents the http://www.w3.org/ns/dcat# namespace.
     */
    public static final Namespace NS = new NamespaceImpl(PREFIX, NAMESPACE);

    /** http://www.w3.org/ns/dcat#Distribution */
    public final static URI DISTRIBUTION;

    /** http://www.w3.org/ns/dcat#downloadURL */
    public final static URI DOWNLOAD_URL;

    static {
        ValueFactory factory = ValueFactoryImpl.getInstance();
        DISTRIBUTION = factory.createURI(RDF.NAMESPACE, "Distribution");
        DOWNLOAD_URL = factory.createURI(RDF.NAMESPACE, "downloadURL");
    }
}
