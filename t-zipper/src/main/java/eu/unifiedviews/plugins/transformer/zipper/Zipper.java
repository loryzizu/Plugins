package eu.unifiedviews.plugins.transformer.zipper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dataunit.files.FilesDataUnitUtils;
import eu.unifiedviews.helpers.dataunit.metadata.MetadataUtils;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.resource.Resource;
import eu.unifiedviews.helpers.dataunit.resource.ResourceHelpers;
import eu.unifiedviews.helpers.dataunit.virtualpath.VirtualPathHelper;
import eu.unifiedviews.helpers.dpu.config.migration.ConfigurationUpdate;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultToleranceUtils;

@DPU.AsTransformer
public class Zipper extends AbstractDpu<ZipperConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(Zipper.class);

    @DataUnit.AsInput(name = "input")
    public FilesDataUnit inFilesData;

    @DataUnit.AsOutput(name = "output")
    public WritableFilesDataUnit outFilesData;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    @ExtensionInitializer.Init(param = "eu.unifiedviews.plugins.transformer.zipper.ZipperConfig__V1")
    public ConfigurationUpdate _ConfigurationUpdate;

    public Zipper() {
        super(ZipperVaadinDialog.class, ConfigHistory.noHistory(ZipperConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {
        final List<FilesDataUnit.Entry> files = FaultToleranceUtils.getEntries(faultTolerance, inFilesData, FilesDataUnit.Entry.class);
        // Prepare zip file.
        final String zipSymbolicName = config.getZipFile();
        final FilesDataUnit.Entry output = faultTolerance.execute(new FaultTolerance.ActionReturn<FilesDataUnit.Entry>() {

            @Override
            public FilesDataUnit.Entry action() throws Exception {
                return FilesDataUnitUtils.createFile(outFilesData, zipSymbolicName);
            }
        });
        
        final Resource resource = new Resource();
        resource.setFormat("application/zip");
        resource.setMimetype("application/zip");
        resource.setCreated(new Date());
        resource.setLast_modified(new Date());
        // TODO Check if this works with fault tolerance wrap.
        faultTolerance.execute(new FaultTolerance.Action() {

            @Override
            public void action() throws Exception {
                ResourceHelpers.setResource(outFilesData, zipSymbolicName, resource);
            }
        });

        // Create zip file - this does not
        final File outputFile = FaultToleranceUtils.asFile(faultTolerance, output);
        zipFiles(outputFile, files);
    }

    /**
     * Pack files in given iterator into zip file and add metadata.
     *
     * @param zipFile
     * @param filesIteration
     */
    private void zipFiles(File zipFile, List<FilesDataUnit.Entry> filesIteration) throws DPUException {
        final byte[] buffer = new byte[8196];
        // Used to publish the error mesage only for the first time.
        boolean firstFailure = true;
        try (FileOutputStream fos = new FileOutputStream(zipFile); ZipOutputStream zos = new ZipOutputStream(fos)) {
            // Itarate over files and zip them.
            int counter = 0;
            for (FilesDataUnit.Entry entry : filesIteration) {
                LOG.info("Processing: {}/{}", counter++, filesIteration.size());
                if (ctx.canceled()) {
                    break;
                }
                if (!addZipEntry(zos, buffer, entry)) {
                    if (firstFailure) {
                        // TODO This needs rework, we fail but not instantly?
                        ContextUtils.sendError(ctx, "zipper.errors.zip.failed", "");
                    }
                    firstFailure = false;
                }
            }
        } catch (IOException ex) {
            throw new DPUException(ex);
        }
    }

    /**
     * Add single file into stream as zip entry.
     *
     * @param zos
     * @param buffer
     * @param entry
     * @return True if file has been added.
     * @throws DataUnitException
     */
    private boolean addZipEntry(ZipOutputStream zos, byte[] buffer, final FilesDataUnit.Entry entry) throws DPUException {
        // Get virtual path.
        final String virtualPath = faultTolerance.execute(new FaultTolerance.ActionReturn<String>() {

            @Override
            public String action() throws Exception {
                return MetadataUtils.getFirst(inFilesData, entry, VirtualPathHelper.PREDICATE_VIRTUAL_PATH);
            }
        });
        if (virtualPath == null) {
            throw ContextUtils.dpuException(ctx, "zipper.error.missing.virtual.path", entry.toString());
        }
        // Add to the zip file.
        final File sourceFile = FaultToleranceUtils.asFile(faultTolerance, entry);
        LOG.info("Adding file '{}' from source '{}'", virtualPath, sourceFile);
        try (FileInputStream in = new FileInputStream(sourceFile)) {
            final ZipEntry ze = new ZipEntry(virtualPath);
            zos.putNextEntry(ze);
            // Copy data into zip file.
            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
        } catch (Exception ex) {
            LOG.error("Failed to add file: {}", entry.toString(), ex);
            return false;
        }
        return true;
    }

}
