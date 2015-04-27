package eu.unifiedviews.plugins.loader.filestoparliament;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.files.FilesHelper;
import eu.unifiedviews.helpers.dataunit.virtualpath.VirtualPathHelper;
import eu.unifiedviews.helpers.dataunit.virtualpath.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.context.UserContext;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;

@DPU.AsLoader
public class FilesToParliament extends AbstractDpu<FilesToParliamentConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(FilesToParliament.class);

    @DataUnit.AsInput(name = "input")
    public FilesDataUnit filesInput;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    public FilesToParliament() {
        super(FilesToParliamentVaadinDialog.class, ConfigHistory.noHistory(FilesToParliamentConfig_V1.class));
    }

    public void sendFile(UserContext ctx, String parliamentBulkInsertLocation, CloseableHttpClient client, String rdfFormat, String filename, FilesDataUnit.Entry entry) throws DPUException {
        CloseableHttpResponse response = null;
        try {

            URIBuilder uriBuilder;
            uriBuilder = new URIBuilder(parliamentBulkInsertLocation);

            uriBuilder.setPath(uriBuilder.getPath());
            HttpPost httpPost = new HttpPost(uriBuilder.build().normalize());
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                    .addTextBody("dataFormat", rdfFormat, ContentType.MULTIPART_FORM_DATA)
                    .addBinaryBody("statements", new File(URI.create(entry.getFileURIString())), ContentType.DEFAULT_BINARY, filename);

            HttpEntity entity = entityBuilder.build();
            httpPost.setEntity(entity);
            response = client.execute(httpPost);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw ContextUtils.dpuException(ctx, "RdfToCkan.execute.exception.uploadFail");
            }
        } catch (URISyntaxException | IllegalStateException | IOException | DataUnitException ex) {
            throw ContextUtils.dpuException(ctx, ex, "RdfToCkan.execute.exception.exception");
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
                try {
                    response.close();
                } catch (IOException ex) {
                    LOG.warn("Error in close", ex);
                }
            }
        }
    }

    @Override
    protected void innerExecute() throws DPUException {
        CloseableHttpClient client = null;
        try {
            client = HttpClients.createDefault();
            for (FilesDataUnit.Entry entry : FilesHelper.getFiles(filesInput)) {
                String filename = VirtualPathHelpers.getVirtualPath(filesInput, entry.getSymbolicName());
                if (StringUtils.isEmpty(filename)) {
                    filename = entry.getSymbolicName();
                }
                sendFile(ctx, config.getBulkUploadEndpointURL(), client, config.getRdfFileFormat().toUpperCase(), filename, entry);
            }
        } catch (DataUnitException ex) {
            throw ContextUtils.dpuException(ctx, ex, "RdfToCkan.execute.exception.exception");
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException ex) {
                    LOG.warn("Error in close", ex);
                }
            }
        }
    }

}
