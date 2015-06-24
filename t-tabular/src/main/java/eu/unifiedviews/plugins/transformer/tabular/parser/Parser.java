package eu.unifiedviews.plugins.transformer.tabular.parser;

import java.io.File;

import eu.unifiedviews.dpu.DPUException;

/**
 *
 * @author Škoda Petr
 */
public interface Parser {

    /**
     * Parse given file.
     * 
     * @param inFile
     * @throws DPUException
     * @throws ParseFailed
     */
    void parse(File inFile) throws DPUException, ParseFailed;

}
