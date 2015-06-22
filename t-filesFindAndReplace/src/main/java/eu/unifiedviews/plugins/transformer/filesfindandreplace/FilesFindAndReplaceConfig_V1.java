package eu.unifiedviews.plugins.transformer.filesfindandreplace;

import java.util.HashMap;
import java.util.Map;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.VersionedConfig;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FilesFindAndReplaceConfig_V1 implements VersionedConfig<FilesFindAndReplaceConfig_V2>{

    private Map<String, String> patterns = new HashMap<String, String>();

    private String charset = "utf8";

    private boolean skipOnError = false;

    public boolean isSkipOnError() {
        return skipOnError;
    }

    public void setSkipOnError(boolean skipOnError) {
        this.skipOnError = skipOnError;
    }

    // DPUTemplateConfig must provide public non-parametric constructor
    public FilesFindAndReplaceConfig_V1() {
    }

    public Map<String, String> getPatterns() {
        return patterns;
    }

    public void setPatterns(Map<String, String> patterns) {
        this.patterns = patterns;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }

    /**
     * @return Next version of configuration with "same" settings.
     * @throws DPUConfigException
     */
    @Override public FilesFindAndReplaceConfig_V2 toNextVersion() throws DPUConfigException {
        FilesFindAndReplaceConfig_V2 config = new FilesFindAndReplaceConfig_V2();
        config.setPatterns(patterns);
        config.setSkipOnError(skipOnError);
        config.setEncoding(Encoding.UTF8); // previous version had hardcoded UTF8 encoding
        return config;
    }
}
