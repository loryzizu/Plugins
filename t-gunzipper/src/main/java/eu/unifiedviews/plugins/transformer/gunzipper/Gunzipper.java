package eu.unifiedviews.plugins.transformer.gunzipper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.openrdf.repository.RepositoryConnection;

import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.thirdparty.guava.common.io.Files;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit.Entry;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.helpers.dataunit.DataUnitUtils;
import eu.unifiedviews.helpers.dataunit.files.FilesHelper;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;

//import eu.unifiedviews.helpers.dpu.rdf.sparql.SparqlUtils;

/**
 * Main data processing unit class.
 *
 */
@DPU.AsTransformer
public class Gunzipper extends AbstractDpu<GunzipperConfig_V1> {

	private static final Logger LOG = LoggerFactory.getLogger(Gunzipper.class);

	// private static final String QUERY_COPY =
	// "INSERT { ?s ?p ?o } WHERE { ?s ?p ?o }";

	@DataUnit.AsInput(name = "filesInput")
	public FilesDataUnit input;

	@DataUnit.AsOutput(name = "filesOutput")
	public WritableFilesDataUnit output;

	@ExtensionInitializer.Init
	public FaultTolerance faultTolerance;

	public Gunzipper() {
		super(GunzipperVaadinDialog.class, ConfigHistory
				.noHistory(GunzipperConfig_V1.class));
	}

	@Override
	protected void innerExecute() throws DPUException {
		Set<FilesDataUnit.Entry> files;
		try {
			files = FilesHelper.getFiles(input);

			for (Entry entry : files) {
				File inputFile = new File(URI.create(entry.getFileURIString()));
				String outputFileUriString = output.addNewFile(entry
						.getSymbolicName());
				File outputFile = new File(URI.create(outputFileUriString));
				GZIPInputStream gzis = null;
				FileOutputStream fos = null;
				try {
					byte[] buffer = new byte[1024];
					gzis = new GZIPInputStream(new FileInputStream(inputFile));
					fos = new FileOutputStream(outputFile);
					int len = 0;
					while ((len = gzis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
				} catch (IOException ex) {
					throw new DPUException(ex);
				} finally {
					try {
						gzis.close();
					} catch (IOException ex) {
						LOG.warn("Error in close", ex);
					}
					try {
						fos.close();
					} catch (IOException ex) {
						LOG.warn("Error in close", ex);
					}
				}
			}
		} catch (DataUnitException ex) {
			throw new DPUException(ex);
		}
	}
}
