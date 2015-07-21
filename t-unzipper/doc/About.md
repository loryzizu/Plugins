### Description

UnZips input file into files based on zip content.

### Configuration parameters

| Name | Description |
|:----|:----|
|**Do not prefix symbolic name (checkbox)** | If checked then output symbolic names of output files are not prefixed with symbolic name of unzipped file. Uncheck to prevent symbolic names collision if multiple zip files with same structure are unzipped. If you do not know, then uncheck this. |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input|i|FilesDataUnit|File to unzip |x|
|output|o|FilesDataUnit|List of unzipped files |x|
