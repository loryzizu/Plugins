### Description

Generates CKAN Resource model for distribution metadata on output.

### Configuration parameters

| Name | Description |
|:----|:----|
|**Distribution title in original language** | A name given to the distribution.  |
|**Description in original language** | free-text account of the distribution.  |
|**Format** | The file format of the distribution.  |
|**Media (MIME) type** | The media type of the distribution as defined by IANA |
|**Issued** | Date of formal issuance (e.g., publication) of the distribution |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|distributionOutput |o |RDFDataUnit | Descriptive data. Resource attached to a graph with symbolic name "distributionMetadata" |x|
