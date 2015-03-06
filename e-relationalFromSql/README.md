# E-RelationalFromSql #
----------

###General###

|                              |                                                                             |
|------------------------------|------------------------------------------------------------------------------|
|**Name:**                     |E-RelationalFromSql                                                           |
|**Description:**              |Extracts data from external relational database tables into internal database |
|                              |                                                                              |
|**DPU class name:**           |RelationalFromSql                                                             | 
|**Configuration class name:** |RelationalFromSqlConfig_V1                                                    |
|**Dialogue class name:**      |RelationalFromSqlVaadinDialog                                                 |
|**WARNING:**                  |This DPU is a part of UV optional functionality (relational data) and in      |
|                              |current implementation it does not fully follow the UV philosophy as user     |
|                              |has control of physical database tables. See details in DPU class             |

***

###Configuration parameters###

|Parameter                           |Description                                                              |
|------------------------------------|-------------------------------------------------------------------------|
|**Database URL:**                   |JDBC URL of source database (currently only PostgreSQL supported)        |
|**User name:**                      |Database user name                                                       |
|**User password:**                  |Database password                                                        |
|**Connect via SSL:**                |Use secured connection to the database                                   |
|**SQL query:**                      |SQL query to extract data from source database                           |
**Target table name:**               |Table name used to internally store the extracted data                   |

***

### Inputs and outputs ###

|Name           |Type           |DataUnit           |Description                                  |
|---------------|---------------|-------------------|---------------------------------------------|
|outputTables   |o              |RelationalDataUnit |Extracted database tables                    |

***

### Version history ###

|Version          |Release notes               |
|-----------------|----------------------------|
|1.0.0-SNAPSHOT   |N/A                         |


***

### Developer's notes ###

|Author           |Notes                           |
|-----------------|--------------------------------|
|N/A              |N/A                             | 
