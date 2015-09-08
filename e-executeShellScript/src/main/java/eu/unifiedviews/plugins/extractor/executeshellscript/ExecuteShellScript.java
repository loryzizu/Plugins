package eu.unifiedviews.plugins.extractor.executeshellscript;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.PumpStreamHandler;
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
import eu.unifiedviews.helpers.dataunit.files.FilesDataUnitUtils;
import eu.unifiedviews.helpers.dataunit.resource.Resource;
import eu.unifiedviews.helpers.dataunit.resource.ResourceHelpers;
import eu.unifiedviews.helpers.dataunit.virtualpath.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;

/**
 * Main data processing unit class.
 */
@DPU.AsExtractor
public class ExecuteShellScript extends AbstractDpu<ExecuteShellScriptConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(ExecuteShellScript.class);

    public static final String SHELL_SCRIPT_PATH = "shell.scripts.path";

    @DataUnit.AsInput(name = "filesInput", optional = true)
    public FilesDataUnit filesInput;

    @DataUnit.AsOutput(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    public ExecuteShellScript() {
        super(ExecuteShellScriptVaadinDialog.class, ConfigHistory.noHistory(ExecuteShellScriptConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {
        try {
            String script = getScriptWithPath();
            File inpFilesList = null;
            if (filesInput != null) {
                inpFilesList = prepareInputFiles();
            }
            LOG.debug(String.format("Script name : %s", script));
            LOG.debug(String.format("Configuration : %s", config.getConfiguration()));
            String confFilePath = writeConfiguration().getAbsolutePath();

            File outputDir = createTempOutputDir();
            CommandLine cmdLine = new CommandLine(script);

            cmdLine.addArgument(confFilePath);
            if (inpFilesList != null) {
                cmdLine.addArgument(inpFilesList.getAbsolutePath());
            }
            cmdLine.addArgument(outputDir.getAbsolutePath());

            String commandLine = cmdLine.toString().substring(1, cmdLine.toString().length() - 1);
            commandLine = commandLine.replace(",", "");
            LOG.debug(String.format("Executing script: %s", commandLine));

            DefaultExecutor executor = new DefaultExecutor();
            ExecuteStreamHandler handler = executor.getStreamHandler();
            ByteArrayOutputStream stdout = new ByteArrayOutputStream();
            PumpStreamHandler psh = new PumpStreamHandler(stdout);
            executor.setStreamHandler(psh);
            try {
                handler.setProcessOutputStream(System.in);
            } catch (IOException e) {
                LOG.debug("Unable to change stdout for command '" + cmdLine + "'", e);
            }
            executor.setWorkingDirectory(new File(URI.create(filesOutput.getBaseFileURIString())));
            int result = -1;
            try {
                result = executor.execute(cmdLine);
                LOG.debug("Script stdout/stderr:" + "\n" + stdout.toString());
                LOG.debug(String.format("Script exit value: %d", result));
            } catch (Exception ex) {
                throw ContextUtils.dpuException(ctx, ex, "ExecuteShellScript.execute.exception");
            } finally {
                IOUtils.closeQuietly(stdout);
            }
            if (result == 0) {
                for (File fileEntry : outputDir.listFiles()) {
                    filesOutput.addExistingFile(fileEntry.getName(), fileEntry.toURI().toASCIIString());
                    VirtualPathHelpers.setVirtualPath(filesOutput, fileEntry.getName(), fileEntry.getName());
                    Resource resource = ResourceHelpers.getResource(filesOutput, fileEntry.getName());
                    Date now = new Date();
                    resource.setCreated(now);
                    resource.setLast_modified(now);
                    resource.setSize(fileEntry.length());

                    ResourceHelpers.setResource(filesOutput, fileEntry.getName(), resource);

                }
            } else {
                LOG.error("Script execution error.");
            }
        } catch (DataUnitException | IOException ex) {
            throw ContextUtils.dpuException(ctx, ex, "ExecuteShellScript.execute.exception");
        }

    }

    private File writeConfiguration() throws IOException, DataUnitException {
        File outputFile = File.createTempFile("script_config", ".conf", new File(URI.create(filesOutput.getBaseFileURIString())));
        FileWriter writer = new FileWriter(outputFile, true);
        writer.write(config.getConfiguration());
        writer.close();
        return outputFile;
    }

    private File createTempOutputDir() throws DataUnitException, DPUException {
        File parentDir = new File(URI.create(filesOutput.getBaseFileURIString()));
        File outputDir = null;
        if (parentDir.getAbsolutePath().endsWith(File.separator)) {
            outputDir = new File(parentDir, "tmp");
        } else {
            outputDir = new File(parentDir, File.separator + "tmp");
        }

        if (!outputDir.mkdirs()) {
            throw ContextUtils.dpuException(ctx, "errors.creatingTempOutputDir");
        }
        return outputDir;
    }

    private File prepareInputFiles() {
        File filesToProcessList = null;
        FileWriter writer = null;
        try {
            filesToProcessList = new File(new File(URI.create(filesOutput.getBaseFileURIString())), "filesList.txt");
            writer = new FileWriter(filesToProcessList, true);
            FilesDataUnit.Iteration iteration = filesInput.getIteration();
            while (iteration.hasNext()) {
                File inpF = FilesDataUnitUtils.asFile(iteration.next());
                writer.write(inpF.getAbsolutePath() + "\n");
            }
        } catch (DataUnitException | IOException ex) {
            LOG.error("Error preparing input files.", ex);
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return filesToProcessList;
    }

    private String getScriptWithPath() throws DPUException {
        DPUContext dpuContext = ctx.getExecMasterContext().getDpuContext();
        Map<String, String> environment = dpuContext.getEnvironment();

        String pathToScript = environment.get(SHELL_SCRIPT_PATH);
        if (pathToScript == null || pathToScript.trim().equals("")) {
            LOG.error("Directory with shell scripts is not set in beckend configuration file!");
            throw ContextUtils.dpuException(ctx, "errors.pathToScriptsNotSet.backend");
        }
        File scriptDirFile = new File(pathToScript);
        if (!scriptDirFile.exists()) {
            LOG.error(String.format("Directory with shell scripts %s doesn't exist!", pathToScript));
            throw ContextUtils.dpuException(ctx, "errors.pathToScriptsDoesntExist.backend");
        }
        File scriptFile = new File(pathToScript, config.getScriptName());
        if (scriptFile == null || !scriptFile.exists()) {
            LOG.error(String.format("Script %s doesn't exist!", config.getScriptName()));
            throw ContextUtils.dpuException(ctx, "errors.scriptDoesntExist");
        }
        return scriptFile.getAbsolutePath();
    }
}
