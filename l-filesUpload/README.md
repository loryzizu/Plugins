# L-FilesUpload #
----------

###General###

|                              |                                                                             |
|------------------------------|-----------------------------------------------------------------------------|
|**Name:**                     |L-FilesUpload                                                                |
|**Description:**              |Uploads list of files. Replaces L-FilesToLocalFS and L-FilesToScp.           |
|**Status:**                   |Supported in Plugins v2.X. Updated to use Plugin-DevEnv v2.X.       |
|                              |                                                                             |
|**DPU class name:**           |FilesUpload                                                                  | 
|**Configuration class name:** |FilesUploadConfig_V1                                                         |
|**Dialogue class name:**      |FilesUploadVaadinDialog                                                      |

***

###Configuration parameters###

|Parameter                                       |Description                                                              |
|------------------------------------------------|-------------------------------------------------------------------------|
|**Destination directory absolute path***        |Destination path for files to upload.                                    |
|**Username:**                                   |Username for destination host.                                           |
|**Password:**                                   |Password for the username.                                               |

***

### Inputs and outputs ###

|Name         |Type           |DataUnit      |Description                               |
|-------------|---------------|--------------|------------------------------------------|
|filesInput   |i              |FilesDataUnit |Files to upload to specified destination. |
|output       |o              |FilesDataUnit |Same as input, only Resource.last_modified time updated.|

***

### Version history ###

|Version          |Release notes                |
|-----------------|-----------------------------|
|2.1.0            | Update to API 2.1.0. Output data unit is optional         |
|2.0.1            | fixes in build dependencies |
|2.0.0            | Update for helpers 2.0.0. Input/output dataUnits names updated. |
|1.0.0            | N/A                         |

***

### Developer's notes ###

|Author           |Notes                           |
|-----------------|--------------------------------|
|N/A              |N/A                             | 
|Petr Škoda       |VirtualPath is required.        |
