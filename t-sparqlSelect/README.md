# T-SPARQLSelect #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |T-SPARQLSelect                                              |
|**Description:**              |Transforms SPARQL SELECT query result to CSV. Does not validate query. |
|**Status:**                   |Supported in Plugins v2.X. Updated to use Plugin-DevEnv v2.X.       |
|                              |                                                               |
|**DPU class name:**           |SparqlSelect     | 
|**Configuration class name:** |SparqlSelectConfig                           |
|**Dialogue class name:**      |SparqlSelectVaadinDialog | 

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Target path:*** |Path and target CSV file name.  |
|**SPARQL query:**|Text area dedicated for SPARQL SELECT query.  | 

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|input  |i |RDFDataUnit  |RDF graph.  |
|output |o |FilesDataUnit  |CSV file containing SPARQL SELECT query result.  |

***

### Version history ###

|Version            |Release notes                                   |
|-------------------|------------------------------------------------|
|2.1.0              | Update to API 2.1.0; sk and en localization      |
|2.0.1              | fixes in build dependencies |
|2.0.0              | Updated to helpers 2.0.0 |
|1.5.0              | N/A          |
|1.4.0              | N/A          |
|1.3.1              | N/A          |
|1.0.0              | N/A          |

***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|N/A               |N/A                   | 

