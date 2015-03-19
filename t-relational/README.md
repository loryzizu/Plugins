# T-Relational #
----------

###General###

|                              |                                                                              |
|------------------------------|------------------------------------------------------------------------------|
|**Name:**                     |T-Relational                                                                  |
|**Description:**              |Transforms N input tables into 1 output table using SELECT SQL queries        |
|                              |                                                                              |
|**DPU class name:**           |Relational                                                                    | 
|**Configuration class name:** |RelationalConfig_V1                                                           |
|**Dialogue class name:**      |RelationalVaadinDialog                                                        |
|**WARNING:**                  |This DPU is a part of UV optional functionality (relational data) and in      |
|                              |current implementation it does not fully follow the UV philosophy as user     |
|                              |has control of physical database tables. See details in DPU class             |

***

###Configuration parameters###

|Parameter                           |Description                                                              |
|------------------------------------|-------------------------------------------------------------------------|
|**SQL query:**                      |SQL query to extract data from source database                           |
**Target table name:**               |Table name used to internally store the extracted data                   |

***

### Inputs and outputs ###

|Name           |Type           |DataUnit           |Description                                  |
|---------------|---------------|-------------------|---------------------------------------------|
|inputTables    |i              |RelationalDataUnit |Source database tables                       |
|outputTable    |o              |RelationalDataUnit |Output (transformed) table                   |

***

### Version history ###

|Version          |Release notes               |
|-----------------|----------------------------|
|0.9.1-SNAPSHOT   |Update to helpers 2.0.0     |
|0.9.0            |N/A                         |


***

### Developer's notes ###

|Author           |Notes                           |
|-----------------|--------------------------------|
|N/A              |N/A                             | 
