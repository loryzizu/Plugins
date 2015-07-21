package eu.unifiedviews.plugins.transformer.gzipper;

public class GzipperConfig_V1 {
    private boolean skipOnError = false;

    public GzipperConfig_V1() {

    }

    public boolean isSkipOnError() {
        return skipOnError;
    }

    public void setSkipOnError(boolean skipOnError) {
        this.skipOnError = skipOnError;
    }
}