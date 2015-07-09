### Popis

Skomprimuje súbory na vstupe `input` do zip súboru podľa nakonfigurovaného mena a tento da na vústup `output`.

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|:----|
|**Zip file path/name (with extension):** | Specifies the path/name for the output file to be created. Given path/name must be relative ie. `/data.zip`, `/data/out.zip`. Absolute path like `c:/` must not be used. In case unix system `/dir/data.zip` is interpreted as a relative path. |

### Vstupy a výstupy ###

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input   |i| FilesDataUnit | List of files to zip |x|
|output  |o| FilesDataUnit | Name of zip file |x|
