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

### Advanced configuration

It is also possible to dynamically configure the DPU over its input `config` using RDF data.

Configuration samples:

```turtle
<http://localhost/resource/config	
    <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://unifiedviews.eu/ontology/dpu/filesToVirtuoso/Config>;
    <http://unifiedviews.eu/ontology/dpu/filesToVirtuoso/config/fileName> "dataset.trig";
    <http://unifiedviews.eu/ontology/dpu/filesToVirtuoso/config/graphUri> "http://dataset".
```
