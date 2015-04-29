package eu.unifiedviews.plugins.loader.filestoparliament;

public class FilesToParliamentConfig_V1 {
    private String bulkUploadEndpointURL = "http://localhost:8080/parliament/bulk/insert";

    private String rdfFileFormat = "Auto";

    private String targetGraphName = "";
    
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

    public String getTargetGraphName() {
        return targetGraphName;
    }

    public void setTargetGraphName(String targetGraphName) {
        this.targetGraphName = targetGraphName;
    }
}
