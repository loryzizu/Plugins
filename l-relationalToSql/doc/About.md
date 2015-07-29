### Description

Loads input internal database tables into external SQL database (currently PostgreSQL) supported.

### Configuration parameters

| Name | Description |
|:----|:----|
|**Database URL** | JDBC URL of source database (currently only PostgreSQL supported) |
|**User name** | Database user name |
|**User password** | Database password |
|**Connect via SSL** | Use secured connection to the database |
|**Target table prefix** | Table name used to internally store the extracted data |
|**Clear table before insert** | Truncate table before insert |
|**Recreate table** | Table is dropped if exists |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|inTablesData |i| RelationalDataUnit | Input database tables |x|
