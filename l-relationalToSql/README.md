# L-RelationalToSql #
----------

###General###

|                              |                                                                                                 |
|------------------------------|-------------------------------------------------------------------------------------------------|
|**Name:**                     |L-RelationalToSql                                                                                |
|**Description:**              |Loads input internal database tables into external SQL database (currently PostgreSQL) supported |
|**Status:**                   |Supported in Plugins v2.X. Updated to use Plugin-DevEnv v2.X.       |
|                              |                                                                                                 |
|**DPU class name:**           |RelationalToSql                                                                                  | 
|**Configuration class name:** |RelationalToSqlConfig_V1                                                                         |
|**Dialogue class name:**      |RelationalToSqlVaadinDialog                                                                      |
|**WARNING:**                  |This DPU is a part of UV optional functionality (relational data) and in                         |
|                              |current implementation it does not fully follow the UV philosophy as user                        |
|                              |has control of physical database tables.                                                         |

***

###Configuration parameters###

|Parameter                           |Description                                                              |
|------------------------------------|-------------------------------------------------------------------------|
|**Database URL:**                   |JDBC URL of source database (currently only PostgreSQL supported)        |
|**User name:**                      |Database user name                                                       |
|**User password:**                  |Database password                                                        |
|**Connect via SSL:**                |Use secured connection to the database                                   |
|**Target table prefix:**            |Table name used to internally store the extracted data                   |
|**Clear table before insert:**      |Truncate table before insert                                             |
|**Recreate table:**                 |Table is dropped if exists

***

### Inputs and outputs ###

|Name           |Type           |DataUnit           |Description                                  |
|---------------|---------------|-------------------|---------------------------------------------|
|inTablesData   |i              |RelationalDataUnit |Input database tables                        |

***

### Version history ###

|Version          |Release notes               |
|-----------------|----------------------------|
|2.0.0            |Update for helpers 2.0.0    |
|0.9.0            |N/A                         |


***

### Developer's notes ###

|Author           |Notes                           |
|-----------------|--------------------------------|
|N/A              |N/A                             | 
