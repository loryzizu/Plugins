package eu.unifiedviews.plugins.transformer.filtervalidxml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.helpers.dataunit.files.FilesDataUnitUtils;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.config.migration.ConfigurationUpdate;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultToleranceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;

@DPU.AsTransformer
public class FilterValidXml extends AbstractDpu<FilterValidXmlConfig_V1> {

    private static final Logger log = LoggerFactory.getLogger(FilterValidXml.class);

    @DataUnit.AsInput(name = "input")
    public FilesDataUnit input;

    @DataUnit.AsOutput(name = "outputValid")
    public WritableFilesDataUnit outputValid;

    @DataUnit.AsOutput(name = "outputInvalid")
    public WritableFilesDataUnit outputInvalid;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    @ExtensionInitializer.Init(param = "eu.unifiedviews.plugins.transformer.filtervalidxml.XmlValidatorConfig_V1")
    public ConfigurationUpdate _ConfigurationUpdate;

    public static final String NOT_WELL_FORMED = "errors.validity.wellformed";

    public static final String NOT_VALID_AGAINST_XSD = "errors.validity.xsd";

    public static final String NOT_VALID_AGAINST_XSLT = "errors.validity.xslt";

    public FilterValidXml() {
        super(FilterValidXmlVaadinDialog.class, ConfigHistory.noHistory(FilterValidXmlConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {
        final List<FilesDataUnit.Entry> files = FaultToleranceUtils.getEntries(faultTolerance, input, FilesDataUnit.Entry.class);

        for(final FilesDataUnit.Entry entry: files) {
            if (ctx.canceled()) {
                break;
            }
            // open input file
            final File inputFile;
            final String inputSymbolicName;
            try {
                inputFile = FilesDataUnitUtils.asFile(entry);
                inputSymbolicName = entry.getSymbolicName();
            } catch (DataUnitException e) {
                log.error("Cannot read file from input", e);
                throw ContextUtils.dpuException(ctx, "errors.file.input", e);
            }

            // do the actual validations
            try {
                validateWellFormed(inputFile);
                validateWithXsd(inputFile);
                validateWithXslt(inputFile);
            } catch (DPUException e) {
                if(config.isFailPipelineOnValidationError()) {
                    throw ContextUtils.dpuException(ctx, e, "errors.validity.false", inputSymbolicName);
                }
                ContextUtils.sendMessage(ctx, DPUContext.MessageType.WARNING, "errors.file.not.valid", e, "errors.validity.false",inputSymbolicName);
                // XML is not valid, copy to invalid output
                faultTolerance.execute(new FaultTolerance.Action() {
                    @Override
                    public void action() throws Exception {
                        FilesDataUnitUtils.addFile(outputInvalid, inputFile, entry.getSymbolicName());
                    }
                }, "errors.file.output");
                continue;
            }

            // XML is valid, copy inputs to valid outputs
            // Add file.
            faultTolerance.execute(new FaultTolerance.Action() {
                @Override
                public void action() throws Exception {
                    FilesDataUnitUtils.addFile(outputValid, inputFile, entry.getSymbolicName());
                }
            }, "errors.file.output");
        }
    }

    private void validateWellFormed(final File file) throws DPUException {
        final StringBuilder errorMessage = new StringBuilder();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);

        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new DPUException(ctx.tr("errors.wellformed.parser"), e);
        }

        builder.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(final SAXParseException exception) throws SAXException {
                // pass
            }
            @Override
            public void fatalError(final SAXParseException exception) throws SAXException {
                errorMessage.append(ctx.tr("errors.wellformed.sax", exception.getLineNumber(), exception.getColumnNumber(), exception.getMessage()));
            }
            @Override
            public void error(final SAXParseException exception) throws SAXException {
                errorMessage.append(ctx.tr("errors.wellformed.sax", exception.getLineNumber(), exception.getColumnNumber(), exception.getMessage()));
            }
        });

        try {
            builder.parse(file);
        } catch (SAXException | IOException e) {
            log.error("Cannot parse file, XML is not well-formed", e);
            throw new DPUException(ctx.tr(NOT_WELL_FORMED) + errorMessage.toString(), e);
        }
    }

    private void validateWithXsd(final File file) throws DPUException {
        if (config.getXsdContents() == null || config.getXsdContents().isEmpty()) {
            return;
        }

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        try {
            schema = schemaFactory.newSchema(new StreamSource(new StringReader(config.getXsdContents())));
        } catch (SAXException e) {
            log.error("Cannot parse XSD schema", e);
            String reason = e.getLocalizedMessage();
            throw new DPUException(ctx.tr(NOT_VALID_AGAINST_XSD) + ctx.tr("errors.xsd.schema") + ": " + reason, e);
        }

        Validator validator = schema.newValidator();

        try {
            validator.validate(new StreamSource(file));
        } catch (SAXException | IOException e) {
            log.error("XML is not valid against XSD schema", e);
            throw new DPUException(ctx.tr(NOT_VALID_AGAINST_XSD) + e.getMessage(), e);
        }
    }

    private void validateWithXslt(final File file) throws DPUException {
        if (config.getXsltContents() == null || config.getXsltContents().isEmpty()) {
            return;
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer(new StreamSource(new StringReader(config.getXsltContents())));
        } catch (TransformerConfigurationException e) {
            log.error("Cannot parse XSLT template", e);
            String reason = e.getCause().getLocalizedMessage();
            throw new DPUException(ctx.tr("errors.xslt.template") + ": " + reason, e);
        }

        StreamSource xmlSource = null;
        try {
            xmlSource = new StreamSource(new FileReader(file));
        } catch (FileNotFoundException e) {
            log.error("Cannot read input file", e);
            throw new DPUException(ctx.tr("errors.xslt.input"), e);
        }

        StringWriter writer = new StringWriter();
        StreamResult streamResult = new StreamResult(writer);

        try {
            transformer.transform(xmlSource, streamResult);
        } catch (TransformerException e) {
            log.error("Cannot transform XML with XSLT", e);
            throw new DPUException(ctx.tr("errors.xslt.transformation"), e);
        }

        String transformationResult = writer.getBuffer().toString();
        if (!isTranformationResultEmpty(transformationResult)) {
            log.error("Result of XSLT transformation: {}", transformationResult);
            throw new DPUException(ctx.tr(NOT_VALID_AGAINST_XSLT) + transformationResult);
        }
    }

    private boolean isTranformationResultEmpty(final String result) {
        if (result == null) {
            return true;
        }

        return result.trim().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "").isEmpty();
    }

}
