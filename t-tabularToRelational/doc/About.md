### Description

Parse tabular file to relational data unit.

### Configuration parameters

| Name | Description |
|:----|:----|
|**Tablename** | Name of table that will hold the parsed data |
|**Filetype** | Type of input file (XLS/X, CSV, DBF) |
|**Data begins at row** | Specifies, at which row of input file begins data part |
|**Charset** | Charset of input file |
|**Quote character** | Field delimiter to use during parsing of CSV file. e.g. '"' |
|**Separator** | Field separator to use during parsing of CSV file. e.g. ',' |
|**Table of column mappings** | Name and types of columns in CSV files. Also marks if column is used as primary key |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |i| FilesDataUnit | List of files to parse |x|
|output |o| RelationalDataUnit| Relational data unit with parsed data | x|
