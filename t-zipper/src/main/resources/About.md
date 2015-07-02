### Descritpion

Zips input files into zip file of given name.

### Configuration parameters

| Name | Description |
|:----|:----|:----|
|**Zip file path/name (with extension):** | Specifies the path/name for the output file to be created. Given path/name must be relative ie. `/data.zip`, `/data/out.zip`. Absolute path like `c:/` must not be used. In case unix system `/dir/data.zip` is interpreted as a relative path. |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input   |i| FilesDataUnit | List of files to zip |x|
|output  |o| FilesDataUnit | Name of zip file |x|


