### Popis

Rozbalí vstupný súbor komprimovaný metódou ZIP na jednotlivé súbory.

### Konfiguračné parametre

| Názov | Popis |
|:----|:----|
|**Prevencia duplicitných názvov** | Ak je zaškrtnuté, DPU zabráni kolízii názvov súborov na výstupe v prípade, že je rozbalených viac súborov s rovnakou štruktúrou. Každý súbor tak bude identifikovaný nie len názvom a relatívnou cestou archivovaných súborov, ale aj identifikátorom samotného zip archívu. Ak nie je zaškrtnutý, rozbalené súbory sú identifikované iba ich menom a relatívnymi cestami vnútri archívu, čo môže spôsobiť kolízie názvov, ak je rozbalených viac ako jeden archív obsahujúcich súbory s rovnakým názvom. Ak si nie ste istý, ponechajte checkbox zaškrtnutý. |

#### Príklady
Vstupný súbor 1: tounzipA.zip
```
text.txt
```

Vstupný súbor 2: tounzipB.zip
```
text.txt
```

Výsledok pri zaškrtnutom checkboxe:
```
tounzipA.zip/text.txt
tounzipB.zip/text.txt
```

Výsledok pri nezaškrtnutom checkboxe:
```
text.txt
text.txt
```
Ak checkbox nie je zaškrtnutý, tento výstup spôsobí kolíziu a zlyhanie procesu.

### Vstupy a výstupy

|Názov |Typ | Dátová jednotka | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |vstup| FilesDataUnit | Súbory na rozbalenie |áno|
|output |výstup| FilesDataUnit | Rozbalené súbory |áno|
