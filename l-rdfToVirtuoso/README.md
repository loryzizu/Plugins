# L-RdfToVirtuoso #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |L-RdfToVirtuoso                                              |
|**Description:**              |RdfToVirtuoso uses Virtuoso Sesame Provider 1.21 (Virtuoso 7 Develop branch) which works with Virtuoso 7, to load RDF graphs from internal store to external Virtuoso instance. |
|**Status:**                   |Supported in Plugins v2.X. Updated to use Plugin-DevEnv v2.X.       |
|                              |                                                               |
|**DPU class name:**           |RdfToVirtuoso     | 
|**Configuration class name:** |RdfToVirtuosoConfig_V1                           |
|**Dialogue class name:**      |RdfToVirtuosoVaadinDialog | 

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Virtuoso JDBC URL:** |URL for establishing JDBC session with Virtuoso server.  |
|**Username:** |Username for Virtuoso server. |
|**Password:** |Password for the username. |
|**Clear destination graph before loading (checkbox):** |Self-descriptive  |
|**Target Graph:**|Target graph URI. May be empty to indicate per-graph loading. In per-graph mode, each graph on input is loaded into separate graph on output, graph name is taken from VirtualGraph, if VirtualGraph is not set, internal RDF store graph name is used. |
|**Thread count:**|How many threads may be used to speed up loading |
|**Skip file on error (checkbox):**|Do not stop the pipeline when error occurs (if checked). |


***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|rdfInput |i | RDFDataUnit | RDF graphs to be loaded |
|rdfOutput |o | WritableRDFDataUnit | In per-graph mode: RDF graphs from input with CKAN Resource set, with VirtualGraph set to real graph name, where the data were loaded. In single graph mode: One RDF graph with CKAN Resource set, with VirtualGraph set to real graph name, where the data were loaded. |

***

### Version history ###

|Version            |Release notes                                   |
|-------------------|------------------------------------------------|
|1.0.0              |Initial release.                         |


***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|N/A               |N/A                   | 
