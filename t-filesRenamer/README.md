# T-FilesRenamer #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |T-FilesRenamer                                              |
|**Description:**              |Renames files. |
|                              |                                                               |
|**DPU class name:**           |Renamer     | 
|**Configuration class name:** |RenameConfig_V2                           |
|**Dialogue class name:**      |RenameVaadinDialog | 

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|mask          |Mask used in renaming of filename.  |
|extensionMask |Mask used in renaming of exatension.  |
|counterStart  |Counter setting. Initial value of counter.  |
|counterStep   |Counter setting. Difference between previous counter value and next.  |
|counterDigits |Counter settings. Number of digits used in converting counter value to string.  |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|inFilesData  |i |FilesDataUnit  |File name to be modified.  |
|outFilesData |o |FilesDataUnit  |File name after modification. | 

***

### Version history ###

|Version    |Release notes                                   |
|-----------|------------------------------------------------|
|1.5.0      |Added support for renaming against the mask.    |                                
|1.4.0      |Small bug fixes.                                |                                
|1.5.0      |Initial version, appends .ttl extension.        |                                


***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|N/A               |N/A                   | 

