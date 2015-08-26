### Description

Unzips input file into files based on zip content.

### Configuration parameters

| Name | Description |
|:----|:----|
|**Duplicate name prevention (checkbox)** | If checked DPU prevents collision in file names sent to output when multiple zip files with the same structure are unzipped. Each file will be unzipped into separate folder named by original ZIP file name.<br/>Unchecked outputs file names as stored in zip files into one folder that may lead into execution failure when duplicates occur.<br/>If not sure keep checked.|

#### Example
File 1: tounzipA.zip
```
text.txt
```

File 2: tounzipB.zip
```
text.txt
```

Result when checked:
```
tounzipA.zip/text.txt
tounzipB.zip/text.txt
```

Result when unchecked:
```
text.txt
text.txt
```
Which leads to name collision and execution failure.

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |i| FilesDataUnit | File to unzip |x|
|output |o| FilesDataUnit | List of unzipped files |x|
