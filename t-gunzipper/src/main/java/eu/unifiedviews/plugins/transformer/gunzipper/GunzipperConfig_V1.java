package eu.unifiedviews.plugins.transformer.gunzipper;

/**
 * Configuration class for FilesMerger.
 */
public class GunzipperConfig_V1 {

    private boolean skipOnError = false;

    public GunzipperConfig_V1() {

    }

    public boolean isSkipOnError() {
        return skipOnError;
    }

    public void setSkipOnError(boolean skipOnError) {
        this.skipOnError = skipOnError;
    }

}
