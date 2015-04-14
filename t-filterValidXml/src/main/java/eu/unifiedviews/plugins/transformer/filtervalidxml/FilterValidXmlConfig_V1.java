package eu.unifiedviews.plugins.transformer.filtervalidxml;

public class FilterValidXmlConfig_V1 {

    private String xsdContents;

    private String xsdFileUploadLabel;

    private String xsltContents;

    private String xsltFileUploadLabel;

    private boolean failPipelineOnValidationError;

    public FilterValidXmlConfig_V1() {
    }

    public String getXsdContents() {
        return xsdContents;
    }

    public void setXsdContents(String xsdContents) {
        this.xsdContents = xsdContents;
    }

    public String getXsdFileUploadLabel() {
        return xsdFileUploadLabel;
    }

    public void setXsdFileUploadLabel(String xsdFileUploadLabel) {
        this.xsdFileUploadLabel = xsdFileUploadLabel;
    }

    public String getXsltContents() {
        return xsltContents;
    }

    public void setXsltContents(String xsltContents) {
        this.xsltContents = xsltContents;
    }

    public String getXsltFileUploadLabel() {
        return xsltFileUploadLabel;
    }

    public void setXsltFileUploadLabel(String xsltFileUploadLabel) {
        this.xsltFileUploadLabel = xsltFileUploadLabel;
    }

    public boolean isFailPipelineOnValidationError() {
        return failPipelineOnValidationError;
    }

    public void setFailPipelineOnValidationError(boolean failPipelineOnValidationError) {
        this.failPipelineOnValidationError = failPipelineOnValidationError;
    }
}
