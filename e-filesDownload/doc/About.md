### Description

Downloads files based on provided list. Individual files and also name of directories could be defined.

### Configuration parameters

| Name | Description |
|:----|:----|
|**List of files and directories to extract** | If directory is provided then all files and files in subdirectories are extracted |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|output |o| FilesDataUnit | Downloaded files |x|
|config |i| RdfDataUnit | Dynamic DPU configuration, see Advanced configuration | |

### Advanced configuration

It is also possible to dynamically configure the DPU over its input `config` using RDF data.

Configuration samples:

```turtle
<http://localhost/resource/config> 
    <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://unifiedviews.eu/ontology/dpu/filesDownload/Config>;
    <http://unifiedviews.eu/ontology/dpu/filesDownload/hasFile> <http://localhost/resource/file/0>.
```

```turtle
<http://localhost/resource/file/0>
    <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://unifiedviews.eu/ontology/dpu/filesDownload/File>;
    <http://unifiedviews.eu/ontology/dpu/filesDownload/file/uri> "http://www.zmluvy.gov.sk/data/att/117597_dokument.pdf";
    <http://unifiedviews.eu/ontology/dpu/filesDownload/file/fileName> "zmluva.pdf".`
```