### Description

Unzips input file into files based on zip content.

### Configuration parameters

| Name | Description |
|:----|:----|
|**Duplicate name prevention (checkbox)** | If checked, DPU prevents collision in symbolic names of files sent to the output when multiple zip files with the same structure are unzipped. If checked, symbolic name of each output file will contain not just file name and relative path within the zip archive, but also the identification of the zip archive itself. If unchecked, unzipped files have symbolic names being constructed only based on their names and relative paths within the unzipped archives, which may lead into collision of symbolic names if more than one zip archive with similar structure and file names is unzipped (and the execution may end with a failure). If not sure, please keep checked.|

#### Example
Input File 1: tounzipA.zip, containing:
```
text.txt
```

Input File 2: tounzipB.zip, containing:
```
text.txt
```

Symbolic name of the unzipped file when 'Duplicate name prevention' is checked:
```
tounzipA.zip/text.txt
tounzipB.zip/text.txt
```

Symbolic name of the unzipped file when 'Duplicate name prevention' is not unchecked:
```
text.txt
text.txt
```


### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |i| FilesDataUnit | File to unzip |x|
|output |o| FilesDataUnit | List of unzipped files |x|
