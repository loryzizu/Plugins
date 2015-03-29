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
|**Configuration class name:** |TabularToRelational_V1                       |
|**Dialogue class name:**      |TabularToRelationalVaadinDialog              |

***

###Configuration parameters###

|Parameter                                       |Description                                                                          |
|------------------------------------------------|-------------------------------------------------------------------------------------|
|**Tablename:                                    |Name of table that will hold the parsed data.                                        |
|**Tablename:                                    |Encoding of input file.                                                              |
|**Rows limit:                                   |Maximum number of processed lines from input file.                                   |
|**Field delimiter:                              |Field delimiter to use during parsing od CSV file. e.g. '"'.                         |
|**Fields sepatator:                             |Field separator to use during parsing of CSV file. e.g. ','.                         |
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
|1.0.0   |N/A           |                                

***

### Developer's notes ###

|Author |Notes |
|-------|------|
|N/A    |N/A   | 
