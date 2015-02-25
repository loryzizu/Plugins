package eu.unifiedviews.plugins.extractor.filesdownload;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.util.CryptorFactory;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Table.ColumnHeaderMode;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.localization.Messages;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

@SuppressWarnings("serial")
public class FilesDownloadVaadinDialog extends AbstractDialog<FilesDownloadConfig_V1> {

    private final Container container = new BeanItemContainer<>(VfsFile.class);

    private Messages messages;

    public FilesDownloadVaadinDialog() {
        super(FilesDownload.class);
    }

    @Override
    protected void buildDialogLayout() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setHeight("-1px");
        mainLayout.setImmediate(false);
        mainLayout.setMargin(false);
        mainLayout.setSpacing(true);
        mainLayout.setWidth("100%");

        Table table = new Table();
        table.addGeneratedColumn("remove", new ColumnGenerator() {

            @Override
            public Object generateCell(Table source, Object itemId, Object columnId) {
                Button result = new Button("-");
                final Table sourceFinal = source;
                final Object itemIdFinal = itemId;

                result.addClickListener(new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        if (sourceFinal.size() > 1) {
                            container.removeItem(itemIdFinal);
                        }
                    }

                });

                return result;
            }

        });
        table.setContainerDataSource(container);
        table.setColumnHeaderMode(ColumnHeaderMode.EXPLICIT);
        table.setColumnHeader("uri", ctx.tr("FilesDownloadVaadinDialog.uri"));
        table.setColumnHeader("username", ctx.tr("FilesDownloadVaadinDialog.username"));
        table.setColumnHeader("password", ctx.tr("FilesDownloadVaadinDialog.password"));
        table.setColumnHeader("fileName", ctx.tr("FilesDownloadVaadinDialog.fileName"));
        table.setEditable(true);
        table.setHeight("200");
        table.setTableFieldFactory(new TableFieldFactory() {

            @Override
            public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {
                AbstractTextField result = new TextField();

                if (propertyId.equals("uri")) {
                    result.setDescription(ctx.tr("FilesDownloadVaadinDialog.uri.description"));
                    result.setRequired(true);
                    result.setRequiredError(ctx.tr("FilesDownloadVaadinDialog.uri.required"));
                    result.setWidth("400");
                } else if (propertyId.equals("password")) {
                    result = new PasswordField();
                } else if (propertyId.equals("fileName")) {
                    result.setDescription(ctx.tr("FilesDownloadVaadinDialog.fileName.description"));
                }

                return result;
            }

        });
        table.setVisibleColumns("remove", "uri", "username", "password", "fileName");
        mainLayout.addComponent(table);

        Button addVfsFile = new Button("+");
        addVfsFile.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                container.addItem(new VfsFile());
            }

        });

        mainLayout.addComponent(addVfsFile);

        final Panel panel = new Panel();
        panel.setContent(mainLayout);
        panel.setSizeFull();
        setCompositionRoot(panel);
    }


    @SuppressWarnings("unchecked")
    @Override
    protected FilesDownloadConfig_V1 getConfiguration() throws DPUConfigException {
        List<VfsFile> vfsFiles = new ArrayList<>();
        vfsFiles.addAll((List<VfsFile>) container.getItemIds());

        try {
            for (VfsFile vfsFile : vfsFiles) {
                if (StringUtils.isBlank(vfsFile.getUri())) {
                    throw new DPUConfigException(ctx.tr("FilesDownloadVaadinDialog.uri.required"));
                } else if (StringUtils.isBlank(vfsFile.getUsername()) && StringUtils.isNotBlank(vfsFile.getPassword())) {
                    throw new DPUConfigException(ctx.tr("FilesDownloadVaadinDialog.username.required"));
                } else if (StringUtils.isNotBlank(vfsFile.getUsername()) && StringUtils.isBlank(vfsFile.getPassword())) {
                    throw new DPUConfigException(ctx.tr("FilesDownloadVaadinDialog.password.required"));
                }

                String encodedURI = vfsFile.getUri();

                if (vfsFile.isDisplayed()) {
                    encodedURI = URIUtil.encodePathQuery(vfsFile.getUri(), "utf8");
                }

                URI uri = new URI(encodedURI);

                if (StringUtils.isNotBlank(vfsFile.getUsername()) && StringUtils.isBlank(uri.getHost())) {
                    throw new DPUConfigException(ctx.tr("FilesDownloadVaadinDialog.uri.invalid"));
                }

                vfsFile.setUri(uri.toString());

                if (vfsFile.isDisplayed() && StringUtils.isNotBlank(vfsFile.getPassword())) {
                    vfsFile.setPassword(CryptorFactory.getCryptor().encrypt(vfsFile.getPassword()));
                }

                vfsFile.setDisplayed(false);
            }
        } catch (DPUConfigException e) {
            throw e;
        } catch (Exception e) {
            throw new DPUConfigException(ctx.tr("FilesDownloadVaadinDialog.getConfiguration.exception"), e);
        }

        FilesDownloadConfig_V1 result = new FilesDownloadConfig_V1();
        result.setVfsFiles(vfsFiles);

        return result;
    }

    @Override
    protected void setConfiguration(FilesDownloadConfig_V1 config) throws DPUConfigException {
        try {
            for (VfsFile vfsFile : config.getVfsFiles()) {
                if (!vfsFile.isDisplayed()) {
                    vfsFile.setUri(URIUtil.decode(vfsFile.getUri(), "utf8"));

                    if (StringUtils.isNotBlank(vfsFile.getPassword())) {
                        vfsFile.setPassword(CryptorFactory.getCryptor().decrypt(vfsFile.getPassword()));
                    }
                }

                vfsFile.setDisplayed(true);

                container.addItem(vfsFile);
            }
        } catch (Exception e) {
            throw new DPUConfigException(ctx.tr("FilesDownloadVaadinDialog.setConfiguration.exception"), e);
        }
    }

    @Override
    public String getDescription() {
        return ctx.tr("FilesDownloadVaadinDialog.getDescription", new Object[] { container.getItemIds().size() });
    }

}
