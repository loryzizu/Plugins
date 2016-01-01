### Description

Filters files based on filter pattern defined in configuration. Regular expressions are also supported.

### Configuration parameters

| Name | Description |
|:----|:----|
|**File path pattern** | Filter files based on certain pattern, for example '.*csv'. This filter pattern is applied to the symbolicName, which is set based on virtualPath (for explanation of symbolic names, virtualPath, please see [here](https://grips.semantic-web.at/display/UDDOC/Basic+Concepts+for+DPU+developers)) |
|**Use regular expression (checkbox)** | If checked, regular expressions are allowed in filter pattern. The regular expression is NOT applied to the file name, but it is applied to the symbolicName, which is set based on virtualPath (for explanation of symbolic names, virtualPath, please see [here](https://grips.semantic-web.at/display/UDDOC/Basic+Concepts+for+DPU+developers)). The syntax for regular expressions is as described [here](http://www.w3.org/TR/xpath-functions/#regex-syntax) |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |i| FilesDataUnit | List of files to be filtered |x|
|output |o| FilesDataUnit | List of files passing the filter |x|
