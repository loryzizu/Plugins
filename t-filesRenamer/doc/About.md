### Description

Renames files based on pattern defined in configuration.

### Configuration parameters

| Name | Description |
|:----|:----|
|**Pattern** | Regular expression used to match string to replace in file name. This value is used as a replace part (second argument) in SPARQL REPLACE |
|**Value to substitute** | Value to substitute, can refer to groups that have been matched by 'Pattern' parameter. This value is used as a substitute part (third argument) in SPARQL REPLACE |
|**Advanced mode (checkbox)** | if checked the user given value is considered an expression instead of a string |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|inFilesData  |i| FilesDataUnit | File name to be modified |x|
|outFilesData |o| FilesDataUnit | File name after modification |x|