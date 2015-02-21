package eu.unifiedviews.plugins.loader.filesupload;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.ftps.FtpsFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.commons.vfs2.util.CryptorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.cuni.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.cuni.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.cuni.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.cuni.dpu.exec.AutoInitializer;
import eu.unifiedviews.helpers.cuni.extensions.FaultTolerance;
import eu.unifiedviews.helpers.cuni.extensions.FaultToleranceUtils;
import eu.unifiedviews.helpers.cuni.extensions.RdfConfiguration;
import eu.unifiedviews.helpers.dataunit.copyhelper.CopyHelpers;
import eu.unifiedviews.helpers.dataunit.resourcehelper.Resource;
import eu.unifiedviews.helpers.dataunit.resourcehelper.ResourceHelpers;
import eu.unifiedviews.plugins.extractor.filestoscp.FilesToScpConfig_V1;
import eu.unifiedviews.plugins.loader.filestolocalfs.FilesToLocalFSConfig_V1;

@DPU.AsLoader
public class FilesUpload extends AbstractDpu<FilesUploadConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(FilesUpload.class);

    @RdfConfiguration.ContainsConfiguration
    @DataUnit.AsInput(name = "config", optional = true)
    public RDFDataUnit rdfConfiguration;

    @DataUnit.AsInput(name = "input")
    public FilesDataUnit filesInput;

    @DataUnit.AsOutput(name = "output")
    public WritableFilesDataUnit filesOutput;

    @AutoInitializer.Init
    public FaultTolerance faultTolerance;

    @AutoInitializer.Init
    public RdfConfiguration _rdfConfiguration;

    @AutoInitializer.Init
    public MultipleConfigurationUpdate _configurationUpdate;

    public FilesUpload() {
        super(FilesUploadVaadinDialog.class,
                ConfigHistory.history(FilesUploadConfig_V1.class)
                .alternative(FilesToScpConfig_V1.class)
                .alternative(FilesToLocalFSConfig_V1.class)
                .addCurrent(FilesUploadConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {
        // Prepare VFS2.
        final StandardFileSystemManager standardFileSystemManager = new StandardFileSystemManager();
        standardFileSystemManager.setClassLoader(standardFileSystemManager.getClass().getClassLoader());

        final FileSystemOptions fileSystemOptions = new FileSystemOptions();
        FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(fileSystemOptions, false);
        FtpsFileSystemConfigBuilder.getInstance().setUserDirIsRoot(fileSystemOptions, false);
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(fileSystemOptions, false);

        try {
            standardFileSystemManager.init();
        } catch (FileSystemException ex) {
            throw ContextUtils.dpuException(ctx, ex, "FilesUpload.execute.exception");
        }
        // Configure authenticator.
        if (StringUtils.isNotBlank(config.getUsername())) {

            final StaticUserAuthenticator userAuthenticator;

            try {
                userAuthenticator = new StaticUserAuthenticator(URI.create(config.getUri()).getHost(),
                        config.getUsername(), CryptorFactory.getCryptor().decrypt(config.getPassword()));
            } catch (Exception ex) {
                throw ContextUtils.dpuException(ctx, ex, "FilesUpload.execute.exception");
            }
            try {
                DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(fileSystemOptions,
                        userAuthenticator);
            } catch (FileSystemException ex) {
                throw ContextUtils.dpuException(ctx, ex, "FilesUpload.execute.exception");
            }
        }
        // Get files to upload.
        final List<FilesDataUnit.Entry> files = FaultToleranceUtils.getEntries(faultTolerance, filesInput,
                FilesDataUnit.Entry.class);
        final String baseUri;
        if (config.getUri().endsWith("/")) {
            baseUri = config.getUri();
        } else {
            baseUri = config.getUri() + "/";
        }
        ContextUtils.sendShortInfo(ctx, baseUri);
        // Iterate and upload files.
        int counter = 0;
        for (final FilesDataUnit.Entry entry : files) {
            if (ctx.canceled()) {
                throw ContextUtils.dpuExceptionCancelled(ctx);
            }
            LOG.info("Processing {}/{} ...", ++counter, files.size());
            final String fileName = FaultToleranceUtils.getVirtualPath(faultTolerance, filesInput, entry);
            if (fileName == null) {
                // Use symbolic name
                throw ContextUtils.dpuException(ctx, "FilesUpload.execute.missingVirtualPath", entry);
            }
            try {
                // Upload file.
                final FileObject fileObject;
                try {
                    fileObject = standardFileSystemManager.resolveFile(baseUri + fileName, fileSystemOptions);
                } catch (FileSystemException ex) {
                    throw ContextUtils.dpuException(ctx, ex, "FilesUpload.execute.exception");
                }
                faultTolerance.execute(new FaultTolerance.Action() {

                    @Override
                    public void action() throws Exception {
                        final FileObject sourceFileObject = standardFileSystemManager.resolveFile(
                                entry.getFileURIString());
                        // Copy or move based on setting. WARN: Move can be dangerous!
                        if (config.isMoveFiles()) {
                            sourceFileObject.moveTo(fileObject);
                        } else {
                            fileObject.copyFrom(sourceFileObject, Selectors.SELECT_SELF);
                        }
                    }
                });
                // Some metadata copy to output work.
                faultTolerance.execute(new FaultTolerance.Action() {

                    @Override
                    public void action() throws Exception {
                        CopyHelpers.copyMetadata(entry.getSymbolicName(), filesInput, filesOutput);
                    }
                });
                final Resource resource = faultTolerance.execute(new FaultTolerance.ActionReturn<Resource>() {

                    @Override
                    public Resource action() throws Exception {
                        return ResourceHelpers.getResource(filesOutput, entry.getSymbolicName());
                    }
                });
                resource.setLast_modified(new Date());
                faultTolerance.execute(new FaultTolerance.Action() {

                    @Override
                    public void action() throws Exception {
                        ResourceHelpers.setResource(filesOutput, entry.getSymbolicName(), resource);
                    }
                });
            } catch (DPUException ex) {
                if (config.isSoftFail()) {
                    ContextUtils.sendWarn(ctx, "FilesUpload.execute.uploadFail", ex, entry.toString());
                } else {
                    throw ex;
                }
            }
            LOG.info("Processing {}/{} ... done", counter, files.size());
        }
    }

}
