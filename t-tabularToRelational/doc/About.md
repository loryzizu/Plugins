### Description

Parse tabular file to relational data unit.

### Configuration parameters

| Name | Description |
|:----|:----|
|**Tablename** | Name of table that will hold the parsed data |
|**Filetype** | Type of input file (XLS/X, CVS, DBF) |
|**Data begins at row** | Specifies, at which row of input file begins data part |
|**Charset** | Charset of input file |
|**Quote character** | Field delimiter to use during parsing od CSV file. e.g. '"' |
|**Sepatator** | Field separator to use during parsing of CSV file. e.g. ',' |
|**Validate CSV files**|If checked, input CSV files are validated (row column counts are compared to user defined columns count) and DPU fails if CSV is not valid|
|**Table of column mappings** | Name and types of culumns in CSV files. Also marks if column is used as primary key |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |i| FilesDataUnit | List of files to parse |x|
|output |o| RelationalDataUnit| Relational dataunit with parsed data | x|
