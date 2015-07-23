### Description

Transforms RDF graphs into files

### Configuration parameters

| Name | Description |
|:----|:----|
|**RDF format (list)**|<UL><LI>Turtle</LI><LI>RDF/XML</LI><LI>N-Triples</LI><LI>N3</LI><LI>RDFa</LI></UL>|
|**Generate graph file (checkbox)**|Is graph file required?|
|**Output graph name**|Name of the output graph (if graph file is required)|
|**File path/name without extension**|self-descriptive|

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input|i|RDFDataUnit|RDF graph|x|
|output|o|FilesDataUnit|File containing RDF triples|x|