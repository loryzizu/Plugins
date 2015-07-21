### Description

Uploads list of files to Parliament store using bulk HTTP interface.

### Configuration parameters

| Name | Description |
|:----|:----|
|**Bulk upload endpoint URL***|Bulk upload endpoint URL for Parliament store (e.g. http://localhost:8080/parliament/bulk/insert)|
|**RDF format**|RDF format of input files (to instruct Parliament loader).|
|**Clear destination graph before loading**| Clear destination graph before loading new data. Otherwise new data are only appended to existing data. Default: false.|

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|filesInput|i|FilesDataUnit|Files to upload to specified destination.|x|
