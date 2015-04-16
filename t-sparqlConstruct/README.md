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
|2.1.0            | TODO to describe changes |
|2.0.1            | fixes in build dependencies |
|2.0.0            | Imported from https://github.com/mff-uk/DPUs repository of DPUs, using helpers 2.0.0                         |


***

### Developer's notes ###

|Author           |Notes                           |
|-----------------|--------------------------------|
|N/A              |N/A                             | 
