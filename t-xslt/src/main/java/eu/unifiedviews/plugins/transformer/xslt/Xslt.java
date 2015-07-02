package eu.unifiedviews.plugins.transformer.xslt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.uv.transformer.xslt.XsltConfig_V2;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.helpers.dataunit.DataUnitUtils;
import eu.unifiedviews.helpers.dataunit.files.FilesDataUnitUtils;
import eu.unifiedviews.helpers.dataunit.metadata.MetadataUtils;
import eu.unifiedviews.helpers.dataunit.virtualpath.VirtualPathHelper;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.config.migration.ConfigurationUpdate;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
import eu.unifiedviews.helpers.dpu.extension.rdf.RdfConfiguration;
import eu.unifiedviews.plugins.transformer.xslt.XsltExecutor.Status;

@DPU.AsTransformer
public class Xslt extends AbstractDpu<XsltConfig_V2> {

    private static final Logger LOG = LoggerFactory.getLogger(Xslt.class);

    @RdfConfiguration.ContainsConfiguration
    @DataUnit.AsInput(name = "config", description = "Optional input for RDF configuration.", optional = true)
    public RDFDataUnit rdfConfig;

    @DataUnit.AsInput(name = "filesInput", description = "Files to process with XSLT processor.")
    public FilesDataUnit filesInput;

    @DataUnit.AsOutput(name = "filesOutput", description = "Processed files. Only file contet has been changed.")
    public WritableFilesDataUnit filesOutput;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    @ExtensionInitializer.Init
    public RdfConfiguration _rdfConfiguration;

    @ExtensionInitializer.Init(param = "eu.unifiedviews.plugins.transformer.xslt.XSLTConfig__V1")
    public ConfigurationUpdate _ConfigurationUpdate;

    public Xslt() {
        super(XsltVaadinDialog.class, 
                ConfigHistory.history(XSLTConfig_V1.class).addCurrent(XsltConfig_V2.class));
    }

