package eu.unifiedviews.plugins.transformer.filesfindandreplace;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.copyhelper.CopyHelper;
import eu.unifiedviews.helpers.dataunit.copyhelper.CopyHelpers;
import eu.unifiedviews.helpers.dataunit.fileshelper.FilesHelper;
import eu.unifiedviews.helpers.dataunit.resourcehelper.Resource;
import eu.unifiedviews.helpers.dataunit.resourcehelper.ResourceHelpers;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;
import eu.unifiedviews.helpers.dpu.localization.Messages;

@DPU.AsTransformer
public class FilesFindAndReplace extends ConfigurableBase<FilesFindAndReplaceConfig_V1> implements ConfigDialogProvider<FilesFindAndReplaceConfig_V1> {

    public static final String PREDICATE_HAS_DISTRIBUTION = "http://comsode.eu/hasDistribution";

    private static final Logger LOG = LoggerFactory.getLogger(FilesFindAndReplace.class);

    @DataUnit.AsInput(name = "filesInput")
    public FilesDataUnit filesInput;

    @DataUnit.AsOutput(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    public FilesFindAndReplace() {
        super(FilesFindAndReplaceConfig_V1.class);
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, InterruptedException {
        Messages messages = new Messages(dpuContext.getLocale(), this.getClass().getClassLoader());
        String shortMessage = this.getClass().getSimpleName() + " " + messages.getString("status.tlfs.starting");
        String longMessage = String.valueOf(config);
        dpuContext.sendMessage(DPUContext.MessageType.INFO, shortMessage, longMessage);

        VirtualPathHelper inputVirtualPathHelper = VirtualPathHelpers.create(filesInput);
        VirtualPathHelper outputVirtualPathHelper = VirtualPathHelpers.create(filesOutput);
        CopyHelper copyHelper = CopyHelpers.create(filesInput, filesOutput);
        long index = 0L;
        try {
            for (FilesDataUnit.Entry entry : FilesHelper.getFiles(filesInput)) {
                if (dpuContext.canceled()) {
                    break;
                }

                try {
                    Path inputPath = new File(URI.create(entry.getFileURIString())).toPath();
                    String outputRelativePath = inputVirtualPathHelper.getVirtualPath(entry.getSymbolicName());
                    if (outputRelativePath == null || outputRelativePath.isEmpty()) {
                        outputRelativePath = entry.getSymbolicName();
                        outputVirtualPathHelper.setVirtualPath(entry.getSymbolicName(), outputRelativePath);
                    }
                    File outputFile = new File(URI.create(filesOutput.getBaseFileURIString() + "/" + outputRelativePath));
                    new File(FilenameUtils.getFullPath(outputFile.getAbsolutePath())).mkdirs();

                    Path outputPath = outputFile.toPath();

                    Date start = new Date();
                    if (dpuContext.isDebugging()) {
                        LOG.debug("Processing {} file {}", (index), entry);
                    }
                    java.nio.file.Files.copy(inputPath, outputPath);
                    try {
                        java.nio.file.Files.setPosixFilePermissions(outputPath, PosixFilePermissions.fromString("rw-r--r--"));
                    } catch (UnsupportedOperationException ex) {
                    }

                    copyHelper.copyMetadata(entry.getSymbolicName());
                    Resource resource = ResourceHelpers.getResource(filesOutput, entry.getSymbolicName());
                    resource.setLast_modified(new Date());
                    ResourceHelpers.setResource(filesOutput, entry.getSymbolicName(), resource);
                    filesOutput.updateExistingFileURI(entry.getSymbolicName(), outputFile.toURI().toASCIIString());

                    String str = IOUtils.toString(outputFile.toURI(), Charset.forName(config.getCharset()));
                    for (Map.Entry<String, String> pattern : config.getPatterns().entrySet()) {
                        str = str.replace(pattern.getKey(), pattern.getValue());
                        if (dpuContext.canceled()) {
                            break;
                        }
                    }
                    FileOutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(outputFile);
                        IOUtils.write(str, outputStream, Charset.forName(config.getCharset()));
                    } finally {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    }
                    if (dpuContext.isDebugging()) {
                        LOG.debug("Processed {} file in {}s", (index), (System.currentTimeMillis() - start.getTime()) / 1000);
                    }
                } catch (IOException ex) {
                    if (config.isSkipOnError()) {
                        LOG.warn("Error processing {} file {}", (index), String.valueOf(entry), ex);
                    } else {
                        throw new DPUException(messages.getString("error.tlfs.processing") + " " + (index) + " " + messages.getString("error.tlfs.file") + " " + String.valueOf(entry), ex);
                    }
                }
            }
        } catch (DataUnitException ex) {
            throw new DPUException(messages.getString("error.fileinput.iterator"), ex);
        } finally {
            inputVirtualPathHelper.close();
            outputVirtualPathHelper.close();
            copyHelper.close();
        }
    }

    @Override
    public AbstractConfigDialog<FilesFindAndReplaceConfig_V1> getConfigurationDialog() {
        return new FilesFindAndReplaceVaadinDialog();
    }
}
