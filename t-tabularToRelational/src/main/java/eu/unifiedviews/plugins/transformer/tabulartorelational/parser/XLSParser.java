package eu.unifiedviews.plugins.transformer.tabulartorelational.parser;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import eu.unifiedviews.helpers.dpu.exec.UserExecContext;
import eu.unifiedviews.plugins.transformer.tabulartorelational.TabularToRelationalConfig_V1;

import java.io.File;

public class XLSParser extends RelationalParser {

    public XLSParser(UserExecContext ctx, TabularToRelationalConfig_V1 config, WritableRelationalDataUnit outputDataunit) {
        super(ctx, config, outputDataunit);
    }

    @Override public void parseFile(File inputFile) throws DataUnitException {

    }
}
