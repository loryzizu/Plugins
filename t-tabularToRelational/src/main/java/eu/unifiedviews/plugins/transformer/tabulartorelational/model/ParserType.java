package eu.unifiedviews.plugins.transformer.tabulartorelational.model;

/**
 * Supported input file types.
 */
public enum ParserType {
    CSV("CSV"),
    DBF("DBF"),
    XLS("XLS/XLSX");

    private final String description;

    ParserType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
