### Description

Transforms RDF graphs into files

### Configuration parameters

| Name | Description |
|:----|:----|
|**RDF format (list)** | Output file format:<BR>- Turtle<BR>- RDF/XML<BR>- N-Triples<BR>- N3<BR>- RDFa |
|**Generate graph file (checkbox)** | Is graph file desired? |
|**Output graph name** | Name of the output graph (if graph file is desired) |
|**File path/name without extension** | self-descriptive |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |i| RDFDataUnit   | RDF graph |x|
|output |o| FilesDataUnit | File containing RDF triples |x|
