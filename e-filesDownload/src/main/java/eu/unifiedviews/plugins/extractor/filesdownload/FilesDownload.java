package eu.unifiedviews.plugins.extractor.filesdownload;

import java.io.File;
import java.net.URI;
import java.util.Date;

import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.cache.NullFilesCache;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.ftps.FtpsFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.commons.vfs2.util.CryptorFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.resourcehelper.Resource;
import eu.unifiedviews.helpers.dataunit.resourcehelper.ResourceHelper;
import eu.unifiedviews.helpers.dataunit.resourcehelper.ResourceHelpers;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelper;
import eu.unifiedviews.helpers.dataunit.virtualpathhelper.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;
import eu.unifiedviews.helpers.dpu.localization.Messages;

@DPU.AsExtractor
public class FilesDownload extends ConfigurableBase<FilesDownloadConfig_V1> implements ConfigDialogProvider<FilesDownloadConfig_V1> {

    @DataUnit.AsOutput(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    public FilesDownload() {
        super(FilesDownloadConfig_V1.class);
    }

    @Override
    public void execute(DPUContext context) throws DPUException, InterruptedException {
        StandardFileSystemManager standardFileSystemManager = new StandardFileSystemManager();
        standardFileSystemManager.setClassLoader(standardFileSystemManager.getClass().getClassLoader());

        FileSystemOptions fileSystemOptions = new FileSystemOptions();
        FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(fileSystemOptions, false);
        FtpsFileSystemConfigBuilder.getInstance().setUserDirIsRoot(fileSystemOptions, false);
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(fileSystemOptions, false);
        ResourceHelper resourceHelper = ResourceHelpers.create(filesOutput);
        VirtualPathHelper virtualPathHelper = VirtualPathHelpers.create(filesOutput);
        Messages messages = new Messages(context.getLocale(), getClass().getClassLoader());

        try {
            standardFileSystemManager.setFilesCache(new NullFilesCache());
            standardFileSystemManager.init();

            for (VfsFile vfsFile : config.getVfsFiles()) {
                if (context.canceled()) {
                    break;
                }

                if (StringUtils.isNotBlank(vfsFile.getUsername())) {
                    DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(
                            fileSystemOptions,
                            new StaticUserAuthenticator(
                                    URI.create(vfsFile.getUri()).getHost(),
                                    vfsFile.getUsername(),
                                    CryptorFactory.getCryptor().decrypt(vfsFile.getPassword())));
                }

                FileObject[] fileObjects = standardFileSystemManager.resolveFile(vfsFile.getUri(), fileSystemOptions).findFiles(new AllFileSelector());

                if (fileObjects != null) {
                    for (FileObject fileObject : fileObjects) {
                        if (FileType.FILE.equals(fileObject.getType())) {
                            String fileName = fileObject.getName().getPathDecoded();

                            if (StringUtils.isNotBlank(vfsFile.getFileName())) {
                                fileName = vfsFile.getFileName();
                            }

                            FileUtils.copyInputStreamToFile(fileObject.getContent().getInputStream(), new File(URI.create(filesOutput.addNewFile(fileName))));

                            Resource resource = resourceHelper.getResource(fileName);
                            Date now = new Date();

                            resource.setCreated(now);
                            resource.setLast_modified(now);
                            resource.getExtras().setSource(URIUtil.decode(vfsFile.getUri(), "utf8"));

                            resourceHelper.setResource(fileName, resource);

                            virtualPathHelper.setVirtualPath(fileName, fileName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new DPUException(messages.getString("FilesDownload.execute.exception"), e);
        } finally {
            standardFileSystemManager.close();
            resourceHelper.close();
            virtualPathHelper.close();
        }
    }

    @Override
    public AbstractConfigDialog<FilesDownloadConfig_V1> getConfigurationDialog() {
        return new FilesDownloadVaadinDialog();
    }

}
