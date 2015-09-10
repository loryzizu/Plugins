### Popis

Nahrá zoznam súborov na definované miesto.

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Úplná cesta k cieľovému súboru** | Cieľová cesta k súborom, kam sa budú nahrávať|
|**Používateľské meno** | Používateľské meno na cieľovom hoste|
|**Heslo** | Heslo príslušné k používateľskému menu|

#### Príklady platnej cieľovej adresy ####

```INI
file:///home/používateľ/adresár
/home/používateľ/adresár
file://///server/adresár/podadresár
https://server:port/adresár
ftps://server:port/adresár
sftp://server:port/adresár
```

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |vstup| FilesDataUnit | Súbory na nahratie do cieľového adresáru |áno|
|output |výstup| FilesDataUnit | Rovnaké ako vstup, len s aktualizáciou času poslednej modifikácie (Resource.last_modified time) | |