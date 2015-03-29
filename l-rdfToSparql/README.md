# L-RdfToSparqlEndpoint #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |L-RdfToSparqlEndpoint                                              |
|**Description:**              |Loads RDF data. |
|**Status:**                   |Supported in Plugins v2.X.  NOT updated to use Plugin-DevEnv v2.X. Compatibility library has to be used so that this DPU may be used in UnifiedViews v2.X, see [here](https://grips.semantic-web.at/pages/viewpage.action?pageId=59113485) .   |
|                              |                                                               |
|**DPU class name:**           |RdfToSparqlEndpoint     | 
|**Configuration class name:** |RdfToSparqlEndpointConfig_V1                           |
|**Dialogue class name:**      |RdfToSparqlEndpointVaadinDialog | 

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**SPARQL endpoint:** |SPARQL endpoint URL.  |
|**Name:** |Username to connect to SPARQL endpoints.|
|**Password:** |Password for username. |
|**Default Graph:**|Default graph name. |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|input |i |RDFDataUnit  |file containing RDF data  |

***

### Version history ###

|Version            |Release notes                                   |
|-------------------|------------------------------------------------|
|1.3.1              |N/A                                             |                                


***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|N/A               |N/A                   | 

