### Description

Extracts RDF statements from the given SPARQL endpoint using the given SPARQL Construct query. The extracted RDF statements are places into a single output graph. 

The given SPARQL Construct query can be rewritten (automatically, on background) so that results obtained from the given Virtuoso SPARQL endpoint are chunked in order to avoid timeouts, result sets max rows limitations, etc. While rewriting, the outermost ORDER BY and LIMIT (if any) are thrown away. Supported by Virtuoso SPARQL endpoints.

### Configuration parameters

| Name | Description |
|:----|:----|
|**SPARQL Endpoint URL** | URL of the SPARQL endpoint the data should be extracted from |
|**SPARQL Construct** | SPARQL Construct query used to extract data |
|**Chunk Size:** | The given query can be rewritten (automatically, on background) so that results obtained from the given Virtuoso SPARQL endpoint are chunked in order to avoid timeouts, result sets max rows limitations, etc. In case the query should be rewritten, this option allows to specify the size of these chunks. |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|output |o |RdfDataUnit |Extracted RDF statements |x|
