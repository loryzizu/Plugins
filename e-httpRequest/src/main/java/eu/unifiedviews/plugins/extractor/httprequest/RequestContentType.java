package eu.unifiedviews.plugins.extractor.httprequest;

public enum RequestContentType {

    TEXT_DEFAULT("text"), TEXT("text/plain"), JSON("application/json"), APPLICATION_XML("application/xml"), TEXT_XML("text/xml"), TEXT_HTML("text/html"), SOAP("application/soap+xml");

    private final String description;

    RequestContentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

}
