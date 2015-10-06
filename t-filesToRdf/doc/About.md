### Description

Extracts RDF data from Files (any file format). Output RDF data unit contains one entry for each input file (so every input file results in one data graph with RDF data). 

By default, RDF format of the input files is estimated automatically based on the extension of the file name. If that does not work for you, please specify the RDF format of the input files explicitely.

### Configuration parameters

| Name | Description |
|:----|:----|
|**RDF format of the input files** | RDF format of the data in the input files. AUTO = automatic selection of RDF format of the input files (default) |
|**Commit size** | 0 = one file, one transaction, 1 = autocommit connection, n = commit every n triples |
|**What to do if extraction on a single file fail** | Stop execution OR Skip and continue |
|**Policy for output symbolic name selection** | Use input files symbolic names OR  Use single fixed symbolic name |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|filesInput |i| FilesDataUnit | Input file containing data |x|
|rdfOutput  |o| RDFDataUnit | RDF data extracted |x|
