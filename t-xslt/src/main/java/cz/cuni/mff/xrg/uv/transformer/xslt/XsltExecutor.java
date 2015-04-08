package cz.cuni.mff.xrg.uv.transformer.xslt;

import java.io.File;
import java.io.StringReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.UserExecContext;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

/**
 * Transform given file with given template.
 *
 * @author Škoda Petr
 */
public class XsltExecutor extends Thread {

    public static class Status {
        
        /**
         * If set to true then all threads will terminate as soon as possible. Used to indicate failure.
         */
        public boolean terminateThreads = false;        
        
    }

    /**
     * Task definition.
     */
    public static class Task {

        private final String symbolicName;

        private final File sourceFile;

        private final File targetFile;

        private boolean addToOutput = false;

        private final int order;

        private final Status status;

        public Task(Status status) {
            this.symbolicName = null;
            this.sourceFile = null;
            this.targetFile = null;
            this.order = -1;
            this.status = status;
        }

        public Task(String symbolicName, File sourceFile, File targetFile, int order, Status status) {
            this.symbolicName = symbolicName;
            this.sourceFile = sourceFile;
            this.targetFile = targetFile;
            this.order = order;
            this.status = status;
        }

        public String getSymbolicName() {
            return symbolicName;
        }

        public File getSourceFile() {
            return sourceFile;
        }

        public File getTargetFile() {
            return targetFile;
        }

        public boolean isAddToOutput() {
            return addToOutput;
        }

    }

    /**
     * Used to kill executor thread.
     */
    public static class DeadPill extends Task {

        public DeadPill(Status status) {
            super(status);
        }

    }

    private static final Logger LOG = LoggerFactory.getLogger(XsltExecutor.class);

    private final Processor proc;

    private final XsltCompiler compiler;

    private final XsltExecutable executable;

    private final XsltConfig_V2 config;

    private final BlockingQueue<Task> taskQueue;

    private final UserExecContext ctx;

    private final Integer totalFileCounter;

    /**
     * True if execution in thread fail.
     */
    private boolean threadFail = true;

    public XsltExecutor(XsltConfig_V2 config,
            BlockingQueue<Task> taskQueue, FaultTolerance faultTolerance, UserExecContext ctx,
            Integer totalFileCounter)
            throws DPUException {
        LOG.info("New executor created!");
        this.proc = new Processor(false);
        this.compiler = proc.newXsltCompiler();
        try {
            this.executable = compiler.compile(
                    new StreamSource(new StringReader(config.getXsltTemplate())));
        } catch (SaxonApiException ex) {
            throw ContextUtils.dpuException(ctx, ex, "xslt.dpu.executor.errors.xsltCompile");
        }
        this.config = config;
        this.taskQueue = taskQueue;
        this.ctx = ctx;
        this.totalFileCounter = totalFileCounter;
    }

    @Override
    public void run() {
        LOG.info("Executor starting.");
        Task task;
        try {
            LOG.info("run - start");
            while (!((task = (Task) taskQueue.take()) instanceof DeadPill) && !task.status.terminateThreads) {
                LOG.info("run - execute");
                execute(task);
                LOG.info("run - execute - done");
            }
            LOG.info("run - dead");
            // Reinsert DeadPill for other thread.
            while (!taskQueue.offer(task, 1, TimeUnit.SECONDS));
        } catch (InterruptedException ex) {
            LOG.error("InterruptedException executor terminated!", ex);
        } catch (Exception ex) {
            ContextUtils.sendError(ctx, "xslt.dpu.executor.fail", ex, "");
            return;
        } catch (Throwable ex) {
            LOG.error("Full throwable error.", ex);
            ContextUtils.sendError(ctx, "xslt.dpu.executor.fail", "", ex.getMessage());
            return;
        }
        // We finished peacefully.
        LOG.info("Executor finished.");
        threadFail = false;
    }

    private void execute(Task task){
        LOG.info("Processing {}/{} : {} size: {}M", task.order, totalFileCounter, task.symbolicName,
                task.sourceFile.length() / 1024.0 / 1024.0);

        // Transform file - go parallel.
        LOG.debug("Memory used (start): {}M", String.valueOf((Runtime.getRuntime().totalMemory()
                - Runtime.getRuntime().freeMemory()) / 1024 / 1024));
        // Prepare classes and parameters.
        final Serializer out = new Serializer(task.targetFile);
        executable.getProcessor().registerExtensionFunction(UUIDGenerator.getInstance());
        
        final XsltTransformer transformer = executable.load();
        
        LOG.debug("Used parameters:");
        for (XsltConfig_V2.Parameter parameter : config.getFilesParameters(task.symbolicName)) {
            LOG.debug("\t {} : {}", parameter.getKey(), parameter.getValue());
            transformer.setParameter(new QName(parameter.getKey()), new XdmAtomicValue(parameter.getValue()));
        }
        try {
            transformer.setSource(new StreamSource(task.sourceFile));
            transformer.setDestination(out);
            transformer.transform();
            // Clear document cache.
            transformer.getUnderlyingController().clearDocumentPool();
            task.addToOutput = true;
        } catch (SaxonApiException ex) {
            if (config.isFailOnError()) {
                ContextUtils.sendError(ctx,
                        ctx.tr("xslt.dpu.executor.fileFail.caption", task.order), ex,
                        "xslt.dpu.executor.fileFail.body", task.symbolicName, task.sourceFile);
                task.status.terminateThreads = true;    // Should kill all threads.
                return;
            } else {
                ContextUtils.sendWarn(ctx,
                        ctx.tr("xslt.dpu.executor.fileFail.caption", task.order), ex,
                        "xslt.dpu.executor.fileFail.body", task.symbolicName, task.sourceFile);
            }
        } finally {
            // In every case close opened resources.
            try {
                out.close();
            } catch (SaxonApiException ex) {
                LOG.warn("Can't close Serializer.", ex);
            }
            try {
                transformer.close();
            } catch (SaxonApiException ex) {
                LOG.warn("Can't close XsltTransformer.", ex);
            }
        }
        LOG.info("Output file size {}M (input: {})", task.targetFile.length() / 1024.0 / 1024.0,
                task.symbolicName);
        LOG.debug("Memory used(end): {}M", String.valueOf((Runtime.getRuntime().totalMemory()
                - Runtime.getRuntime().freeMemory()) / 1024.0 / 1024.0));
    }

    public boolean isThreadFail() {
        return threadFail;
    }

}
