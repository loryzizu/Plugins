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
import eu.unifiedviews.helpers.dataunit.copy.CopyHelper;
import eu.unifiedviews.helpers.dataunit.copy.CopyHelpers;
import eu.unifiedviews.helpers.dataunit.files.FilesHelper;
import eu.unifiedviews.helpers.dataunit.resource.Resource;
import eu.unifiedviews.helpers.dataunit.resource.ResourceHelpers;
import eu.unifiedviews.helpers.dataunit.virtualpath.VirtualPathHelper;
import eu.unifiedviews.helpers.dataunit.virtualpath.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.config.migration.ConfigurationUpdate;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;

@DPU.AsTransformer
public class FilesFindAndReplace extends AbstractDpu<FilesFindAndReplaceConfig_V2> {

    public static final String PREDICATE_HAS_DISTRIBUTION = "http://comsode.eu/hasDistribution";

    private static final Logger LOG = LoggerFactory.getLogger(FilesFindAndReplace.class);

    @DataUnit.AsInput(name = "filesInput")
    public FilesDataUnit filesInput;

    @DataUnit.AsOutput(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    @ExtensionInitializer.Init(param = "eu.unifiedviews.plugins.transformer.relational.RelationalConfig__V1")
    public ConfigurationUpdate _ConfigurationUpdate;

    public FilesFindAndReplace() {
        super(FilesFindAndReplaceVaadinDialog.class, ConfigHistory.history(FilesFindAndReplaceConfig_V2.class).alternative(FilesFindAndReplaceConfig_V1.class).addCurrent(FilesFindAndReplaceConfig_V2.class));
    }

    private DPUContext dpuContext;

    @Override
    protected void innerExecute() throws DPUException {
        String shortMessage = this.ctx.tr("status.tlfs.starting", this.getClass().getSimpleName());
        String longMessage = String.valueOf(this.config);
        this.dpuContext = this.ctx.getExecMasterContext().getDpuContext();
        this.dpuContext.sendMessage(DPUContext.MessageType.INFO, shortMessage, longMessage);

        VirtualPathHelper inputVirtualPathHelper = VirtualPathHelpers.create(filesInput);
        VirtualPathHelper outputVirtualPathHelper = VirtualPathHelpers.create(filesOutput);
        final CopyHelper copyHelper = CopyHelpers.create(filesInput, filesOutput);
        long index = 0L;
        try {
            for (final FilesDataUnit.Entry entry : FilesHelper.getFiles(filesInput)) {
                if (this.dpuContext.canceled()) {
                    break;
                }

                try {
                    Path inputPath = new File(URI.create(entry.getFileURIString())).toPath();
                    String outputRelativePath = inputVirtualPathHelper.getVirtualPath(entry.getSymbolicName());
                    if (outputRelativePath == null || outputRelativePath.isEmpty()) {
                        outputRelativePath = entry.getSymbolicName();
                        outputVirtualPathHelper.setVirtualPath(entry.getSymbolicName(), outputRelativePath);
                    }
                    final File outputFile = new File(URI.create(filesOutput.getBaseFileURIString() + "/" + outputRelativePath));
                    new File(FilenameUtils.getFullPath(outputFile.getAbsolutePath())).mkdirs();

                    Path outputPath = outputFile.toPath();

                    Date start = new Date();
                    if (this.dpuContext.isDebugging()) {
                        LOG.debug("Processing {} file {}", (index), entry);
                    }
                    java.nio.file.Files.copy(inputPath, outputPath);
                    try {
                        java.nio.file.Files.setPosixFilePermissions(outputPath, PosixFilePermissions.fromString("rw-r--r--"));
                    } catch (UnsupportedOperationException ex) {
                    }

                    this.faultTolerance.execute(new FaultTolerance.Action() {

                        @SuppressWarnings("unqualified-field-access")
                        @Override
                        public void action() throws Exception {
                            copyHelper.copyMetadata(entry.getSymbolicName());
                            Resource resource = ResourceHelpers.getResource(filesOutput, entry.getSymbolicName());
                            resource.setLast_modified(new Date());
                            ResourceHelpers.setResource(filesOutput, entry.getSymbolicName(), resource);
                            filesOutput.updateExistingFileURI(entry.getSymbolicName(), outputFile.toURI().toASCIIString());
                        }
                    });

                    Charset charset = Charset.forName(config.getEncoding().getCharset());

                    String str = IOUtils.toString(outputFile.toURI(), charset);
                    for (Map.Entry<String, String> pattern : config.getPatterns().entrySet()) {
                        str = str.replace(pattern.getKey(), pattern.getValue());
                        if (this.dpuContext.canceled()) {
                            break;
                        }
                    }
                    FileOutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(outputFile);
                        IOUtils.write(str, outputStream, charset);
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
                        ContextUtils.dpuException(this.ctx, ex, "error.tlfs.processing", index, String.valueOf(entry));
                    }
                }
            }
        } catch (DataUnitException ex) {
            ContextUtils.dpuException(this.ctx, ex, "error.fileinput.iterator");
        } finally {
            inputVirtualPathHelper.close();
            outputVirtualPathHelper.close();
            copyHelper.close();
        }
    }

}
