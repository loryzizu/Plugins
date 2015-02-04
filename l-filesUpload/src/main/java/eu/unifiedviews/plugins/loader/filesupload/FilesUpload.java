package eu.unifiedviews.plugins.loader.filesupload;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.ftps.FtpsFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.commons.vfs2.util.CryptorFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit.Entry;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.fileshelper.FilesHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;
import eu.unifiedviews.helpers.dpu.localization.Messages;

@DPU.AsLoader
public class FilesUpload extends ConfigurableBase<FilesUploadConfig_V1> implements ConfigDialogProvider<FilesUploadConfig_V1> {

    @DataUnit.AsInput(name = "filesInput")
    public FilesDataUnit filesInput;

    public FilesUpload() {
        super(FilesUploadConfig_V1.class);
    }

    @Override
    public void execute(DPUContext context) throws DPUException, InterruptedException {
        StandardFileSystemManager standardFileSystemManager = new StandardFileSystemManager();
        standardFileSystemManager.setClassLoader(standardFileSystemManager.getClass().getClassLoader());

        FileSystemOptions fileSystemOptions = new FileSystemOptions();
        FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(fileSystemOptions, false);
        FtpsFileSystemConfigBuilder.getInstance().setUserDirIsRoot(fileSystemOptions, false);
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(fileSystemOptions, false);
        VirtualPathHelper virtualPathHelper = VirtualPathHelpers.create(filesInput);
        Messages messages = new Messages(context.getLocale(), getClass().getClassLoader());

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

            for (Entry entry : FilesHelper.getFiles(filesInput)) {
                if (context.canceled()) {
                    break;
                }

                String fileName = virtualPathHelper.getVirtualPath(entry.getSymbolicName());

                if (StringUtils.isBlank(fileName)) {
                    fileName = entry.getSymbolicName();
                }

                FileObject fileObject = standardFileSystemManager.resolveFile(config.getUri() + fileName, fileSystemOptions);
                fileObject.copyFrom(standardFileSystemManager.resolveFile(entry.getFileURIString()), Selectors.SELECT_SELF);
            }
        } catch (Exception e) {
            throw new DPUException(messages.getString("FilesUpload.execute.exception"), e);
        } finally {
            standardFileSystemManager.close();
            virtualPathHelper.close();
        }
    }

    @Override
    public AbstractConfigDialog<FilesUploadConfig_V1> getConfigurationDialog() {
        return new FilesUploadVaadinDialog();
    }

}
