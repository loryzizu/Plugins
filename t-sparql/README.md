# T-SPARQL #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |T-SPARQL                                              |
|**Description:**              |Transforms data using SPARQL. |
|**Status:**                   |Deprecated. Not supported in Plugins v2.X. Not updated to use Plugin-DevEnv v2.X. Use t-sparqlUpdate or t-sparqlConstruct instead.  |
|                              |                                                               |
|**DPU class name:**           |SPARQL     | 
|**Configuration class name:** |SPARQLConfig_V1<BR>SPARQLConfig_V2                           |
|**Dialogue class name:**      |SPARQLVaadinDialog<BR>SPARQLVaadinDialog2 | 

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Add query tab (button):** |Adds new tab where query can be created (if more than one query is created, queries are executed from top to bottom). |
|**Query**|Tab (text area) dedicated for SPARQL query.  | 
|**Output graph symbolic name** |self-descriptive |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|input  |i |RDFDataUnit  |RDF graph.  |
|output |o |RDFDataUnit  |RDF graph.  |

***

### Version history ###

|Version            |Release notes                                   |
|-------------------|------------------------------------------------|
|1.6.0              |localization support                            |                                
|1.3.1              |N/A                                             |                                


***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|N/A               |N/A                   | 

