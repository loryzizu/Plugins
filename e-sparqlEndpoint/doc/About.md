### Description

Extracts RDF statements from the given external SPARQL endpoint using the given SPARQL Construct query. The extracted RDF statements are places into a single output graph. 

The given SPARQL Construct query can be rewritten (automatically, on background) so that results obtained from the given Virtuoso SPARQL endpoint are chunked in order to avoid timeouts, result sets max rows limitations, etc. While rewriting, the outermost ORDER BY and LIMIT (if any) are thrown away. Supported by Virtuoso SPARQL endpoints.

### Configuration parameters

| Name | Description |
|:----|:----|
|**SPARQL Endpoint URL** | URL of SPARQL endpoint to extract data from |
|**SPARQL Construct** | SPARQL construct used to extract data |
|**Chunk Size:** | The given query can be rewritten (automatically, on background) so that results obtained from the given Virtuoso SPARQL endpoint are chunked in order to avoid timeouts, result sets max rows limitations, etc. This option allows you to specify the chunk size of such chunks. |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|output |o |RdfDataUnit |Extracted RDF statements |x|
