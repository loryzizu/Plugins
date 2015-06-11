# T-UnZipper #
----------

###General###

|                              |                                                  |
|------------------------------|--------------------------------------------------|
|**Name:**                     |T-UnZipper                                        |
|**Description:**              |UnZips input file into files based on zip content. |
|**Status:**                   |Supported in Plugins v2.X. Updated to use Plugin-DevEnv v2.X.       |
|                              |                      |
|**DPU class name:**           |UnZipper              | 
|**Configuration class name:** |UnZipperConfig_V1     |
|**Dialogue class name:**      |UnZipperVaadinDialog  |

***

###Configuration parameters###

|Parameter                                       |Description                                                              |                                                        
|------------------------------------------------|-------------------------------------------------------------------------|
|**Do not prefix symbolic name (checkbox):**     |If checked then output symbolic names of output files are not prefixed with symbolic name of unzipped file. Uncheck to prevent symbolic names collision if multiple zip files with same structure are unzipped. If you do not know, then uncheck this. | 

***

### Inputs and outputs ###

|Name    |Type           |DataUnit      |Description            |
|--------|---------------|--------------|-----------------------|
|input   |i              |FilesDataUnit |File to unzip.          |
|output  |o              |FilesDataUnit |List of unzipped files. |

***

### Version history ###

|Version          |Release notes               |
|-----------------|----------------------------|
|2.1.0            | Update to API 2.1.0        |
|2.0.1            | fixes in build dependencies |
|2.0.0            |Update for helpers 2.0.     |
|1.5.0            |N/A                          |
|1.4.0            |N/A                          |
|1.3.1            |N/A                          |
|1.0.0            |N/A                          |

***

### Developer's notes ###

|Author           |Notes                           |
|-----------------|--------------------------------|
|N/A              |N/A                             |
|Petr Škoda       |VirtualPath is required.        |
