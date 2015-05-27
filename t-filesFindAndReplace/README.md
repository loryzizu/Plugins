# T-FilesFindAndReplace #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |L-FilesFindAndReplace                                          |
|**Description:**              |Finds and replaces strings (patterns) in files                 |
|**Status:**                   |Supported in Plugins v2.X. Updated to use Plugin-DevEnv v2.X.       |
|                              |                                                               |
|**DPU class name:**           |FilesFindAndReplace                                            | 
|**Configuration class name:** |FilesFindAndReplaceConfig_V1                                   |
|**Dialog class name:**        |FilesFindAndReplaceVaadinDialog                                | 

***

###Configuration parameters###

|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Search pattern** |Pattern to search for in files |
|**Replace** | String to replace the searched pattern|
|**Skip file on error (checkbox)** | Additional self-descriptive option for load. |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|filesInput |i |FilesDataUnit |Input files to replace strings  |
|filesOutput |o|FilesDataUnit|Output files with replaced strings |

***

### Version history ###

|Version            |Release notes                                   |
|-------------------|------------------------------------------------|
|2.1.0              | Update to API 2.1.0        |
|2.0.0              | Initial release, using helpers 2.0.0           |                                


***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|N/A               |N/A                   | 

