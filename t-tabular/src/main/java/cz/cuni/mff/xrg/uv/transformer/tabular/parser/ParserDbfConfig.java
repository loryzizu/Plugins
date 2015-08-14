package cz.cuni.mff.xrg.uv.transformer.tabular.parser;

/**
 *
 * @author Škoda Petr
 */
public class ParserDbfConfig {

    final String encoding;

    final Integer rowLimit;

    final boolean checkStaticRowCounter;

    final boolean trimStringValues;

    public ParserDbfConfig(String encoding, Integer rowLimit,
            boolean checkStaticRowCounter, boolean trimStringValues) {
        this.encoding = encoding;
        this.rowLimit = rowLimit;
        this.checkStaticRowCounter = checkStaticRowCounter;
        this.trimStringValues = trimStringValues;
    }

}
