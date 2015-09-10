### Description

Creates links between RDF resources based on the Silk Link Specification Language (LSL), https://www.assembla.com/spaces/silk/wiki/Link_Specification_Language. The script containing the specification of the linkage task may be uploaded from an XML file or directly copied to the DPU configuration. Output section of such script is always ignored, output is written to two output data units of the DPU - "links_confirmed", "links_to_be_verified". DPU configuration must also specify thresholds for the two outputs created - thresholds for links which are confirmed and which should be verified. 

### Configuration parameters

| Name | Description |
|:----|:----|
|**Silk library location** | Path to the executable Silk JAR file, as Silk is executed as an external process |
|**Silk configuration file** | XML configuration file for Silk. It may be uploaded from a file or directly inserted to the text area below |
|**Minimum score for links considered as confirmed links** | Please specify minimum score (in the range 0-1) for the links to be considered as confirmed links (which are probably correct) |
|**Minimum score for links considered as to be verified links** | Please specify minimum score (in the range 0-1) for the links which may be correct, but typically requires verification. Such score must be lower than ‘Minimum score for links considered as confirmed links’ |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|links_confirmed |o |RdfDataUnit |Confirmed links |x|
|links_to_be_verified |o |RdfDataUnit |Links to be verified | |
