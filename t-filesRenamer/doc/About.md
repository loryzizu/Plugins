### Description

Renames files

### Configuration parameters

| Name | Description |
|:----|:----|
|**Pattern**|Regular expression used to match string to replace in file name. This value is used as a replace part (second argument) in SPARQL REPLACE.|
|**Value to substitute**|Value to substitute, can refer to groups that have been matched by 'Pattern' parameter. This value is used as a substitute part (third argument) in SPARQL REPLACE.|

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|inFilesData|i|FilesDataUnit|File name to be modified.|x|
|outFilesData|o|FilesDataUnit|File name after modification.|x|