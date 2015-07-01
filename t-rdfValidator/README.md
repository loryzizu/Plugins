# T-RdfValidator #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |T-RdfValidator                                              |
|**Description:**              |Validates RDF data. |
|**Status:**                   |Supported in Plugins v2.X. Updated to use Plugin-DevEnv v2.X.       |
|                              |                                                               |
|**DPU class name:**           |RdfValidator     | 
|**Configuration class name:** |RdfValidatorConfig_V2                           |
|**Dialogue class name:**      |RdfValidatorVaadinDialog | 

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Validation query** | ASK or SELECT SPARQL query. ASK returning True = validation fails, SELECT returning non-zero tuples = validation fails.  |
|**Fail execution when validation produce any error** |When checked, DPU will throw an exception when validation fails, causing pipeline to fail. Otherwise, the DPU will just log results and return successfully. |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|rdfInput |i |RDFDataUnit  |Input RDF to be validated.   |
|rdfOutput|o(optional) |RDFDataUnit  |Copy of rdfInput data. | 

***

### Version history ###

|Version            |Release notes                                   |
|-------------------|------------------------------------------------|
|3.0.0              | Select/Ask queries replace old Insert type. Dropped per-graph mode, optional output contains copy of input (instead of invalid triples)         |
|2.1.0              | Update to API 2.1.0; Localization        |
|2.0.1              | fixes in build dependencies |
|2.0.0              | Update for helpers 2.0.0                        |
|1.6.0              | fixed per graph select                          |
|1.5.0              | N/A                                             |
|1.4.0              | N/A                                             |
|1.3.1              | N/A                                             |
|1.3.0              | N/A                                             |


***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|N/A               |N/A                   | 

