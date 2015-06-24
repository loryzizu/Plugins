# E-SparqlEndpoint #
----------

###General###

|                              |                                                                             |
|------------------------------|------------------------------------------------------------------------------|
|**Name:**                     |E-SparqlEndpoint |
|**Description:**              |Extracts RDF statements from external SPARQL endpoint into a single output graph. May be used instead of e-RdfFromSparql. |
|**Status:**                   | |
|                              | |
|**DPU class name:**           |SparqlEndpoint                                                             |
|**Configuration class name:** |SparqlEndpointConfig_V1                                                    |
|**Dialogue class name:**      |SparqlEndpointVaadinDialog                                                 |

***

###Configuration parameters###

|Parameter                           |Description                                                              |
|------------------------------------|-------------------------------------------------------------------------|
|**Enpoint URL:**|URL of SPARQL endpoint to extract data from.|
|**SPARQL Construct:**|SPARQL construct used to extract data. |


***

### Inputs and outputs ###

|Name           |Type           |DataUnit           |Description                                  |
|---------------|---------------|-------------------|---------------------------------------------|
|output |o |RdfDataUnit |Extracted RDF statements. |

***

### Version history ###

|Version          |Release notes                |
|-----------------|-----------------------------|
|1.0.1            | Moved from Charles University repository (https://github.com/mff-uk/DPUs/tree/master/dpu). |


***

### Developer's notes ###

|Author           |Notes                           |
|-----------------|--------------------------------|
| | |

***

### Runtime dependencies ###

|Dependency|Provided by|
|---------|----|
|commons-io-2.4.jar|UnifiedViews,Core|
|httpclient-osgi-4.3.2.jar||
|httpcore-osgi-4.3.2.jar||
|jackson-annotations-2.4.4.jar||
|jackson-core-2.4.4.jar||
|jackson-databind-2.4.4.jar||
|opencsv-3.3.jar||
|commons-lang3-3.3.2.jar||
