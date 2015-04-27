package eu.unifiedviews.plugins.loader.filestoparliament;

public class FilesToParliamentConfig_V1 {
    private String bulkUploadEndpointURL = "http://localhost:8080/parliament/bulk/insert";

    private String rdfFileFormat = "Auto";

    public String getBulkUploadEndpointURL() {
        return bulkUploadEndpointURL;
    }

    public void setBulkUploadEndpointURL(String bulkUploadEndpointURL) {
        this.bulkUploadEndpointURL = bulkUploadEndpointURL;
    }

    public String getRdfFileFormat() {
        return rdfFileFormat;
    }

    public void setRdfFileFormat(String rdfFileFormat) {
        this.rdfFileFormat = rdfFileFormat;
    }

}
