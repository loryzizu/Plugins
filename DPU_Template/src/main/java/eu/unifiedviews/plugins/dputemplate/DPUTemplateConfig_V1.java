package eu.unifiedviews.plugins.dputemplate;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import eu.unifiedviews.dpu.DPU;

/**
 * We put {@link DPU} configuration here. It has to be POJO with getters and setters.
 *
 * Try to avoid using complex types in fields, use {@link Long}, {@link String}, {@link java.util.Date}
 * where you can.
 *
 * <b>This class must have default (parameter less) constructor!</b>
 */
public class DPUTemplateConfig_V1 {

    /**
     * We should kindly provide reasonable default, just in case, user uses {@link DPU} but does not open configuration dialog at all
     */
    private String outputFilename = "output.csv";

    /**
     * Should we just skip the graph is some error occurs or should {@link DPU} execution fail?
     */
    private Boolean skipGraphOnError = Boolean.FALSE;

    /**
     * Getter for our property
     * @return outputFilename
     */
    public String getOutputFilename() {
        return outputFilename;
    }

    /**
     * Setter for our property
     * @param outputFilename new value
     */
    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    public Boolean getSkipGraphOnError() {
        return skipGraphOnError;
    }

    public void setSkipGraphOnError(Boolean skipGraphOnError) {
        this.skipGraphOnError = skipGraphOnError;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }
}
