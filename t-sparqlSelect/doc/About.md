### Description

Transforms SPARQL SELECT query result to CSV. Does not validate query.

### Configuration parameters

| Name | Description |
|:----|:----|
|**Target path*** | Path and target CSV file name |
|**SPARQL query**| Text area dedicated for SPARQL SELECT query | 


### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |i | RDFDataUnit   | RDF graph |x|
|output |o | FilesDataUnit | CSV file containing SPARQL SELECT query result |x|
