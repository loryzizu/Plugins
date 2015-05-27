# T-FilterValidXml #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |T-FilterValidXml                                              |
|**Description:**              |Validates XML inputs in 3 ways: checks if the XML is well formed, checks if it conforms to a specified XSD scheme, validate using specified XSLT template. |
|**Status:**                   |Supported in Plugins v2.X. Updated to use Plugin-DevEnv v2.X.       |
|                              |                                                               |
|**DPU class name:**           |FilterValidXml     |
|**Configuration class name:** |FilterValidXmlConfig_V1                           |
|**Dialogue class name:**      |FilterValidXmlVaadinDialog |

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**XSD schema:** |XSD schema to validate the input files against.  |
|**XSLT transformation:*** |XSLT tranformation to validate the input files. Any output that the XSLT produces is considered an error message, and the validation is failed.|
|**Fail pipeline on first validation error. | If first validation error is detected, DPU will stop pipeline. |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|input |i |FilesDataUnit  |List of files to be validated.  |
|outputValid|o |FilesDataUnit |List of files passing the validation. |
|outputInalid|o |FilesDataUnit |List of files that does not pass the validation. |

***

### Version history ###

|Version            |Release notes                                   |
|-------------------|------------------------------------------------|
|2.1.0            | Update to API 2.1.0        |
|2.0.1              | fixes in build dependencies |
|2.0.0              | initial release, uses version 2.0.0 of helpers |                                


***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|N/A               |N/A                   | 

