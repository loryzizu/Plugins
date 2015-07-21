# T-Tabular #
----------

###General###

|                              |                                                                             |
|------------------------------|-----------------------------------------------------------------------------|
|**Name:**                     |T-Tabular                                                               |
|**Description:**              |Converts tabular data into RDF data. |
|                              |                                                                             |
|**DPU class name:**           |Tabular                                                                 | 
|**Configuration class name:** |TabularConfig_V1                             |
|**Dialogue class name:**      |TabularVaadinDialog                      |

***

###Configuration parameters###

|Parameter                                       |Description                                                              |                                                        
|------------------------------------------------|-------------------------------------------------------------------------|


***

### Inputs and outputs ###

|Name         |Type           |DataUnit     |Description             |
|-------------|---------------|-------------|------------------------|
|table        |i              |FilesDataUnit|Input file containing tabular data. |  
|triplifiedTable  |o          |RDFDataUnit  |RDF data. |

***

### Version history ###

|Version          |Release notes               |
|-----------------|----------------------------|
|2.2.1 | Fixed bug with missing trailing columns in xls-like files with empty leading columns and header autogeneration. |
|2.2.0 | Added possibility to strip null value in header. For XSL: If row is shorter than header then row is expanded. |
|2.1.0            | Update to API 2.1.0.Combobox for Encoding in Dialog |
|                 | Fixed bug with :Skip n first lines: for XLS, where empty text box makes configuration invalid. |
|                 | Added option "Generate labels". |
|                 | Fixed bug with wrong initial column. First column wrongly named as "col2" instead of "col1". |
|2.0.1 | fixes in build dependencies |
|2.0.0            |Replaced with the DPU taken from the repository https://github.com/mff-uk/DPUs.|
|1.5.0            |N/A                         |
|1.4.0            |N/A                         |
|1.3.1            |N/A                         |
|1.0.0            |N/A                         |

***

### Developer's notes ###

|Author           |Notes                           |
|-----------------|--------------------------------|
|N/A              |N/A                             | 

***

### Runtime dependencies ###
t-Tabular requires runtime dependencies that must be presented in module/lib folder of UnifiedViews prior
the t-Tabular usage. Those dependencies can be found in CUNI repository (https://github.com/mff-uk/DPUs).

|Library          |
|-----------------|
|https://github.com/mff-uk/DPUs/blob/master/dpu/t-tabular/libs/org.apache.poi-bundle-3.10.0.jar |
|https://github.com/mff-uk/DPUs/blob/master/dependencies/org.junit-bundle-4.11.0.jar |


