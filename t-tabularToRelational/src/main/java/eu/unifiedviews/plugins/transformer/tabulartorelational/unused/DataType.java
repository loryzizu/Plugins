package eu.unifiedviews.plugins.transformer.tabulartorelational.unused;

import java.util.regex.Pattern;

/**
 * Enum containing supported datatype.
 * When adding new supported type, add its regex into constructor.
 */
public enum DataType {
    INTEGER("^-?[0-9]+$"),
    DECIMAL("(^-?[0-9]+$)|(^-?[0-9]+[.,]{1}[0-9]+$)"),
    VARCHAR("[\\s\\S]*");

    private Pattern pattern;

    DataType(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public Pattern getPattern() {
        return pattern;
    }
}
