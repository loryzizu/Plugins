package eu.unifiedviews.plugins.transformer.relationaltordf;

public class ConversionFailed extends Exception {

    private static final long serialVersionUID = 5144028189430853795L;

    public ConversionFailed(String message) {
        super(message);
    }

    public ConversionFailed(String message, Throwable cause) {
        super(message, cause);
    }

}
