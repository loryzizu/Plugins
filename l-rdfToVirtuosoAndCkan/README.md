# L-RdfToVirtuosoAndCkan #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |L-RdfToVirtuosoAndCkan                                              |
|**Description:**              |Loads RDF data to Virtuoso using L-RdfToVirtuoso and creates CKAN resources using L-RdfToCkan. |
|**Status:**                   |Supported in Plugins v2.X. Updated to use Plugin-DevEnv v2.X.       |
|                              |                                                               |
|**DPU class name:**           |RdfToVirtuosoAndCkan     | 
|**Configuration class name:** |RdfToVirtuosoAndCkanConfig_V1                           |
|**Dialogue class name:**      |RdfToVirtuosoAndCkanVaadinDialog | 

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Virtuoso JDBC URL:** |URL for establishing JDBC session with Virtuoso server.  |
|**Username:** |Username for Virtuoso server. |
|**Password:** |Password for the username. |
|**Clear destination graph before loading (checkbox):** |Self-descriptive  |
|**Target Graph:**|Target graph URI. May be empty to indicate per-graph loading (each graph on input is loaded into separate graph on output) |
|**Thread count:**|How many threads may be used to speed up loading |
|**Skip file on error (checkbox):**|Do not stop the pipeline when error occurs (if checked). |


***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|TODO: provide Name, Dataunit and Description of input |i |  |  |

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

