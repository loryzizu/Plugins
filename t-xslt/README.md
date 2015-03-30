# T-Xslt #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |T-Xslt                                              |
|**Description:**              |Does XSL Transformation over files and outputs Files |
|**Status:**                   |Supported in Plugins v2.X. Updated to use Plugin-DevEnv v2.X.       |
|                              |                                                               |
|**DPU class name:**           |Xslt     | 
|**Configuration class name:** |XsltConfig_V1                           |
|**Dialogue class name:**      |XsltVaadinDialog | 

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|


***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|files  |i |FilesDataUnit  |File to be transformed.  |
|files |o |FilesDataUnit  |Transformed file of given type.  |
|config |i |RDFDataUnit   | Configuration (template parameters). |

***

### Version history ###

|Version            |Release notes                                   |
|-------------------|------------------------------------------------|
|1.5.1              |fix in localization                             |
|1.5.0              |N/A                                             |
|1.3.2              |N/A                                             |                                
|2.0.0              |Replaced with version from CUNI repository.     |

***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|N/A               |N/A                   | 
|Petr Škoda        |DPU fail if virtual path is not set. |

