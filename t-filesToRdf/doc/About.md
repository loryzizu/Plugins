### Description

Extracts RDF data from Files (any file format) and adds them to RDF

### Configuration parameters

| Name | Description |
|:----|:----|
|**Commit size**|0 = one file, one transaction, 1 = autocommit connection, n = commit every n triples|
|**Symbolic name**||
|**Line format**||
|**File format**||

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|filesInput|i|FilesDataUnit|Input file containing data.|x|
|rdfOutput|o|RDFDataUnit|RDF data extracted.|x|