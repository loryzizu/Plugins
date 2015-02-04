package eu.unifiedviews.plugins.loader.filesupload;

import java.net.URI;

import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.util.CryptorFactory;

import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;
import eu.unifiedviews.helpers.dpu.config.InitializableConfigDialog;
import eu.unifiedviews.helpers.dpu.localization.Messages;

@SuppressWarnings("serial")
public class FilesUploadVaadinDialog extends BaseConfigDialog<FilesUploadConfig_V1> implements InitializableConfigDialog {

    private TextField uri;

    private TextField username;

    private PasswordField password;

    private Messages messages;

    public FilesUploadVaadinDialog() {
        super(FilesUploadConfig_V1.class);
    }

    @Override
    public void initialize() {
        messages = new Messages(getContext().getLocale(), getClass().getClassLoader());

        Panel panel = new Panel();
        panel.setContent(buildMainLayout());
        panel.setSizeFull();

        setCompositionRoot(panel);
        setHeight("100%");
        setWidth("100%");
    }

    private VerticalLayout buildMainLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setHeight("-1px");
        mainLayout.setImmediate(false);
        mainLayout.setMargin(false);
        mainLayout.setSpacing(true);
        mainLayout.setWidth("100%");

        uri = new TextField(messages.getString("FilesUploadVaadinDialog.uri"));
        uri.setDescription(messages.getString("FilesUploadVaadinDialog.uri.description"));
        uri.setRequired(true);
        uri.setRequiredError(messages.getString("FilesUploadVaadinDialog.uri.required"));
        uri.setWidth("100%");

        mainLayout.addComponent(uri);

        username = new TextField(messages.getString("FilesUploadVaadinDialog.username"));

        mainLayout.addComponent(username);

        password = new PasswordField(messages.getString("FilesUploadVaadinDialog.password"));

        mainLayout.addComponent(password);

        return mainLayout;
    }

    @Override
    protected FilesUploadConfig_V1 getConfiguration() throws DPUConfigException {
        FilesUploadConfig_V1 result = new FilesUploadConfig_V1();

        try {
            if (StringUtils.isBlank(uri.getValue())) {
                throw new DPUConfigException(messages.getString("FilesUploadVaadinDialog.uri.required"));
            } else if (StringUtils.isBlank(username.getValue()) && StringUtils.isNotBlank(password.getValue())) {
                throw new DPUConfigException(messages.getString("FilesUploadVaadinDialog.username.required"));
            } else if (StringUtils.isNotBlank(username.getValue()) && StringUtils.isBlank(password.getValue())) {
                throw new DPUConfigException(messages.getString("FilesUploadVaadinDialog.password.required"));
            }

            URI encodedUri = new URI(URIUtil.encodePathQuery(uri.getValue(), "utf8"));

            if (StringUtils.isNotBlank(username.getValue()) && StringUtils.isBlank(encodedUri.getHost())) {
                throw new DPUConfigException(messages.getString("FilesUploadVaadinDialog.uri.invalid"));
            }

            result.setUri(encodedUri.toString());

            result.setUsername(username.getValue());

            if (StringUtils.isNotBlank(password.getValue())) {
                result.setPassword(CryptorFactory.getCryptor().encrypt(password.getValue()));
            }
        } catch (DPUConfigException e) {
            throw e;
        } catch (Exception e) {
            throw new DPUConfigException(messages.getString("FilesUploadVaadinDialog.getConfiguration.exception"), e);
        }

        return result;
    }

    @Override
    protected void setConfiguration(FilesUploadConfig_V1 config) throws DPUConfigException {
        try {
            uri.setValue(URIUtil.decode(config.getUri(), "utf8"));

            username.setValue(config.getUsername());

            if (StringUtils.isNotBlank(config.getPassword())) {
                password.setValue(CryptorFactory.getCryptor().decrypt(config.getPassword()));
            }
        } catch (Exception e) {
            throw new DPUConfigException(messages.getString("FilesUploadVaadinDialog.setConfiguration.exception"), e);
        }
    }

    @Override
    public String getDescription() {
        return messages.getString("FilesUploadVaadinDialog.getDescription", new Object[] { uri.getValue() });
    }

}
