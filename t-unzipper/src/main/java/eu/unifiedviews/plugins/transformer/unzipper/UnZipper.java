package eu.unifiedviews.plugins.transformer.unzipper;

import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.helpers.dataunit.files.FilesDataUnitUtils;
import eu.unifiedviews.helpers.dataunit.metadata.MetadataUtils;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.virtualpath.VirtualPathHelper;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.config.migration.ConfigurationUpdate;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultToleranceUtils;

@DPU.AsTransformer
public class UnZipper extends AbstractDpu<UnZipperConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(UnZipper.class);

    @DataUnit.AsInput(name = "input")
    public FilesDataUnit inFilesData;

    @DataUnit.AsOutput(name = "output")
    public WritableFilesDataUnit outFilesData;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    @ExtensionInitializer.Init(param = "eu.unifiedviews.plugins.transformer.unzipper.UnZipperConfig__V1")
    public ConfigurationUpdate _ConfigurationUpdate;

    public UnZipper() {
        super(UnZipperVaadinDialog.class, ConfigHistory.noHistory(UnZipperConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {
        // Older version of unzipper does not have configuration at all.
        if (config == null) {
            config = new UnZipperConfig_V1();
        }
        // Prepare root output directory.
        final File baseTargetDirectory = faultTolerance.execute(new FaultTolerance.ActionReturn<File>() {

            @Override
            public File action() throws Exception {
                return new File(java.net.URI.create(outFilesData.getBaseFileURIString()));
            }
        }, "unzipper.errors.file.outputdir");
        // Get list of files to unzip.
        final List<FilesDataUnit.Entry> files = FaultToleranceUtils.getEntries(faultTolerance, inFilesData,
            FilesDataUnit.Entry.class);

        LOG.info(">> {}", files.size());

        int counter = 0;
        for (final FilesDataUnit.Entry fileEntry : files) {
            LOG.info("Processing: {}/{}", counter++, files.size());
            if (ctx.canceled()) {
                return;
            }
            final File sourceFile = FaultToleranceUtils.asFile(faultTolerance, fileEntry);
            // Get virtual path.
            final String zipRelativePath = faultTolerance.execute(new FaultTolerance.ActionReturn<String>() {

                @Override
                public String action() throws Exception {
                    return MetadataUtils.getFirst(inFilesData, fileEntry, VirtualPathHelper.PREDICATE_VIRTUAL_PATH);
                }
            }, "unzipper.error.virtualpath.get.failed");
            if (zipRelativePath == null) {
                throw ContextUtils.dpuException(ctx, "unzipper.error.missing.virtual.path", fileEntry.toString());
            }
            // Unzip.
            final File targetDirectory = new File(baseTargetDirectory, zipRelativePath);
            unzip(sourceFile, targetDirectory);
            // Scan for new files.
            scanDirectory(targetDirectory, zipRelativePath);
        }
    }

    /**
     * Scan given directory for files and add then to {@link #outFilesData}.
     *
     * @param directory
     * @throws DPUException
     */
    private void scanDirectory(File directory, String pathPrefix) throws DPUException {
        final Path directoryPath = directory.toPath();
        final Iterator<File> iter = FileUtils.iterateFiles(directory, null, true);
        while (iter.hasNext()) {
            final File newFile = iter.next();
            final String relativePath = directoryPath.relativize(newFile.toPath()).toString();
            final String newFileRelativePath;
            if (config.isNotPrefixed()) {
                newFileRelativePath = relativePath;
            } else {
                newFileRelativePath = pathPrefix + "/" + relativePath;
            }
            // Add file.
            faultTolerance.execute(new FaultTolerance.Action() {

                @Override
                public void action() throws Exception {
                    FilesDataUnitUtils.addFile(outFilesData, newFile, newFileRelativePath);
                }
            }, "unzipper.error.file.add");
        }
    }

    /**
     * Extract given zip file into given directory.
     *
     * @param zipFile
     * @param targetDirectory
     * @throws DPUException
     */
    private void unzip(File zipFile, File targetDirectory) throws DPUException{
        try {
            final ZipFile zip = new ZipFile(zipFile);
            if (zip.isEncrypted()) {
                throw ContextUtils.dpuException(ctx, "unzipper.errors.file.encrypted");
            }
            zip.extractAll(targetDirectory.toString());
        } catch (ZipException ex) {
            throw ContextUtils.dpuException(ctx, ex, "unzipper.errors.dpu.extraction.failed");
        }
    }

}
