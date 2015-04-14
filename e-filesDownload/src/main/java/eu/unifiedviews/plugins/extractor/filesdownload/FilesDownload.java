package eu.unifiedviews.plugins.extractor.filesdownload;

import java.io.IOException;
import java.net.URI;
import java.util.Date;

import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.cache.NullFilesCache;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.ftps.FtpsFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.uv.extractor.filesfromlocal.FilesFromLocalConfig_V1;
import cz.cuni.mff.xrg.uv.extractor.httpdownload.HttpDownloadConfig_V2;
import cz.cuni.mff.xrg.uv.extractor.scp.FilesFromScpConfig_V1;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.files.FilesDataUnitUtils;
import eu.unifiedviews.helpers.dataunit.resource.Resource;
import eu.unifiedviews.helpers.dataunit.resource.ResourceHelpers;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultToleranceUtils;
import eu.unifiedviews.helpers.dpu.extension.rdf.RdfConfiguration;
import eu.unifiedviews.plugins.extractor.httpdownload.HttpDownloadConfig_V1;

/**
 * TODO Add support for caching.
 * TODO Add support for soft failure.
 */
@DPU.AsExtractor
public class FilesDownload extends AbstractDpu<FilesDownloadConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(FilesDownload.class);

    @RdfConfiguration.ContainsConfiguration
    @DataUnit.AsInput(name = "config", optional = true)
    public RDFDataUnit rdfConfiguration;

    @DataUnit.AsOutput(name = "output")
    public WritableFilesDataUnit filesOutput;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    @ExtensionInitializer.Init
    public RdfConfiguration _rdfConfiguration;

    @ExtensionInitializer.Init
    public MultipleConfigurationUpdate _configurationUpdate;

    public FilesDownload() {
        super(FilesDownloadVaadinDialog.class,
                ConfigHistory.history(FilesDownloadConfig_V1.class)
                        .alternative(HttpDownloadConfig_V1.class)
                        .alternative(FilesFromScpConfig_V1.class)
                        .alternative(FilesFromLocalConfig_V1.class)
                        .alternative(HttpDownloadConfig_V2.class)
                        .addCurrent(FilesDownloadConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {
        final StandardFileSystemManager standardFileSystemManager = new StandardFileSystemManager();
        standardFileSystemManager.setClassLoader(standardFileSystemManager.getClass().getClassLoader());

        final FileSystemOptions fileSystemOptions = new FileSystemOptions();
        FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(fileSystemOptions, false);
        FtpsFileSystemConfigBuilder.getInstance().setUserDirIsRoot(fileSystemOptions, false);
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(fileSystemOptions, false);

        try {
            standardFileSystemManager.setFilesCache(new NullFilesCache());
            standardFileSystemManager.init();
        } catch (FileSystemException ex) {
            throw ContextUtils.dpuException(ctx, ex, "FilesDownload.execute.exception");
        }
        // For each file in cofiguration.
        for (final VfsFile vfsFile : config.getVfsFiles()) {
            if (ctx.canceled()) {
                throw ContextUtils.dpuExceptionCancelled(ctx);
            }
            // If user name is not blank, then we prepare for autentification.
            if (StringUtils.isNotBlank(vfsFile.getUsername())) {
                final StaticUserAuthenticator staticUserAuthenticator;
                try {
                    staticUserAuthenticator = new StaticUserAuthenticator(
                            URI.create(vfsFile.getUri()).getHost(),
                            vfsFile.getUsername(),
                            vfsFile.getPassword());
                } catch (Exception ex) {
                    throw ContextUtils.dpuException(ctx, ex, "FilesDownload.execute.exception");
                }
                try {
                    DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(fileSystemOptions,
                            staticUserAuthenticator);
                } catch (FileSystemException ex) {
                    throw ContextUtils.dpuException(ctx, ex, "FilesDownload.execute.exception");
                }
            }
            // One path can be resolved in multiple files (like directory.
            final FileObject[] fileObjects;
            try {
                fileObjects = standardFileSystemManager.resolveFile(vfsFile.getUri(), fileSystemOptions)
                        .findFiles(new AllFileSelector());
            } catch (FileSystemException ex) {
                throw ContextUtils.dpuException(ctx, ex, "FilesDownload.execute.exception");
            }

            if (fileObjects == null) {
                // Skip null files but add a log.                    
                LOG.warn("Skipping file: '{}' as it resolves on null value.", vfsFile.getUri());
                continue;
            }

            // We download each file.
            for (FileObject fileObject : fileObjects) {
                final boolean isFile;
                try {
                    isFile = FileType.FILE.equals(fileObject.getType());
                } catch (FileSystemException ex) {
                    throw ContextUtils.dpuException(ctx, ex, "FilesDownload.execute.exception");
                }
                if (isFile) {
                    // Get file name.
                    final String fileName;
                    if (StringUtils.isNotBlank(vfsFile.getFileName())) {
                        fileName = vfsFile.getFileName();
                    } else {
                        try {
                            fileName = fileObject.getName().getPathDecoded();
                        } catch (FileSystemException ex) {
                            throw ContextUtils.dpuException(ctx, ex, "FilesDownload.execute.exception");
                        }
                    }
                    // Prepare new output file record.
                    final FilesDataUnit.Entry destinationFile = faultTolerance.execute(new FaultTolerance.ActionReturn<FilesDataUnit.Entry>() {

                        @Override
                        public FilesDataUnit.Entry action() throws Exception {
                            return FilesDataUnitUtils.createFile(filesOutput, fileName);
                        }
                    });
                    // Add some metadata, TODO: Improve this code!
                    faultTolerance.execute(new FaultTolerance.Action() {

                        @Override
                        public void action() throws Exception {
                            final Resource resource = ResourceHelpers.getResource(filesOutput, fileName);
                            final Date now = new Date();
                            resource.setCreated(now);
                            resource.setLast_modified(now);
                            resource.getExtras().setSource(URIUtil.decode(vfsFile.getUri(), "utf8"));
                            ResourceHelpers.setResource(filesOutput, fileName, resource);
                        }
                    }, "FilesDownload.execute.exception");
                    // Copy file.
                    try {
                        FileUtils.copyInputStreamToFile(fileObject.getContent().getInputStream(),
                                FaultToleranceUtils.asFile(faultTolerance, destinationFile));
                    } catch (IOException ex) {
                        throw ContextUtils.dpuException(ctx, ex, "FilesDownload.execute.exception");
                    }
                }
            }

        }
    }
}
