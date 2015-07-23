### Description

Transforms SPARQL SELECT query result to CSV (without validation)

### Configuration parameters

| Name | Description |
|:----|:----|
|**Target path***|path and target CSV file name|
|**SPARQL query**|text area dedicated for SPARQL SELECT query|

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input|i|RDFDataUnit|RDF graph|x|
|output|o|FilesDataUnit|CSV file containing SPARQL SELECT query result|x|