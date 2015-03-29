# T-SparqlConstruct #
----------

###General###

|                              |                                                                              |
|------------------------------|------------------------------------------------------------------------------|
|**Name:**                     |T-SparqlConstruct                                                             |
|**Description:**              |Transform input using SPARQL construct.                                       |
|**Status:**                   |Supported in Plugins v2.X. Updated to use Plugin-DevEnv v2.X.       |
|                              |                                                                              |
|**DPU class name:**           |SparqlConstruct                                                               | 
|**Configuration class name:** |SparqlConstruct_V1                                                            |
|**Dialogue class name:**      |SparqlConstructDialog                                                         |

***

###Configuration parameters###

|Parameter                           |Description                                                              |
|------------------------------------|-------------------------------------------------------------------------|
|**Per-graph execution:**            |If checked query is executed per-graph.                                  |
|**SPARQL construct query:**         |SPARQL construct query.                                                  |

***

### Inputs and outputs ###

|Name           |Type           |DataUnit           |Description                                  |
|---------------|---------------|-------------------|---------------------------------------------|
|input          |i              |RDFDataUnit        |RDF input                                    |
|output         |o              |RDFDataUnit        |RDF output (transformed)                     |

***

### Version history ###

|Version          |Release notes               |
|-----------------|----------------------------|
|1.1.2-SNAPSHOT   |Import from CUNI repository.                         |


***

### Developer's notes ###

|Author           |Notes                           |
|-----------------|--------------------------------|
|N/A              |N/A                             | 
