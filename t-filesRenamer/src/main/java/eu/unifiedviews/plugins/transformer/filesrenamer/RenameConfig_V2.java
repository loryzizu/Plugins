package eu.unifiedviews.plugins.transformer.filesrenamer;

public class RenameConfig_V2 {

    private String mask = "[N]"; // default value

    private String extensionMask = "[E]"; // default value

    private Integer counterStart = 0; // default value

    private Integer counterStep = 1; // default value

    private Integer counterDigits = 1; // default value

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getExtensionMask() {
        return extensionMask;
    }

    public void setExtensionMask(String extensionMask) {
        this.extensionMask = extensionMask;
    }

    public Integer getCounterStart() {
        return counterStart;
    }

    public void setCounterStart(Integer counterStart) {
        this.counterStart = counterStart;
    }

    public Integer getCounterStep() {
        return counterStep;
    }

    public void setCounterStep(Integer counterStep) {
        this.counterStep = counterStep;
    }

    public Integer getCounterDigits() {
        return counterDigits;
    }

    public void setCounterDigits(Integer counterDigits) {
        this.counterDigits = counterDigits;
    }

    @Override
    public String toString() {
        return "RenameConfig_V1 [mask=" + mask + ", extensionMask=" + extensionMask + ", counterStart=" + counterStart + ", counterStep=" + counterStep + ", counterDigits=" + counterDigits + "]";
    }
}