    @Override
    protected void innerExecute() throws DPUException {
        // Some check for template.
        if (config.getXsltTemplate() == null || config.getXsltTemplate().isEmpty()) {
            throw ContextUtils.dpuException(ctx, "xslt.dpu.error.noTemplate");
        }
        // Get files to iterate.
        final List<FilesDataUnit.Entry> files
                = faultTolerance.execute(new FaultTolerance.ActionReturn<List<FilesDataUnit.Entry>>() {

                    @Override
                    public List<FilesDataUnit.Entry> action() throws Exception {
                        return DataUnitUtils.getEntries(filesInput, FilesDataUnit.Entry.class);
                    }

                });
        // Prepare queue. We use +1 size for DeadPill.
        final List<XsltExecutor.Task> allTaskList = new ArrayList<>(files.size());
        final BlockingQueue<XsltExecutor.Task> taskQueue = new ArrayBlockingQueue<>(64);
        // Spawn threads, we need to spawn at least one.
        final int toSpawn = config.getNumberOfExtraThreads() + 1;
        final List<XsltExecutor> workers = new ArrayList<>(config.getNumberOfExtraThreads());
        for (int i = 0; i < toSpawn; ++i) {
            final XsltExecutor worker = new XsltExecutor(config, taskQueue, faultTolerance, ctx, files.size());
            worker.start();
            workers.add(worker);
        }
        // Prepare status object osed to monitor execuiton.
        final Status status = new Status();
        // Create tasks and insert them into queue.
        LOG.info("Adding tasks ... size: {}", files.size());
        for (final FilesDataUnit.Entry source : files) {
            final int fileIndex = allTaskList.size();
            // Get all the necesary informations.
            final File sourceFile = faultTolerance.execute(new FaultTolerance.ActionReturn<File>() {
                @Override
                public File action() throws Exception {
                    return FilesDataUnitUtils.asFile(source);
                }
            });
            final String symbolicName = faultTolerance.execute(new FaultTolerance.ActionReturn<String>() {

                @Override
                public String action() throws Exception {
                    return source.getSymbolicName();
                }
            });
            // Prepare output file.
            final File targetFile = faultTolerance.execute(new FaultTolerance.ActionReturn<File>() {

                @Override
                public File action() throws Exception {
                    // Use fileNumber to indentify the output file.
                    return new File(new File(java.net.URI.create(filesOutput.getBaseFileURIString())),
                        Integer.toString(fileIndex));
                }
            });
            final XsltExecutor.Task task = new XsltExecutor.Task(symbolicName, sourceFile, targetFile, 
                    fileIndex, status);
            // Add to lists.
            allTaskList.add(task);
            try {
                LOG.debug("New task: {}", task.getSymbolicName());
                // We have to succes.
                while (!taskQueue.offer(task, 2, TimeUnit.SECONDS) && !ctx.canceled()) {
                    // No-op.
                    if (status.terminateThreads) {
                        // Failure, do not add any other task.
                        break;
                    }
                }
            } catch (InterruptedException ex) {
                // DPU should end.
                LOG.info("InterruptedException", ex);
                break;
            }
            if (ctx.canceled()) {
                break;
            }
        }
        LOG.info("Adding tasks ... done");
        // We do not need files any more.
        files.clear();
        // Add dead pill.
        while(true) {
            try {
                while (!taskQueue.offer(new XsltExecutor.DeadPill(status), 5, TimeUnit.SECONDS) || status.terminateThreads) {
                    // No-op here.
                }
                break;
            } catch (InterruptedException ex) {
                // Ok .. some threads may be hanging there. This is too dangerous, we have to ignore this
                // add wait until we can add a DeadPill.
                ContextUtils.sendShortInfo(ctx, "xslt.dpu.info.interrupted");
            }
        }
        // Wait for everyone.
        LOG.info("Waiting for others ...");
        for (XsltExecutor worker : workers ) {
            try {
                worker.join();
            } catch (InterruptedException ex) {
                // Ok we ends ..
                LOG.warn("DPU interupted during threads joining. Some threads may not end properly!", ex);
            }
        }
        for (XsltExecutor worker : workers ) {
            if (worker.isThreadFail()) {
                // At least one thread fail, so whole DPU fail too. This is serious exception.
                throw ContextUtils.dpuException(ctx, "xslt.dpu.error.executorFailed");
            }
        }

        LOG.info("Adding files to output ...");
        // Add sucessfully transformed files into filesOutput.
        int processed = 0;
        for (final XsltExecutor.Task task : allTaskList) {
            if (ctx.canceled()) {
                break;
            }
            if (!task.isAddToOutput()) {
                // Transformation failed.
                continue;
            }
            ++processed;
            // Add to output.
            faultTolerance.execute(new FaultTolerance.Action() {

                @Override
                public void action() throws Exception {
                    // Add a new file - virtual path will be set later.
                    filesOutput.addExistingFile(task.getSymbolicName(),
                            task.getTargetFile().toURI().toString());
                }

            });
            // Set output virtual path.
            faultTolerance.execute(new FaultTolerance.Action() {

                @Override
                public void action() throws Exception {
                    // Update virtual path.
                    String virtualPath = MetadataUtils.getFirst(filesInput, task.getSymbolicName(),
                            VirtualPathHelper.PREDICATE_VIRTUAL_PATH);
                    if (virtualPath == null) {
                        // Use symbolic name
                        throw ContextUtils.dpuException(ctx, "xslt.dpu.error.virtualPathNotSet",
                                task.getSymbolicName());
                    }
                    // Update VirtualPath.
                    if (config.getOutputFileExtension() != null && !config.getOutputFileExtension().isEmpty()) {
                        final int subPathLen = virtualPath.lastIndexOf(".");
                        if (subPathLen == -1)  {
                            // No suffix, just add it.
                            virtualPath += config.getOutputFileExtension();
                        } else {
                            virtualPath = virtualPath.substring(0, subPathLen)
                                + config.getOutputFileExtension();
                        }
                    }
                    // Save metadata.
                    MetadataUtils.set(filesOutput, task.getSymbolicName(),
                            VirtualPathHelper.PREDICATE_VIRTUAL_PATH, virtualPath);
                }
            });
        }

        // Print final messages.
        if (processed == allTaskList.size() && !status.terminateThreads) {
            ContextUtils.sendShortInfo(ctx, "xslt.dpu.info.processed", processed, allTaskList.size());
        } else {
            ContextUtils.sendShortWarn(ctx, "xslt.dpu.info.processed", processed, allTaskList.size());
            // Sotmehing failed.
            if (config.isFailOnError()) {
                throw ContextUtils.dpuException(ctx, "xslt.dpu.error.notAllTransformer");
            }
        }
    }
}
