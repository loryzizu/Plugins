# T-FilesToRdf #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |T-FilesToRdf                                              |
|**Description:**              |Extracts RDF data from Files (any file format) and adds them to RDF. |
|**Status:**                   |Supported in Plugins v2.X. Updated to use Plugin-DevEnv v2.X.       |
|                              |                                                               |
|**DPU class name:**           |FilesToRDF     | 
|**Configuration class name:** |FilesToRDFConfig_V1                           |
|**Dialogue class name:**      |FilesToRDFVaadinDialog | 

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Commit size (0 = one file, one transaction, 1 = autocommit connection, n = commit every n triples)** |TODO: provide description  |
|**Symbolic name to baseURI and Format map. Line format: symbolicName;baseURI(optional);FileFormat(optional)** |TODO: provide description |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|filesInput |i |FilesDataUnit  |Input file containing data.   |
|rdfOutput|o |RDFDataUnit  |RDF data extracted. | 

***

### Version history ###

|Version            |Release notes                                   |
|-------------------|------------------------------------------------|
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

