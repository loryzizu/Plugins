package eu.unifiedviews.plugins.transformer.filesfindandreplace;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.VersionedConfig;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.HashMap;
import java.util.Map;

public class FilesFindAndReplaceConfig_V2 implements VersionedConfig<FilesFindAndReplaceConfig_V2> {

    private Map<String, String> patterns = new HashMap<String, String>();

    private Encoding encoding = Encoding.UTF8;

    private boolean skipOnError = false;

    public boolean isSkipOnError() {
        return skipOnError;
    }

    public void setSkipOnError(boolean skipOnError) {
        this.skipOnError = skipOnError;
    }

    // DPUTemplateConfig must provide public non-parametric constructor
    public FilesFindAndReplaceConfig_V2() {
    }

    public Map<String, String> getPatterns() {
        return patterns;
    }

    public void setPatterns(Map<String, String> patterns) {
        this.patterns = patterns;
    }

    public Encoding getEncoding() {
        return encoding;
    }

    public void setEncoding(Encoding encoding) {
        this.encoding = encoding;
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
        return this;
    }
}
