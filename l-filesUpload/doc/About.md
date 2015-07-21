### Description

Uploads list of files to defined location.

### Configuration parameters

| Name | Description |
|:----|:----|
|**Destination directory absolute path***|Destination path for files to upload.|
|**Username:**|Username for destination host.|
|**Password:**|Password for the username.|

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input|i|FilesDataUnit|Files to upload to specified destination.|x|
|output|o|FilesDataUnit|Same as input, only Resource.last_modified time updated.||