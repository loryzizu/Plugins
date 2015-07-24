### Description

Extracts RDF statements from external SPARQL endpoint using SPARQL query defined in configuration into a single output graph.

### Configuration parameters

| Name | Description |
|:----|:----|
|**Enpoint URL** | URL of SPARQL endpoint to extract data from |
|**SPARQL Construct** | SPARQL construct used to extract data |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|output |o |RdfDataUnit |Extracted RDF statements |x|
