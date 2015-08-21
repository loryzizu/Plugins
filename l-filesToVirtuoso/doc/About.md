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
|**Include subdirectories (checkbox)** | If checked, subdirectories are also loaded to Virtuoso |
|**File name pattern** | A pattern for file names to be included according to SQL standard (characters string is symbolized as ‘%’). For example ‘%.ttl’ |
|**Target Graph** | Target graph URI |
|**Update status interval (s)** | Time period between status updates (in seconds) |
|**Thread count** | Number of concurrently running processes |
|**Skip file on error (checkbox)** | Do not stop the pipeline when error occurs (if checked) |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|config |i| RdfDataUnit | Dynamic DPU configuration, see Advanced configuration | |

The input for this DPU (the RDF data itself) has to be specified as a directory on local disc. All files in this directory must be either application/rdf+xml or text/turtle (filenames suffix .rdf or .ttl repspectively). Pipeline designer has to move the files into this directory before the loader is executed. This can be achieved by using l-filesUpload with 'run after' edge to this DPU. This will make sure, that loader is running at the time, when files are at the correct place.
Example pipeline design:

    --------------------      ----------------      -----------------                     ---------------------
    |                  |      |              |      |               |                     |                   |
    | e-sparqlEndpoint | ---> | t-rdfToFiles | ---> | l-filesUpload | --run after edge--> | l-FilesToVirtuoso |
    |                  |      |              |      |               |                     |                   |
    --------------------      ----------------      -----------------                     ---------------------
 
 
### Advanced configuration

It is also possible to dynamically configure the DPU over its input `config` using RDF data.

Configuration samples:

```turtle
<http://localhost/resource/config	
    <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://unifiedviews.eu/ontology/dpu/filesToVirtuoso/Config>;
    <http://unifiedviews.eu/ontology/dpu/filesToVirtuoso/config/fileName> "dataset.trig";
    <http://unifiedviews.eu/ontology/dpu/filesToVirtuoso/config/graphUri> "http://dataset".
```
