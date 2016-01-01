/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.unifiedviews.plugins.transformer.filestordft;

/**
 * An exception which means that RDF format cannot be automatically derived
 * by OpenRDF API (based on the file extension) and, thus, is null;
 * 
 * @author tomasknap
 */
class RDFDataFormatNullException extends Exception {

    public RDFDataFormatNullException(String the_input_file_format_cannot_be_automatic) {
    }

    RDFDataFormatNullException() {
    }

}
