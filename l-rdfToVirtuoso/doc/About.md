### Description

Loads RDF graphs to external Virtuoso instance.

### Configuration parameters

| Name | Description |
|:----|:----|
|**Virtuoso JDBC URL** |URL for establishing JDBC session with Virtuoso server.|
|**Username** |Username for Virtuoso server.|
|**Password** |Password for the username.|
|**Clear destination graph before loading (checkbox)** |Self-descriptive |
|**Target Graph**|Target graph URI. May be empty to indicate per-graph loading. In per-graph mode, each graph on input is loaded into separate graph on output, graph name is taken from VirtualGraph, if VirtualGraph is not set, internal RDF store graph name is used.|
|**Thread count**|How many threads may be used to speed up loading|
|**Skip file on error (checkbox)**|Do not stop the pipeline when error occurs (if checked).|

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|rdfInput|i|RDFDataUnit|RDF graphs to be loaded|x|
|rdfOutput|o|RDFDataUnit|In per-graph mode: RDF graphs from input with CKAN Resource set, with VirtualGraph set to real graph name, where the data were loaded. In single graph mode: One RDF graph with CKAN Resource set, with VirtualGraph set to real graph name, where the data were loaded.||