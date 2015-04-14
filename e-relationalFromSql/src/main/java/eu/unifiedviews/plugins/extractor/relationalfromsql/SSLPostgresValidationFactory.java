package eu.unifiedviews.plugins.extractor.relationalfromsql;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.postgresql.ssl.WrappedFactory;

/**
 * Custom SSL Postgres factory (server SSL certificate validation)
 * Checks certificate of target server based on truststore defined in javax.net.ssl.trustStore system property
 * This factory can be used only if truststore property is set, otherwise creating of the factory instance fails
 */
public class SSLPostgresValidationFactory extends WrappedFactory
{

    private String trustStoreLocation = null;

    private String trustStorePassword = null;

    private String tustStoreProvider = null;

    private String trustStoreType = null;

    private String trustManagerFactoryAlgorithm = null;

    private String sslProtocol = null;

    private String sslCtxProvider = null;

    private KeyStore trustStoreInstance = null;

    private TrustManager[] trustManagers = null;

    private static final String _SYS_PROP_PREFIX = "javax.net.ssl.";

    public SSLPostgresValidationFactory() throws Exception
    {

        this.trustStorePassword = System.getProperty(_SYS_PROP_PREFIX + "trustStorePassword");
        this.tustStoreProvider = System.getProperty(_SYS_PROP_PREFIX + "trustStoreProvider");
        this.trustStoreType = System.getProperty(_SYS_PROP_PREFIX + "trustStoreType");
        this.trustStoreLocation = System.getProperty(_SYS_PROP_PREFIX + "trustStore");
        if (this.trustStoreLocation == null) {
            throw new Exception("No truststore location is defined by system property javax.net.ssl.trustStore");
        }

        if (this.trustStorePassword == null)
        {
            this.trustStorePassword = "changeit";
        }

        if (this.sslProtocol == null)
        {
            this.sslProtocol = "SSLv3";
        }

        if (this.sslCtxProvider == null)
        {
            this.sslCtxProvider = "SunJSSE";
        }

        if (this.trustManagerFactoryAlgorithm == null)
        {
            this.trustManagerFactoryAlgorithm = "PKIX";
        }

        if (this.trustStoreType == null)
        {
            this.trustStoreType = KeyStore.getDefaultType();
        }

        if (this.tustStoreProvider == null)
        {
            this.tustStoreProvider = KeyStore.getInstance(this.trustStoreType).getProvider().getName();
        }

        this.trustStoreInstance = getKeyStore(this.trustStoreType, this.tustStoreProvider, new File(this.trustStoreLocation), this.trustStorePassword);

        TrustManagerFactory trust_manager_factory = TrustManagerFactory.getInstance(this.trustManagerFactoryAlgorithm);
        trust_manager_factory.init(this.trustStoreInstance);
        this.trustManagers = trust_manager_factory.getTrustManagers();

        SSLContext sslContext = SSLContext.getInstance(this.sslProtocol, this.sslCtxProvider);
        sslContext.init(null, this.trustManagers, null);

        this._factory = sslContext.getSocketFactory();
    }

    private static KeyStore getKeyStore(String type, String provider, File file, String password) throws Exception
    {
        KeyStore keyStore = KeyStore.getInstance(type, provider);
        FileInputStream keyStoreInput = new FileInputStream(file);
        keyStore.load(keyStoreInput, password.toCharArray());
        keyStoreInput.close();

        return keyStore;
    }
}
