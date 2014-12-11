package eu.unifiedviews.plugins.dputemplate;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

/**
 * We choose the type of this {@link DPU}, it can be {@link DPU.AsExtractor}, {@link DPU.AsLoader}, {@link DPU.AsTransformer}.
 * For this tutorial, we will program transformer DPU.
 *
 * If your {@link DPU} does not have any configuration dialog, you can declare is simply by
 * <p><blockquote><pre>
 * public class {@link DPUTemplate} implements {@link DPU}
 * </pre></blockquote></p>
 */
@DPU.AsTransformer
public class DPUTemplate extends ConfigurableBase<DPUTemplateConfig_V1> implements ConfigDialogProvider<DPUTemplateConfig_V1> {

    /**
     * We define one data unit on input, containing RDF graphs ({@link RDFDataUnit})
     * The name in {@link DataUnit.AsInput} has to be designed carefully, as once user creates pipelines with
     * your {@link DPU}, you can not change the name (it would break the pipeline graph).
     */
    @DataUnit.AsInput(name = "rdfIinput")
    public RDFDataUnit rdfInput;

    /**
     * We define one data unit on output, containing files ({@link FilesDataUnit}).
     */
    @DataUnit.AsOutput(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    /**
     * Public non-parametric constructor has to call super constructor in {@link ConfigurableBase}
     */
    public DPUTemplate() {
        super(DPUTemplateConfig_V1.class);
    }

    /**
     * Simple getter which is used by container to obtain configuration dialog instance.
     */
    @Override
    public AbstractConfigDialog<DPUTemplateConfig_V1> getConfigurationDialog() {
        return new DPUTemplateVaadinDialog();
    }

    /**
     * We implement the main method called "execute", which is being called when the {@link DPU} is launched.
     *
     * DPU's configuration is accessible under 'this.config'
     * DPU's context is accessible under 'dpuContext'
     *
     * Let's write simple RDF graph to file transformer DPU
     * It will export each RDF data graph from rdfInput to single RDF+XML file on the filesOutput
     * Copy any metadata from graph to file to be neat to others using them
     * And finally, we will generate one new file, which name is configured by user in dialog
     * and it will contain list of symbolicName;graphUri;fileLocation for each graph-file pair on each line
     * it is of no practical meaning, just to show the API
     *
     */
    @Override
    public void execute(DPUContext dpuContext) throws DPUException {

    }
}
