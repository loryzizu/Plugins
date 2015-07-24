### Description

Does XSL Transformation over input files, using single static template. It outputs Files.

DPU supports random UUID generation using ```randomUUID()``` function in namespace ```uuid-functions```. Example of usage:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    xmlns:uuid="uuid-functions"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="2.0">
    <xsl:template match="/">
        <xsl:value-of select="uuid:randomUUID()"/>
    </xsl:template>
</xsl:stylesheet>
```

### Configuration parameters

| Name | Description |
|:----|:----|
| **Skip file on error** | If selected and transformation fail, then the file is skipped and the execution continues |
| **File extension** | If provided then file extension in virtual path is set to given value.<br>If no virtual path is set for some file then error message is logged and no virtual path is set |
| **Number of extra threads** | How many additional workers should be spawn. Remember that higher number of workers may speed up transformation but will also result in greater memory consumption.<br>This option should work better with files that takes longer to transform. One worker thread is always spawned even if the value is zero |
| **XSLT template** | Template used during transformation |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|files  |i| FilesDataUnit | File to be transformed  |x|
|files  |o| FilesDataUnit | Transformed file of given type |x|
|config |i| RDFDataUnit | Dynamic DPU configuration, see Advanced configuration ||

### Advanced configuration

It is also possible to dynamically configure the DPU over its input `config` using RDF data.

Configuration samples:

```turtle
<http://localhost/resource/config> 
    <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://linked.opendata.cz/ontology/uv/dpu/xslt/Config>;
    <http://linked.opendata.cz/ontology/uv/dpu/xslt/fileInfo> <http://localhost/resource/fileInfo/0>;
    <http://linked.opendata.cz/ontology/uv/dpu/xslt/outputFileExtension> “.ttl”.
```

```turtle
<http://localhost/resource/fileInfo/0>
    <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://linked.opendata.cz/ontology/uv/dpu/xslt/FileInfo>;
    <http://linked.opendata.cz/ontology/uv/dpu/xslt/param> <http://localhost/resource/param/0>;
    <http://unifiedviews.eu/DataUnit/MetadataDataUnit/symbolicName> “smlouva.ttl”.
```

```turtle
<http://localhost/resource/param/0>
    <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://linked.opendata.cz/ontology/uv/dpu/xslt/Param>;
    <http://linked.opendata.cz/ontology/uv/dpu/xslt/param/name> “paramName”;
    <http://linked.opendata.cz/ontology/uv/dpu/xslt/param/value> “paramValue”.
```

