package eu.unifiedviews.plugins.transformer.filesrenamer;

public class RenamerConfig_V1 {

    private String pattern = "xml";
    
    private String replaceText = "ttl";

    private boolean useAdvanceReplace = false;

    public RenamerConfig_V1() {
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getReplaceText() {
        return replaceText;
    }

    public void setReplaceText(String replaceText) {
        this.replaceText = replaceText;
    }

    public boolean isUseAdvanceReplace() {
        return useAdvanceReplace;
    }

    public void setUseAdvanceReplace(boolean useAdvanceReplace) {
        this.useAdvanceReplace = useAdvanceReplace;
    }
    
}
