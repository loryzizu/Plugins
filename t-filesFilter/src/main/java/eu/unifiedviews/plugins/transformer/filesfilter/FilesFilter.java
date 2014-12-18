package eu.unifiedviews.plugins.transformer.filesfilter;

import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

@DPU.AsTransformer
public class FilesFilter extends ConfigurableBase<FilesFilterConfig_V1> implements ConfigDialogProvider<FilesFilterConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(FilesFilter.class);

    @DataUnit.AsInput(name = "input")
    public FilesDataUnit inFilesData;

    @DataUnit.AsOutput(name = "output")
    public WritableFilesDataUnit outFilesData;

    public FilesFilter() {
        super(FilesFilterConfig_V1.class);
    }

    @Override
    public AbstractConfigDialog<FilesFilterConfig_V1> getConfigurationDialog() {
        return new FilesFilterVaadinDialog();
    }

    @Override
    public void execute(DPUContext context) throws DPUException {
        Pattern pattern = null;
        if (config.isUseRegExp()) {
            try {
                pattern = Pattern.compile(config.getObject());
            } catch (PatternSyntaxException ex) {
                context.sendMessage(DPUContext.MessageType.ERROR, "Configuration problem", "Error in object regexp.", ex);
                return;
            }
        }
        //
        // get file iterator
        //
        final Iterator<FilesDataUnit.Entry> filesIteration;
        try {
            filesIteration = FilesHelper.getFiles(inFilesData).iterator();
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, "DPU Failed", "Can't get file iterator.", ex);
            return;
        }
        //
        // filter data here
        //
        boolean useSymbolicName = config.getPredicate().equals(FilesFilterConfig_V1.SYMBOLIC_NAME);

        try {
            while (filesIteration.hasNext()) {
                final FilesDataUnit.Entry entry = filesIteration.next();

                final String value;
                if (useSymbolicName) {
                    value = entry.getSymbolicName();
                } else {
                    // virtual path
                    value = VirtualPathHelpers.getVirtualPath(inFilesData, entry.getSymbolicName());
                }

                if (value == null) {
                    // no value for predicate - continue
                    LOG.debug("Entry '{}' has no value", entry.getSymbolicName());
                    continue;
                }

                if (pattern == null) {
                    // match as string
                    if (value.compareTo(config.getObject()) != 0) {
                        LOG.debug("Entry '{}' with value '{}' doesn't match given value", entry.getSymbolicName(), value);
                        continue;
                    }
                } else {
                    // use reg exp
                    if (!pattern.matcher(value).matches()) {
                        LOG.debug("Entry '{}' with value '{}' doesn't match regExp", entry.getSymbolicName(), value);
                        continue;
                    }
                }
                LOG.debug("Entry '{}' pass the filter.", entry.getSymbolicName());
                // if we are here, then file pass through our filters
                CopyHelpers.copyMetadata(entry.getSymbolicName(), inFilesData, outFilesData);
            }
        } catch (DataUnitException ex) {
            context.sendMessage(DPUContext.MessageType.ERROR, "Problem with DataUnit", "", ex);
        }
    }

}
