### Description

UnZips input file into files based on zip content.

### Configuration parameters

| Name | Description |
|:----|:----|
|**Duplicate name prevention. (checkbox)** |If checked DPU prevents collision in names of files send on output when multiple zip files with the same/similar structure are unzipped. Uncheck sends on output files names as are in zip files. If not sure keep checked |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |i| FilesDataUnit | File to unzip |x|
|output |o| FilesDataUnit | List of unzipped files |x|
