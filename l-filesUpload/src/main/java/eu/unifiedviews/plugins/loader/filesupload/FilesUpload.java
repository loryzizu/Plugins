package eu.unifiedviews.plugins.loader.filesupload;

import java.net.URI;
import java.util.Date;

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
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit.Entry;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.copyhelper.CopyHelper;
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

@DPU.AsLoader
public class FilesUpload extends ConfigurableBase<FilesUploadConfig_V1> implements ConfigDialogProvider<FilesUploadConfig_V1> {
    private static final Logger LOG = LoggerFactory
            .getLogger(FilesUpload.class);

    @DataUnit.AsInput(name = "filesInput")
    public FilesDataUnit filesInput;

    @DataUnit.AsOutput(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    public FilesUpload() {
        super(FilesUploadConfig_V1.class);
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, InterruptedException {
        StandardFileSystemManager standardFileSystemManager = new StandardFileSystemManager();
        standardFileSystemManager.setClassLoader(standardFileSystemManager.getClass().getClassLoader());

        FileSystemOptions fileSystemOptions = new FileSystemOptions();
        FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(fileSystemOptions, false);
        FtpsFileSystemConfigBuilder.getInstance().setUserDirIsRoot(fileSystemOptions, false);
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(fileSystemOptions, false);
        VirtualPathHelper virtualPathHelper = VirtualPathHelpers.create(filesInput);
        Messages messages = new Messages(dpuContext.getLocale(), getClass().getClassLoader());
        CopyHelper copyHelper = CopyHelpers.create(filesInput, filesOutput);
        try {
            standardFileSystemManager.init();

            if (StringUtils.isNotBlank(config.getUsername())) {
                DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(
                        fileSystemOptions,
                        new StaticUserAuthenticator(
                                URI.create(config.getUri()).getHost(),
                                config.getUsername(),
                                CryptorFactory.getCryptor().decrypt(config.getPassword())));
            }
            long index = 0L;
            for (Entry entry : FilesHelper.getFiles(filesInput)) {
                try {
                    if (dpuContext.canceled()) {
                        break;
                    }

                    if (dpuContext.isDebugging()) {
                        LOG.debug("Processing {} file {}", index, entry);
                    }
                    String fileName = virtualPathHelper.getVirtualPath(entry.getSymbolicName());

                    if (StringUtils.isBlank(fileName)) {
                        fileName = entry.getSymbolicName();
                    }

                    FileObject destinationFileObject = standardFileSystemManager.resolveFile(config.getUri() + fileName, fileSystemOptions);
                    FileObject sourceFileObject = standardFileSystemManager.resolveFile(entry.getFileURIString());
                    if (config.isMoveFiles()) {
                        sourceFileObject.moveTo(destinationFileObject);
                    } else {
                        destinationFileObject.copyFrom(sourceFileObject, Selectors.SELECT_SELF);
                    }

                    copyHelper.copyMetadata(entry.getSymbolicName());
                    Resource resource = ResourceHelpers.getResource(filesOutput, entry.getSymbolicName());
                    resource.setLast_modified(new Date());
                    ResourceHelpers.setResource(filesOutput, entry.getSymbolicName(), resource);
                    if (dpuContext.isDebugging()) {
                        LOG.debug("Processed {} file", index);
                    }
                    index++;
                } catch (FileSystemException | DataUnitException ex) {
                    if (config.isSkipOnError()) {
                        LOG.warn("Error processing {} file {}", index, String.valueOf(entry), ex);
                    } else {
                        throw new DPUException(messages.getString("FilesUpload.execute.exception.skipFile", index, String.valueOf(entry)), ex);
                    }
                }
            }
        } catch (DPUException ex){
            throw ex;
        } catch (Exception ex) {
            throw new DPUException(messages.getString("FilesUpload.execute.exception"), ex);
        } finally {
            standardFileSystemManager.close();
            virtualPathHelper.close();
            copyHelper.close();
        }
    }

    @Override
    public AbstractConfigDialog<FilesUploadConfig_V1> getConfigurationDialog() {
        return new FilesUploadVaadinDialog();
    }
}
