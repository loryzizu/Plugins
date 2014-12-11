package eu.unifiedviews.plugins.dputemplate;

/**
 * We put DPU configuration here. It has to be POJO with getters and setters.
 *
 * Try to avoid using complex types in fields, use Long, String, java.util.Date
 * where you can.
 *
 * <b>This class must have default (parameter less) constructor!</b>
 */
public class DPUTemplateConfig_V1 {

    /**
     * We should kindly provide reasonable default, just in case, user uses DPU but does not open configuration dialog at all
     */
    private String outputFilename = "output.csv";

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
}
