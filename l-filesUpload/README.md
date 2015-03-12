# L-FilesUpload #
----------

###General###

|                              |                                                                             |
|------------------------------|-----------------------------------------------------------------------------|
|**Name:**                     |L-FilesUpload                                                                |
|**Description:**              |Uploads list of files. Replaces L-FilesToLocalFS and L-FilesToScp.           |
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

***

### Version history ###

|Version          |Release notes               |
|-----------------|----------------------------|
|1.0.0-SNAPSHOT   |N/A                         |
|1.0.1            |Update for new helpers. Input/output dataUnits names updated. |

***

### Developer's notes ###

|Author           |Notes                           |
|-----------------|--------------------------------|
|N/A              |N/A                             | 
|Petr Škoda       |VirtualPath is required.        |