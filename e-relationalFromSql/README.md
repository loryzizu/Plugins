# E-RelationalFromSql #
----------

###General###

|                              |                                                                             |
|------------------------------|------------------------------------------------------------------------------|
|**Name:**                     |E-RelationalFromSql                                                           |
|**Description:**              |Extracts data from external relational database tables into internal database |
|**Status:**                   |Supported in Plugins v2.X. Updated to use Plugin-DevEnv v2.X.       |
|                              |                                                                              |
|**DPU class name:**           |RelationalFromSql                                                             | 
|**Configuration class name:** |RelationalFromSqlConfig_V2                                                    |
|**Dialogue class name:**      |RelationalFromSqlVaadinDialog                                                 |
|**WARNING:**                  |This DPU is a part of UV optional functionality (relational data) and in      |
|                              |current implementation it does not fully follow the UV philosophy as user     |
|                              |has control of physical database tables. See details in DPU class             |

***

###Configuration parameters###

|Parameter                           |Description                                                              |
|------------------------------------|-------------------------------------------------------------------------|
|**Database type:**                  |Database type: PostgreSQL, Oracle, MySQL, MS SQL                         |
|**Database host:**                  |Database host                                                            |
|**Database port:**                  |Database port                                                            |
|**Database name:**                  |Database name (for ORACLE SID name)                                      |
|**Instance name:**                  |Name of database instance (optional) - ONLY FOR MS SQL                   |
|**User name:**                      |Database user name                                                       |
|**User password:**                  |Database password                                                        |
|**Connect via SSL:**                |Use secured connection to the database                                   |
|**Truststore location:**            |Path to truststore file to be used to validate server's certificate      |
|                                    |(optional) If not filled, default Java truststore is used                |
|**Truststore password:**            |Truststore password (optional)                                           |
|**SQL query:**                      |SQL query to extract data from source database                           |
|**Target table name:**              |Table name used to internally store the extracted data                   |
|**Primary key columns:**            |Target table primary key (optional)                                      |
|**Indexed columns:**                |Target table indexed columns (optional)                                  |


***

### Inputs and outputs ###

|Name           |Type           |DataUnit           |Description                                  |
|---------------|---------------|-------------------|---------------------------------------------|
|outputTables   |o              |RelationalDataUnit |Extracted database tables                    |

***

### Version history ###

|Version          |Release notes                |
|-----------------|-----------------------------|
|2.1.0            | Update to API 2.1.0        |
|2.0.2            | Fixes of automatic naming of duplicate columns in SQL select |
|2.0.1            | fixes in build dependencies |
|2.0.0            | New features, user-friendly GUI, support for MS SQL, Oracle, MySQL, Update for helpers 2.0.0 |
|0.9.0            | Version for evaluation, compatible with API v1.3.0 |


***

### Developer's notes ###

|Author           |Notes                           |
|-----------------|--------------------------------|
|eea03            |Manual steps are required to build this project, see [build info](BUILD.md)    | 
