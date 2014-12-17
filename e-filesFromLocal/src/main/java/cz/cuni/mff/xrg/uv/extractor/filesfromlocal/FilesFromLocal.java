package cz.cuni.mff.xrg.uv.extractor.filesfromlocal;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.resourcehelper.Resource;
import eu.unifiedviews.helpers.dataunit.resourcehelper.ResourceHelpers;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

/**
 * @author Škoda Petr
 */
@DPU.AsExtractor
public class FilesFromLocal extends ConfigurableBase<FilesFromLocalConfig_V1> implements ConfigDialogProvider<FilesFromLocalConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(
            FilesFromLocal.class);

    @DataUnit.AsOutput(name = "output")
    public WritableFilesDataUnit outFilesData;

    public FilesFromLocal() {
        super(FilesFromLocalConfig_V1.class);
    }

    @Override
    public void execute(DPUContext context) throws DPUException, InterruptedException {
        File source = new File(config.getSource());

        if (source.isDirectory()) {
            final Path directoryPath = source.toPath();
            final Iterator<File> iter = FileUtils.iterateFiles(source, null, true);
            while (iter.hasNext()) {
                final File newFile = iter.next();
                final String relativePath = directoryPath.relativize(newFile.toPath()).toString();
                final String newSymbolicName = relativePath;
                try {
                    addOneFile(newSymbolicName, relativePath, newFile);
                } catch (DataUnitException ex) {
                    throw new DPUException("Problem with DataUnit", ex);
                }
            }
        } else if (source.isFile()) {
            try {
                addOneFile(source.getName(), source.getName(), source);
            } catch (DataUnitException ex) {
                throw new DPUException("Problem with DataUnit", ex);
            }
        } else {
            throw new DPUException("Can't determine source type.");
        }
    }

    @Override
    public AbstractConfigDialog<FilesFromLocalConfig_V1> getConfigurationDialog() {
        return new FilesFromLocalVaadinDialog();
    }

    private void addOneFile(String symbolicName, String virtualPath, File file) throws DataUnitException {
        outFilesData.addExistingFile(symbolicName, file.toURI().toASCIIString());
        VirtualPathHelpers.setVirtualPath(outFilesData, symbolicName, virtualPath);
        Resource resource = ResourceHelpers.getResource(outFilesData, symbolicName);
        Date now = new Date();
        resource.setCreated(now);
        resource.setLast_modified(now);
        ResourceHelpers.setResource(outFilesData, symbolicName, resource);
    }
}
