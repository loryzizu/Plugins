# E-DistributionMetadata #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |E-DistributionMetadata                                              |
|**Description:**              |Generates CKAN Resource model for distribution metadata on output. |
|                              |                                                               |
|**DPU class name:**           |DistributionMetadata     | 
|**Configuration class name:** |DistributionMetadataConfig_V1                           |
|**Dialogue class name:**      |DistributionMetadataVaadinDialog | 

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Distribution title in original language:** | A name given to the distribution.  |
|**Description in original language:** | free-text account of the distribution.  |
|**Format:** | The file format of the distribution.  |
|**Media (MIME) type:** | The media type of the distribution as defined by IANA. |
|**Issued:** | Date of formal issuance (e.g., publication) of the distribution. |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|distributionOutput |o |RDFDataUnit  | Descriptive data. Resource attached to a graph with symbolic name "distributionMetadata"  |

***

### Version history ###

|Version            |Release notes                                   |
|-------------------|------------------------------------------------|
|2.0.0-SNAPSHOT              |Adopted by UV/Plugins                                             |                                
|1.3.1              |N/A                                             |                                
|1.5.1              |Update for new helpers.                         | 

***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|N/A               |N/A                   | 

