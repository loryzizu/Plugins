### Description

Renames files based on pattern defined in configuration.

### Configuration parameters

| Name | Description |
|:----|:----|
|**Pattern** | Regular expression used to match string to replace in file name. This value is used as a replace part (second argument) in SPARQL REPLACE |
|**Value to substitute** | Value to substitute, can refer to groups that have been matched by 'Pattern' parameter. This value is used as a substitute part (third argument) in SPARQL REPLACE |
|**Use advanced mode** | if checked the user given value to substitute is considered as an expression instead of a string. This enables use of SPARQL functions, but the result must be in the form of string. |

#### List of useful commands with Advanced mode unchecked:

| Action | Pattern | Value to substitue |
|:----|:----|:----|
|Add a suffix ".gml" | ^(.+)$ | $1.gml |
|Rename file to "abc" | ^.+$ | abc |


### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|inFilesData  |i| FilesDataUnit | File name to be modified |x|
|outFilesData |o| FilesDataUnit | File name after modification |x|