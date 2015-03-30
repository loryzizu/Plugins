package eu.unifiedviews.plugins.transformer.filesmerger;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

/**
 * Vaadin configuration dialog for FilesMerger.
 *
 * @author Petr Škoda
 */
public class FilesMergerVaadinDialog extends AbstractDialog<FilesMergerConfig_V1> {

    public FilesMergerVaadinDialog() {
        super(FilesMerger.class);
    }

    @Override
    public void setConfiguration(FilesMergerConfig_V1 c) throws DPUConfigException {

    }

    @Override
    public FilesMergerConfig_V1 getConfiguration() throws DPUConfigException {
        final FilesMergerConfig_V1 c = new FilesMergerConfig_V1();

        return c;
    }

    @Override
    public void buildDialogLayout() {
    }

}
