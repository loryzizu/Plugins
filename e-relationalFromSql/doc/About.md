### Description

Extracts data from external relational database tables into internal database

Can be used to extract data from relational database using SQL queries and stores them into internal data unit relational database.
This DPU provides some features for extracting from database: list of tables in database, generating SELECT for selected table, data preview.
Secured connection to source database via SSL is supported

DPU currently supports these databases:
* PostgreSQL
* Oracle
* MySQL
* Microsoft SQL

### Configuration parameters

| Name | Description |
|:----|:----|
|**Database type:** |Database type: PostgreSQL, MySQL, MS SQL, optionally ORACLE |
|**Database host:** |Database host |
|**Database port:** |Database port |
|**Database name:** |Database name (for ORACLE SID name) |
|**Instance name:** | *(optional)* Name of database instance - ONLY FOR MS SQL |
|**User name:** |Database user name |
|**User password:** |Database password |
|**Connect via SSL:** |Use secured connection to the database |
|**Truststore location:** |*(optional)* Path to truststore file to be used to validate server's certificate. If not filled, default Java truststore is used |
|**Truststore password:** |*(optional)* Truststore password |
|**SQL query:** |SQL query to extract data from source database |
|**Target table name:** |Table name used to internally store the extracted data |
|**Primary key columns:** |*(optional)* Target table primary key |
|**Indexed columns:** |*(optional)* Target table indexed columns |

### Inputs and outputs ###

|Name |Type | DataUnit | Description | Mandatory |
|:---|:---:|:---:|:---|:---:|
|outputTables |o    |RelationalDataUnit |Extracted database tables |x|
