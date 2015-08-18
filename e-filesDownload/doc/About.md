### Description

Downloads files based on provided list. Individual files and also name of directories could be defined.

### Configuration parameters

| Name | Description |
|:----|:----|
|**List of files and directories to extract** | If directory is provided then all files and files in subdirectories are extracted. 
If filename is specified for each downloaded entry, such filename is then used to internally identify the given file further on the pipeline and also such value is used as the value of 
VirtualPath (target location of the file when loaded outside of UnifiedViews at the end of pipeline). If you do not care about the internal name of the file or value of VirtualPath, e.g., 
in cases where you just need to iterate over downloaded files later on process every downloaded file in the same way,  you do not need to specify filename. |

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
