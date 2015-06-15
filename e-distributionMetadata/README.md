# E-DistributionMetadata #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |E-DistributionMetadata                                              |
|**Description:**              |Generates DCAT distribution metadata on output. |
|                              |                                                               |
|**DPU class name:**           |DistributionMetadata     | 
|**Configuration class name:** |DistributionMetadataConfig_V1                           |
|**Dialogue class name:**      |DistributionMetadataVaadinDialog | 

***

###Configuration parameters###


|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|**Access URL:** | A landing page, feed, SPARQL endpoint or other type of resource that gives access to the distribution of the dataset  |
|**Description in original language:** | free-text account of the distribution.  |
|**Format:** | The file format of the distribution.  |
|**License:** | This links to the license document under which the catalog is made available and not the datasets. Even if the license of the catalog applies to all of its datasets and distributions, it should be replicated on each distribution. |
|**Download URL:** | A file that contains the distribution of the dataset in a given format |
|**Media (MIME) type:** | The media type of the distribution as defined by IANA. |
|**Sparql Endpoint URI:** |self-descriptive  |
|**Issued:** | Date of formal issuance (e.g., publication) of the distribution. |
|**Distribution title in original language:** | A name given to the distribution.  |
|**Described by:** | URL to the data dictionary for the distribution found at the downloadURL. Note that documentation other than a data dictionary can be referenced using Related Documents as shown in the expanded fields. |
|**Description original language:** | The machine-readable file format (IANA Media Type or MIME Type) of the distribution’s describedBy URL.  |
|**Type of description** | The machine-readable file format (IANA Media Type or MIME Type) of the distribution’s describedBy URL.  |
|**Example resource** | Example resource |


***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|distributionOutput |o |RDFDataUnit  |DCAT-AP Descriptive data.  | 

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

