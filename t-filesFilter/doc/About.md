### Description

Filters files

### Configuration parameters

| Name | Description |
|:----|:----|
|**Used filter:**|There are two filters to perform filtering on: <BR> - symbolic name <BR> - virtual path|
|**Custom predicate:***|Filter pattern, for example '.*csv'.|
|**Use regular expression: (checkbox)**|If checked, regular expressions are allowed in filter pattern.|

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input|i|FilesDataUnit|List of files to be filtered||
|output|o|FilesDataUnit|List of files passing the filter||