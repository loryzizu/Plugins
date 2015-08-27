### Description

Unzips input file into files based on zip content.

### Configuration parameters

| Name | Description |
|:----|:----|
|**Duplicate name prevention (checkbox)** | If checked, DPU prevents collision in file names sent to the output when multiple zip files with the same structure are unzipped. Each file will be identified not just with name and relative path within the zip archive, but also with identification of the zip archive itself. If unchecked, unzipped files are identified only with their names and relative paths within the unzipped archives, which may lead into collision if more than one zip archive with similar structure and file names is unzipped. If not sure, please keep checked.|

#### Example
Input File 1: tounzipA.zip
```
text.txt
```

Input File 2: tounzipB.zip
```
text.txt
```

Output when checked:
```
tounzipA.zip/text.txt
tounzipB.zip/text.txt
```

Output when unchecked:
```
text.txt
text.txt
```

If unchecked, it leads in this case to name collision and execution failure.

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |i| FilesDataUnit | File to unzip |x|
|output |o| FilesDataUnit | List of unzipped files |x|
