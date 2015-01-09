package eu.unifiedviews.plugins.loader.filestolocalfs;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Date;

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
import eu.unifiedviews.helpers.dataunit.copyhelper.CopyHelper;
import eu.unifiedviews.helpers.dataunit.copyhelper.CopyHelpers;
import eu.unifiedviews.helpers.dataunit.resourcehelper.Resource;
import eu.unifiedviews.helpers.dataunit.resourcehelper.ResourceHelpers;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;
import eu.unifiedviews.helpers.dpu.localization.Messages;

@DPU.AsLoader
public class FilesToLocalFS extends
        ConfigurableBase<FilesToLocalFSConfig_V1> implements
        ConfigDialogProvider<FilesToLocalFSConfig_V1> {

    public static final String PREDICATE_HAS_DISTRIBUTION = "http://comsode.eu/hasDistribution";

    private static final Logger LOG = LoggerFactory
            .getLogger(FilesToLocalFS.class);

    @DataUnit.AsInput(name = "filesInput")
    public FilesDataUnit filesInput;

    @DataUnit.AsOutput(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    public FilesToLocalFS() {
        super(FilesToLocalFSConfig_V1.class);
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException,
            InterruptedException {
        Messages messages = new Messages(dpuContext.getLocale());
        String shortMessage = this.getClass().getSimpleName() + " " + messages.getString("status.tlfs.starting");
        String longMessage = String.valueOf(config);
        dpuContext.sendMessage(DPUContext.MessageType.INFO, shortMessage, longMessage);

        FilesDataUnit.Iteration filesIteration;
        try {
            filesIteration = filesInput.getIteration();
        } catch (DataUnitException ex) {
            throw new DPUException(messages.getString("error.tlfs.fileinput"), ex);
        }
        File destinationDirFile = new File(config.getDestination());
        destinationDirFile.mkdirs();
        String destinationAbsolutePath = destinationDirFile.getAbsolutePath();

        boolean moveFiles = config.isMoveFiles();
        ArrayList<CopyOption> copyOptions = new ArrayList<>(1);
        if (config.isReplaceExisting()) {
            copyOptions.add(StandardCopyOption.REPLACE_EXISTING);
        }
        CopyOption[] copyOptionsArray = copyOptions.toArray(new CopyOption[copyOptions.size()]);

        long index = 0L;
        boolean shouldContinue = !dpuContext.canceled();
        VirtualPathHelper inputVirtualPathHelper = VirtualPathHelpers.create(filesInput);
        CopyHelper copyHelper = CopyHelpers.create(filesInput, filesOutput);
        try {
            while (shouldContinue && filesIteration.hasNext()) {
                index++;

                FilesDataUnit.Entry entry;
                entry = filesIteration.next();

                try {
                    Path inputPath = new File(URI.create(entry.getFileURIString())).toPath();
                    String outputRelativePath = inputVirtualPathHelper.getVirtualPath(entry.getSymbolicName());
                    if (outputRelativePath == null || outputRelativePath.isEmpty()) {
                        outputRelativePath = entry.getSymbolicName();
                        VirtualPathHelpers.setVirtualPath(filesOutput, entry.getSymbolicName(), outputRelativePath);
                    }
                    File outputFile = new File(destinationAbsolutePath + File.separator + outputRelativePath);
                    new File(FilenameUtils.getFullPath(outputFile.getAbsolutePath())).mkdirs();

                    Path outputPath = outputFile.toPath();

                    Date start = new Date();
                    if (dpuContext.isDebugging()) {
                        LOG.debug("Processing {} file {}", appendNumber(index), entry);
                    }
                    if (moveFiles) {
                        java.nio.file.Files.move(inputPath, outputPath, copyOptionsArray);
                    } else {
                        java.nio.file.Files.copy(inputPath, outputPath, copyOptionsArray);
                    }
                    java.nio.file.Files.setPosixFilePermissions(outputPath, PosixFilePermissions.fromString("rw-r--r--"));

                    copyHelper.copyMetadata(entry.getSymbolicName());
                    Resource resource = ResourceHelpers.getResource(filesOutput, entry.getSymbolicName());
                    resource.setLast_modified(new Date());
                    ResourceHelpers.setResource(filesOutput, entry.getSymbolicName(), resource);
                    filesOutput.updateExistingFileURI(entry.getSymbolicName(), outputRelativePath);

                    if (dpuContext.isDebugging()) {
                        LOG.debug("Processed {} file in {}s", appendNumber(index), (System.currentTimeMillis() - start.getTime()) / 1000);
                    }
                } catch (IOException ex) {
                    if (config.isSkipOnError()) {
                        LOG.warn("Error processing {} file {}", appendNumber(index), String.valueOf(entry), ex);
                    } else {
                        throw new DPUException(messages.getString("error.tlfs.processing") + " " + appendNumber(index) + " " + messages.getString("error.tlfs.file") + " " + String.valueOf(entry), ex);
                    }
                }

                shouldContinue = !dpuContext.canceled();
            }
        } catch (DataUnitException ex) {
            throw new DPUException(messages.getString("error.fileinput.iterator"), ex);
        } finally {
            try {
                filesIteration.close();
            } catch (DataUnitException ex) {
                LOG.warn("Error closing filesInput", ex);
            }
            inputVirtualPathHelper.close();
            copyHelper.close();
        }
    }

    @Override
    public AbstractConfigDialog<FilesToLocalFSConfig_V1> getConfigurationDialog() {
        return new FilesToLocalFSVaadinDialog();
    }

    public static String appendNumber(long number) {
        String value = String.valueOf(number);
        if (value.length() > 1) {
            // Check for special case: 11 - 13 are all "th".
            // So if the second to last digit is 1, it is "th".
            char secondToLastDigit = value.charAt(value.length() - 2);
            if (secondToLastDigit == '1') {
                return value + "th";
            }
        }
        char lastDigit = value.charAt(value.length() - 1);
        switch (lastDigit) {
            case '1':
                return value + "st";
            case '2':
                return value + "nd";
            case '3':
                return value + "rd";
            default:
                return value + "th";
        }
    }
}
