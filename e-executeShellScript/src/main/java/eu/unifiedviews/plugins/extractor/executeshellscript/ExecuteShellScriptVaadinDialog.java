package eu.unifiedviews.plugins.extractor.executeshellscript;

import java.io.File;
import java.util.Map;

import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

/**
 * Vaadin configuration dialog .
 */
public class ExecuteShellScriptVaadinDialog extends AbstractDialog<ExecuteShellScriptConfig_V1> {

    ListSelect lstScriptName;

    private TextArea txtConfiguration;

    private String pathToShellScripts = null;

    private Label errorLabel;

    public ExecuteShellScriptVaadinDialog() {
        super(ExecuteShellScript.class);
    }

    @Override
    public void setConfiguration(ExecuteShellScriptConfig_V1 c) throws DPUConfigException {
        lstScriptName.setValue(c.getScriptName());
        txtConfiguration.setValue(c.getConfiguration());
    }

    @Override
    public ExecuteShellScriptConfig_V1 getConfiguration() throws DPUConfigException {
        final ExecuteShellScriptConfig_V1 c = new ExecuteShellScriptConfig_V1();

        c.setScriptName((String) lstScriptName.getValue());
        c.setConfiguration(txtConfiguration.getValue());
        return c;
    }

    @Override
    public void buildDialogLayout() {
        Map<String, String> env = this.getContext().getEnvironment();
        pathToShellScripts = env.get(ExecuteShellScript.SHELL_SCRIPT_PATH);
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        lstScriptName = new ListSelect(ctx.tr("ExecuteShellScript.dialog.scriptName"));
        lstScriptName.setRows(5);
        lstScriptName.setNullSelectionAllowed(true);
        lstScriptName.setWidth("100%");
        mainLayout.addComponent(lstScriptName);

        txtConfiguration = new TextArea(ctx.tr("ExecuteShellScript.dialog.configuration"));
        txtConfiguration.setSizeFull();
        mainLayout.addComponent(txtConfiguration);

        errorLabel = new Label();
        errorLabel.setVisible(false);
        errorLabel.setStyleName("dpu-error-label");
        mainLayout.addComponent(errorLabel);

        fillListValues();
        setCompositionRoot(mainLayout);
    }

    private void fillListValues() {
        if (pathToShellScripts == null) {
            if (errorLabel == null) {
                errorLabel = new Label();
            }
            errorLabel.setValue(ctx.tr("errors.pathToScriptsNotSet"));
            errorLabel.setVisible(true);
            return;
        }
        File scriptDirFile = new File(pathToShellScripts);
        if (!scriptDirFile.exists()) {
            errorLabel.setValue(ctx.tr("errors.pathToScriptsDoesntExist"));
            errorLabel.setVisible(true);
            return;
        }
        File[] scriptFiles = scriptDirFile.listFiles();
        if (scriptFiles == null || scriptFiles.length < 1) {
            errorLabel.setValue(ctx.tr("errors.dirWithScriptsIsEmpty"));
            errorLabel.setVisible(true);
            return;
        }

        for (File scriptFile : scriptFiles) {
            lstScriptName.addItem(scriptFile.getName());
        }
    }
}
