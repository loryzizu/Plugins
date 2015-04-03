# E-FilesDownload #
----------

###General###

|                              |                                                                             |
|------------------------------|-----------------------------------------------------------------------------|
|**Name:**                     |E-FilesDownload                                                              |
|**Description:**              |Downloads list of files. Replaces E-FilesFromLocal and E-HttpDownload.       |
|**Status:**                   |Supported in Plugins v2.X. Updated to use Plugin-DevEnv v2.X.                |
|                              |                                                                             |
|**DPU class name:**           |FilesDownload                                                                | 
|**Configuration class name:** |FilesDownloadConfig_V1                                                       |
|**Dialogue class name:**      |FilesDownloadVaadinDialog                                                    |

***

###Configuration parameters###

|Parameter                                       |Description                                                                        |
|------------------------------------------------|-----------------------------------------------------------------------------------|
**List of files and directories to extract:***   |If directory is provided then all files and files in subdirectories are extracted. |

***

### Inputs and outputs ###

|Name         |Type           |DataUnit      |Description             |
|-------------|---------------|--------------|------------------------|
|output  |o              |FilesDataUnit |Downloaded files.       |

***

### Version history ###

|Version          |Release notes               |
|-----------------|----------------------------|
|2.0.0            |Update to helpers 2.0.0                        |
|1.0.0            |N/A                        |


***

### Developer's notes ###

|Author           |Notes                           |
|-----------------|--------------------------------|
|N/A              |N/A                             | 
