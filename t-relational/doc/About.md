### Description

Transforms N input tables into 1 output table using SQL SELECT query

### Configuration parameters

| Name | Description |
|:----|:----|
|**SQL query** | SQL SELECT query to extract/join/transform data from input tables |
|**Target table name** | Name of output table - the table where the results of sql query will be stored |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|inputTables |i| RelationalDataUnit | Input tables |x|
|outputTable |o| RelationalDataUnit | Output table |x|
