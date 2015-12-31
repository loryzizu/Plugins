### Description

Unzips GZIP files

### Configuration parameters

| Name | Description |
|:----|:----|
|Skip file on error| If checked, in case processing of the input archive ends with error, the archive is skipped, warning is logged, and the execution of the DPU continues with processing of other archives. If unchecked, in case of an error in processing one of the input archives, the DPU ends with error.  |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|filesInput  |i| FilesDataUnit | DataUnit to that will contain files to extract |x|
|filesOutput |o| FilesDataUnit | DataUnit containing extracted files |x|
