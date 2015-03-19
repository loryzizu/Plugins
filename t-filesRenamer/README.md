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
|||
|||
|||

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
|2.0.0      |Update for new helpers. SPARQL used to transform file name. Broken backward compatibility. |
|1.5.0      |Added support for renaming against the mask.    |                                
|1.4.0      |Small bug fixes.                                |                                
|1.5.0      |Initial version, appends .ttl extension.        |                                


***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|N/A               |N/A                   | 

