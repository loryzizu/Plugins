### Popis

Skomprimuje súbory na vstupe `input` do zip súboru podľa nakonfigurovaného mena a tento dá na výstup `output`.

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|:----|
|**Zip súbor cesta/názov (s príponou)** |  Špecifikuje cestu s názvom výstupného zip súboru. Cesta s názvom musí byť relatívna, napr. /subor1.zip, /adresar1/subor2.zip. Absolútna cesta ako napr. C:/ nemôže byť použitá. V prípade systémov UNIX /adresar1/subor2.zip je braná ako relatívna cesta.|

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input   |i| FilesDataUnit | Zoznam súborov na zbalenie do zipu. |x|
|output  |o| FilesDataUnit | Zip súbor |x|
