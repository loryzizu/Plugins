# T-SparqlConstruct #
----------

###General###

|                              |                                                                              |
|------------------------------|------------------------------------------------------------------------------|
|**Name:**                     |T-SparqlConstruct                                                             |
|**Description:**              |Transform input using SPARQL construct.                                       |
|                              |                                                                              |
|**DPU class name:**           |SparqlConstruct                                                               | 
|**Configuration class name:** |SparqlConstruct_V1                                                            |
|**Dialogue class name:**      |SparqlConstructDialog                                                         |

***

###Configuration parameters###

|Parameter                           |Description                                                              |
|------------------------------------|-------------------------------------------------------------------------|
|**SQL query:**                      |SQL query to extract data from source database                           |
**Target table name:**               |Table name used to internally store the extracted data                   |

***

### Inputs and outputs ###

|Name           |Type           |DataUnit           |Description                                  |
|---------------|---------------|-------------------|---------------------------------------------|
|input          |i              |RDFDataUnit        |RDF input                                    |
|outpu          |o              |RDFDataUnit        |RDF output (transformed)                     |

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
