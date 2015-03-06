package eu.unifiedviews.plugins.transformer.xslt;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.copyhelper.CopyHelpers;
import eu.unifiedviews.helpers.dataunit.fileshelper.FilesHelper;
import eu.unifiedviews.helpers.dataunit.maphelper.MapHelper;
import eu.unifiedviews.helpers.dataunit.maphelper.MapHelpers;
import eu.unifiedviews.helpers.dataunit.resourcehelper.Resource;
import eu.unifiedviews.helpers.dataunit.resourcehelper.ResourceHelpers;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;
import eu.unifiedviews.helpers.dpu.localization.Messages;

@DPU.AsTransformer
public class XSLT extends ConfigurableBase<XSLTConfig_V1> implements ConfigDialogProvider<XSLTConfig_V1> {
    private static final Logger LOG = LoggerFactory.getLogger(XSLT.class);

    @DataUnit.AsInput(name = "filesInput")
    public FilesDataUnit filesInput;

    @DataUnit.AsOutput(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;
    
    private Messages messages;

    public XSLT() {
        super(XSLTConfig_V1.class);
    }

    @Override
    public AbstractConfigDialog<XSLTConfig_V1> getConfigurationDialog() {
        return new XSLTVaadinDialog();
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException {
        this.messages = new Messages(dpuContext.getLocale(), this.getClass().getClassLoader());
        //check that XSLT is available
        if (config.getXslTemplate().isEmpty()) {
            throw new DPUException(this.messages.getString("errors.xslt.template"));
        }

        String shortMessage = this.messages.getString("messages.xslt.start", this.getClass().getSimpleName());
        String longMessage = this.messages.getString("messages.xslt.longstart", XSLTConfig_V1.class.getSimpleName(), 
                this.config.getXslTemplateFileNameShownInDialog(),
                this.config.isSkipOnError(), this.config.getXsltParametersMapName(), this.config.getOutputFileExtension());
        dpuContext.sendMessage(DPUContext.MessageType.INFO, shortMessage, longMessage);

        //try to compile XSLT
        Processor proc = new Processor(false);
        XsltCompiler comp = proc.newXsltCompiler();
        XsltExecutable exp;
        try {
            exp = comp.compile(new StreamSource(new StringReader(config.getXslTemplate())));
        } catch (SaxonApiException ex) {
            throw new DPUException(this.messages.getString("errors.xslt.compile"));
        }

        dpuContext.sendMessage(DPUContext.MessageType.INFO, this.messages.getString("messages.xslt.compile"));

        final Iterator<FilesDataUnit.Entry> filesIteration;
        try {
            filesIteration = FilesHelper.getFiles(filesInput).iterator();
        } catch (DataUnitException ex) {
            dpuContext.sendMessage(DPUContext.MessageType.ERROR, this.messages.getString("errors.dpu.failed"), this.messages.getString("errors.xslt.iterator"), ex);
            return;
        }
        long filesSuccessfulCount = 0L;
        long index = 0L;
        boolean shouldContinue = !dpuContext.canceled();

        MapHelper mapHelper = MapHelpers.create(filesInput);
        VirtualPathHelper inputVirtualPathHelper = VirtualPathHelpers.create(filesInput);
        VirtualPathHelper outputVirtualPathHelper = VirtualPathHelpers.create(filesOutput);
        String xsltParametersMapName = config.getXsltParametersMapName();
        try {
            File baseOutputDirectory = new File(URI.create(filesOutput.getBaseFileURIString()));
            while (shouldContinue && filesIteration.hasNext()) {
                FilesDataUnit.Entry entry;
                entry = filesIteration.next();

                String inSymbolicName = entry.getSymbolicName();

                try {
                    File outputFile = File.createTempFile("t-xslt-dpu", "", baseOutputDirectory);
                    File inputFile = new File(URI.create(entry.getFileURIString()));
                    index++;

                    Date start = new Date();
                    if (dpuContext.isDebugging()) {
                        long inputSizeM = inputFile.length() / 1024 / 1024;
                        LOG.debug("Memory used: {}M", String.valueOf((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024));
                        LOG.debug("Processing {} file {} length {}M", (index), entry, inputSizeM);

                    }
                    Serializer out = new Serializer(outputFile);

//                    DocumentBuilder builder = proc.newDocumentBuilder();
//                    builder.setTreeModel(TreeModel.TINY_TREE_CONDENSED);
//                    XdmNode source = builder.build(new StreamSource(entry.getFilesystemURI().toASCIIString()));
//                    trans.setInitialContextNode(source);
                    XsltTransformer trans = exp.load();
                    LOG.debug("XSLT map name: {} for symbolic name: {}", xsltParametersMapName, inSymbolicName);
                    Map<String, String> xsltParameters = mapHelper.getMap(inSymbolicName, xsltParametersMapName);
                    if (xsltParameters != null) {
                        LOG.debug("XSLT params size: {}", xsltParameters.keySet().size());
                        for (String key : xsltParameters.keySet()) {
                            trans.setParameter(new QName(key), new XdmAtomicValue(xsltParameters.get(key)));
                        }
                    }
                    trans.setSource(new StreamSource(inputFile));
                    trans.setDestination(out);
                    trans.transform();
                    trans.getUnderlyingController().clearDocumentPool();

                    filesSuccessfulCount++;
                    CopyHelpers.copyMetadata(inSymbolicName, filesInput, filesOutput);
                    filesOutput.updateExistingFileURI(inSymbolicName, outputFile.toURI().toASCIIString());
                    String inputVirtualPath = inputVirtualPathHelper.getVirtualPath(inSymbolicName);
                    if (inputVirtualPath != null && config.getOutputFileExtension() != null && !config.getOutputFileExtension().isEmpty()) {
                        outputVirtualPathHelper.setVirtualPath(inSymbolicName, FilenameUtils.removeExtension(inputVirtualPath) + config.getOutputFileExtension());
                    } else if (config.getOutputFileExtension() != null && !config.getOutputFileExtension().isEmpty()) {
                        outputVirtualPathHelper.setVirtualPath(inSymbolicName, inSymbolicName + config.getOutputFileExtension());
                    }
                    Resource resource = ResourceHelpers.getResource(filesOutput, inSymbolicName);
                    Date now = new Date();
                    resource.setLast_modified(now);
                    ResourceHelpers.setResource(filesOutput, inSymbolicName, resource);

                    if (dpuContext.isDebugging()) {
                        LOG.debug("Processed {} file in {}s", (index), (System.currentTimeMillis() - start.getTime()) / 1000);
                        LOG.debug("Memory used: " + String.valueOf((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024) + "M");
                    }
                } catch (SaxonApiException | IOException | DataUnitException ex) {
                    if (config.isSkipOnError()) {
                        LOG.warn("Error processing {} file {}", (index), String.valueOf(entry), ex);
                    } else {
                        throw new DPUException(this.messages.getString("errors.xslt.process", index, String.valueOf(entry)), ex);
                    }
                }
                shouldContinue = !dpuContext.canceled();
            }
        } catch (DataUnitException ex) {
            throw new DPUException(this.messages.getString("errors.xslt.iteration"), ex);
        } finally {
            mapHelper.close();
            inputVirtualPathHelper.close();
            outputVirtualPathHelper.close();
        }
        String message = this.messages.getString("messages.xslt.processed", filesSuccessfulCount, index);
        dpuContext.sendMessage(filesSuccessfulCount < index ? DPUContext.MessageType.WARNING : DPUContext.MessageType.INFO, message);
    }
}
