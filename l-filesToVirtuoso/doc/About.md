### Description

Loads RDF data (graphs) into Virtuoso

### Configuration parameters

| Name | Description |
|:----|:----|
|**Virtuoso JDBC URL** | URL for establishing JDBC session with Virtuoso server |
|**Username** | Username for Virtuoso server |
|**Password** | Password for the username |
|**Clear destination graph before loading (checkbox)** | Self-descriptive |
|**Directory to load path** | Path to directory to be loaded |
|**Include subdirectories** | Boolean value setting (accepts 'true' or 'false') |
|**File name pattern** | A pattern for file names to be included |
|**Target Graph** | Target graph URI |
|**Update status interval (s)** | Time period between status updates (in seconds) |
|**Thread count** | Number of concurrently running processes |
|**Skip file on error (checkbox)** | Do not stop the pipeline when error occurs (if checked) |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|TODO: provide Name, Dataunit and Description of input |i |  |  | |
|config |i| RdfDataUnit | Dynamic DPU configuration | |

### Advanced dynamic configuration over input RDF data unit

It is also possible to dynamically configure the DPU over its input configurational RDF data unit. Configuration sample:

    <http://localhost/resource/config	
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://unifiedviews.eu/ontology/dpu/filesToVirtuoso/Config>;
        <http://unifiedviews.eu/ontology/dpu/filesToVirtuoso/config/fileName> "dataset.trig";
        <http://unifiedviews.eu/ontology/dpu/filesToVirtuoso/config/graphUri> "http://dataset".


