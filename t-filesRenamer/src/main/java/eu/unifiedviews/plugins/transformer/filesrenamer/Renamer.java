package eu.unifiedviews.plugins.transformer.filesrenamer;

import java.util.Date;
import java.util.Iterator;

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
import eu.unifiedviews.helpers.dataunit.resourcehelper.Resource;
import eu.unifiedviews.helpers.dataunit.resourcehelper.ResourceHelpers;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;
import eu.unifiedviews.helpers.dpu.localization.Messages;

/**
 * Filenames renaming transformation DPU.
 * 
 * @author mva
 */
@DPU.AsTransformer
public class Renamer extends ConfigurableBase<RenameConfig_V2> implements ConfigDialogProvider<RenameConfig_V2> {

    private static final Logger LOG = LoggerFactory.getLogger(Renamer.class);

    @DataUnit.AsInput(name = "input")
    public FilesDataUnit inFilesData;

    @DataUnit.AsOutput(name = "output")
    public WritableFilesDataUnit outFilesData;

    public Renamer() {
        super(RenameConfig_V2.class);
    }

    @Override
    public AbstractConfigDialog<RenameConfig_V2> getConfigurationDialog() {
        return new RenameVaadinDialog();
    }

    @Override
    public void execute(DPUContext context) throws DPUException {
        Messages messages = new Messages(context.getLocale(), this.getClass().getClassLoader());
        final Iterator<FilesDataUnit.Entry> filesIteration;
        try {
            filesIteration = FilesHelper.getFiles(inFilesData).iterator();
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, messages.getString("errors.file.iterator"), "", ex);
            return;
        }

        // create renaming engine, set config and initialize it
        RenamerEngine renamerEngine = new RenamerEngine();
        renamerEngine.setConfig(config);
        renamerEngine.initialize();

        VirtualPathHelper virtualPathHelperInput = VirtualPathHelpers.create(inFilesData);
        VirtualPathHelper virtualPathHelperOutput = VirtualPathHelpers.create(outFilesData);
        try {

            while (!context.canceled() && filesIteration.hasNext()) {
                FilesDataUnit.Entry entry;
                try {
                    entry = filesIteration.next();

                    final String oldVirtualPath = virtualPathHelperInput.getVirtualPath(entry.getSymbolicName());
                    final String newVirtualPath = renamerEngine.renameNext(oldVirtualPath);

                    // rename and copy metadata
                    CopyHelpers.copyMetadata(entry.getSymbolicName(), inFilesData, outFilesData);
                    virtualPathHelperOutput.setVirtualPath(entry.getSymbolicName(), newVirtualPath);
                    Resource resource = ResourceHelpers.getResource(outFilesData, entry.getSymbolicName());
                    // adjust modification date
                    Date now = new Date();
                    resource.setLast_modified(now);
                    ResourceHelpers.setResource(outFilesData, entry.getSymbolicName(), resource);

                } catch (DataUnitException ex) {
                    context.sendMessage(DPUContext.MessageType.ERROR,
                            messages.getString("errors.dpu.generalfailed"), "", ex);
                }
            }
        } finally {
            virtualPathHelperInput.close();
            virtualPathHelperOutput.close();
        }
    }
}
