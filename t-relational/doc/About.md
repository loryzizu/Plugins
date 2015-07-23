### Description

Transforms N input tables into 1 output table using SELECT SQL queries

### Configuration parameters

| Name | Description |
|:----|:----|
|**SQL query** | SQL query to extract data from source database |
|**Target table name** | Table name used to internally store the extracted data |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|inputTables |i| RelationalDataUnit | Source database tables |x|
|outputTable |o| RelationalDataUnit | Output (transformed) table |x|
