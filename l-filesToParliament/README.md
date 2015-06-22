# L-FilesToParliament #
----------

###General###

|                              |                                                                             |
|------------------------------|-----------------------------------------------------------------------------|
|**Name:**                     |L-FilesToParliament                                                                |
|**Description:**              |Uploads list of files to Parliament store using bulk HTTP interface.          |
|**Status:**                   |Supported in Plugins v2.X. Updated to use Plugin-DevEnv v2.X.       |
|                              |                                                                             |
|**DPU class name:**           |FilesToParliament                                                                  | 
|**Configuration class name:** |FilesToParliamentConfig_V1                                                         |
|**Dialogue class name:**      |FilesToParliamentVaadinDialog                                                      |

***

###Configuration parameters###

|Parameter                                       |Description                                                              |
|------------------------------------------------|-------------------------------------------------------------------------|
|**Bulk upload endpoint URL***        |Bulk upload endpoint URL for Parliament store (e.g. http://localhost:8080/parliament/bulk/insert)                                 |
|**RDF format**                                   |RDF format of input files (to instruct Parliament loader).    |
|**Clear destination graph before loading**                                   | Clear destination graph before loading new data. Otherwise new data are only appended to existing data. Default: false.   |

***

### Inputs and outputs ###

|Name         |Type           |DataUnit      |Description                               |
|-------------|---------------|--------------|------------------------------------------|
|filesInput   |i              |FilesDataUnit |Files to upload to specified destination. |

***

### Version history ###

|Version          |Release notes                |
|-----------------|-----------------------------|
|1.0.0            | Initial release |

***

### Developer's notes ###

|Author           |Notes                           |
|-----------------|--------------------------------|
|N/A              |N/A                             | 
