### Description

Validates RDF data

### Configuration parameters

| Name | Description |
|:----|:----|
|**Validation query** | ASK or SELECT SPARQL query.<br>ASK returning True = validation fails,<br>SELECT returning non-zero tuples = validation fails  |
|**Fail execution when validation produce any error** | When checked, DPU will throw an exception when validation fails, causing pipeline to fail.<br>Otherwise, the DPU will just log results and return successfully |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|rdfInput |i| RDFDataUnit | Input RDF to be validated |x|
|rdfOutput|o| RDFDataUnit | Copy of rdfInput data | |
