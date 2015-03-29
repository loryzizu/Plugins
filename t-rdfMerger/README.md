# T-RdfMerger #
----------

###General###

|                              |                                                                             |
|------------------------------|-----------------------------------------------------------------------------|
|**Name:**                     |T-RdfMerger                                                               |
|**Description:**              |Merges RDF data in no time. |
|**Status:**                   |Supported in Plugins v2.X. Updated to use Plugin-DevEnv v2.X.       |
|                              |                                                                             |
|**DPU class name:**           |RdfMerger                                                                 | 
|**Configuration class name:** |RdfMergerConfig_V1                             |
|**Dialogue class name:**      |RdfMergerVaadinDialog                      |

***

###Configuration parameters###

|Parameter                                       |Description                                                              |                                                        
|------------------------------------------------|-------------------------------------------------------------------------|
|**N/A**                                         |N/A                |

***

### Inputs and outputs ###

|Name         |Type           |DataUnit     |Description             |
|-------------|---------------|-------------|------------------------|
|rdfInput     |i              |RDFDataUnit  |DataUnit to which user connects all inputs which has to be merged. |  
|rdfOutput    |o              |RDFDataUnit  |DataUnit which outputs all input graphs. |

***

### Version history ###

|Version          |Release notes               |
|-----------------|----------------------------|
|1.3.2            |N/A                         |                                
|1.6.1            |SPARQL is used to copy metadata. DPU moved from Core to Plugins repository. |

***

### Developer's notes ###

|Author           |Notes                           |
|-----------------|--------------------------------|
|N/A              |N/A                             | 
