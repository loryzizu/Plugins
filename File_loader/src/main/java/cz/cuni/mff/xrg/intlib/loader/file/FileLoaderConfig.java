package cz.cuni.mff.xrg.intlib.loader.file;

import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;
import cz.cuni.xrg.intlib.rdf.enums.RDFFormatType;

/**
 * Enum for naming setting values.
 *
 * @author Petyr
 * @author Jiri Tomes
 *
 */
public class FileLoaderConfig implements DPUConfigObject {

	public String DirectoryPath = "";

	public String FileName = "";

	public RDFFormatType RDFFileFormat = RDFFormatType.AUTO;

	public boolean DiffName = false;

	@Override
	public boolean isValid() {
		return DirectoryPath != null &&
				FileName != null &&
				RDFFileFormat != null;
	}
}
