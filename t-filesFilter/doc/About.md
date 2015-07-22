### Description

Filters files based on filter pattern defined in configuration. Regular expressions are also supported.

### Configuration parameters

| Name | Description |
|:----|:----|
|**Used filter** | There are two filters to perform filtering on: <br> - symbolic name <br> - virtual path |
|**Custom predicate** | Filter pattern, for example '.*csv' |
|**Use regular expression (checkbox)** | If checked, regular expressions are allowed in filter pattern |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |i| FilesDataUnit | List of files to be filtered |x|
|output |o| FilesDataUnit | List of files passing the filter |x|