### Description

Validates XML inputs in 3 ways:
* checks if the XML is correctly formated
* checks if it conforms to a specified XSD scheme
* validates using specified XSLT template

### Configuration parameters

| Name | Description |
|:----|:----|
|**XSD schema**|XSD schema to validate the input files against|
|**XSLT transformation***|Any output that the XSLT produces is considered an error message causing the validation failed|
|**Fail pipeline on first validation error**|If first validation error is detected, DPU will stop pipeline|

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input|i|FilesDataUnit|List of files to be validated|x|
|outputValid|o|FilesDataUnit|List of files having passed validation||
|outputInalid|o|FilesDataUnit|List of files failed validation||