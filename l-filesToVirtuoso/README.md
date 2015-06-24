# L-FilesToVirtuoso #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |L-FilesToVirtuoso                                              |
|**Description:**              |VirtuosoLoader issues Virtuoso internal functions to load directory of RDF data. |
|**Status:**                   |Supported in Plugins v2.X. Updated to use Plugin-DevEnv v2.X.       |
|                              |                                                               |
|**DPU class name:**           |VirtuosoLoader     | 
|**Configuration class name:** |VirtuosoLoaderConfig_V1                           |
|**Dialogue class name:**      |VirtuosoLoaderVaadinDialog | 

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Virtuoso JDBC URL:** |URL for establishing JDBC session with Virtuoso server.  |
|**Username:** |Username for Virtuoso server. |
|**Password:** |Password for the username. |
|**Clear destination graph before loading (checkbox):** |Self-descriptive  |
|**Directory to load path:**|Path to directory to be loaded. |
|**Include subdirectories:**|Boolean value setting (accepts 'true' or 'false'). |
|**File name pattern:**|A pattern for file names to be included. | 
|**Target Graph:**|Target graph URI. |
|**Update status interval (s):**|Time period between status updates (in seconds). |
|**Thread count:**|TODO: provide description |
|**Skip file on error (checkbox):**|Do not stop the pipeline when error occurs (if checked). |


***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|TODO: provide Name, Dataunit and Description of input |i |  |  |

***

### Version history ###

|Version            |Release notes                                   |
|-------------------|------------------------------------------------|
|2.2.0              | Added support for RDF configuration for some properties. |
|2.1.0              | Update to API 2.1.0        |
|2.0.1              | fixes in build dependencies |
|2.0.0              | Update for new helpers.                        |
|1.7.0              | N/A                                            |                                
|1.6.0              | N/A                                            |                                
|1.5.0              | N/A                                            |                                
|1.4.0              | N/A                                            |                                
|1.3.1              | N/A                                            |                                
|1.3.0              | N/A                                            |                                


***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|N/A               |N/A                   | 

