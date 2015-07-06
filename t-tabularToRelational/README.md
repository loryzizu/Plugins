# T-TabularToRelational #
----------

###General###

|                              |                                             |
|------------------------------|---------------------------------------------|
|**Name:**                     |T-TabularToRelational                        |
|**Description:**              |Parse tabular file to relational data unit.  |
|**Status:**                   |Supported in Plugins v2.X. Updated to use Plugin-DevEnv v2.X.       |
|                              |                                             |
|**DPU class name:**           |TabularToRelational                          | 
|**Configuration class name:** |TabularToRelational_V2                       |
|**Dialogue class name:**      |TabularToRelationalVaadinDialog              |

***

###Configuration parameters###

|Parameter                                       |Description                                                                          |
|------------------------------------------------|-------------------------------------------------------------------------------------|
|**Tablename:                                    |Name of table that will hold the parsed data.                                        |
|**Filetype:                                     |Type of input file (XLS/X, CVS, DBF).                                                |
|**Data begins at row:                           |Specifies, at which row of input file begins data part.                              |
|**Charset:                                      |Charset of input file.                                                               |
|**Quote character:                              |Field delimiter to use during parsing od CSV file. e.g. '"'.                         |
|**Sepatator:                                    |Field separator to use during parsing of CSV file. e.g. ','.                         |
|**Table of column mappings                      |Name and types of culumns in CSV files. Also marks if column is used as primary key. |

***

### Inputs and outputs ###

|Name    |Type           |DataUnit          |Description                          |
|--------|---------------|------------------|-------------------------------------|
|input   |i              |FilesDataUnit     |List of files to parse.              |
|output  |o              |RelationalDataUnit|Relational dataunit with parsed data.|   

### Version history ###

|Version |Release notes |
|--------|--------------|
|2.1.0   | improved user experience; Update to API 2.1.0; added new parsers(XLS, XLSX, DBF) |
|2.0.1   | fixes in build dependencies |
|2.0.0   | Update to helpers 2.0.0 |
|1.0.0   | N/A           |                                

***

### Developer's notes ###

|Author |Notes |
|-------|------|
|N/A    |N/A   | 
