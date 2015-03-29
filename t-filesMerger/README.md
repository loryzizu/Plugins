# T-FilesMerger #
----------

###General###

|                              |                                                                             |
|------------------------------|-----------------------------------------------------------------------------|
|**Name:**                     |T-FilesMerger                                                               |
|**Description:**              |Merges Files inputs. |
|**Status:**                   |Supported in Plugins v2.X. Updated to use Plugin-DevEnv v2.X.       |
|                              |                                                                             |
|**DPU class name:**           |FilesMerger                                                                | 
|**Configuration class name:** |FilesMergerConfig_V1                             |
|**Dialogue class name:**      |FilesMergerVaadinDialog                      |

***

###Configuration parameters###

|Parameter                                       |Description                                                              |                                                        
|------------------------------------------------|-------------------------------------------------------------------------|
|**N/A**                                         |N/A                |

***

### Inputs and outputs ###

|Name         |Type           |DataUnit     |Description             |
|-------------|---------------|-------------|------------------------|
|filesInput     |i              |FilesDataUnit  |DataUnit to which user connects all inputs which has to be merged.  |  
|filesOutput    |o              |FilesDataUnit  |DataUnit which outputs all files from input. |

***

### Version history ###

|Version          |Release notes               |
|-----------------|----------------------------|
|1.3.1            |N/A                         |    
|1.6.1            |Use SPARQL to merge metadata. Moved to plugins repository.|                            


***

### Developer's notes ###

|Author           |Notes                           |
|-----------------|--------------------------------|
|N/A              |N/A                             | 
