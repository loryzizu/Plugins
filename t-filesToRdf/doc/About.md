### Description

Extracts RDF data from Files (any file format). Output RDF data unit contains one entry for each input file (so every input file results in one data graph with RDF data). 

### Configuration parameters

| Name | Description |
|:----|:----|
|**Commit size** | 0 = one file, one transaction, 1 = autocommit connection, n = commit every n triples |
|**What to do if extraction on a single file fail** | Stop execution OR Skip and continue |
|**Policy for output symbolic name selection** | Use input files symbolic names OR  Use single fixed symbolic name |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|filesInput |i| FilesDataUnit | Input file containing data |x|
|rdfOutput  |o| RDFDataUnit | RDF data extracted |x|
