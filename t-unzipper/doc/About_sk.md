### Popis

Rozbalí vstupný súbor komprimovaný metódou ZIP na jednotlivé súbory.

### Konfiguračné parametre

| Názov | Popis |
|:----|:----|
|**Prevencia duplicitných názvov** | Ak je zaškrtnuté, DPU zabráni kolízii názvov súborov na výstupe v prípade, že je rozbalených viac súborov s rovnakou štruktúrou. Každý súbor tak bude rozbalený do samostatného priečinku pomenovaného podľa názvu pôvodného ZIP súboru.<br/>Odznačenie pošle na výstup súbory pomenované rovnako, ako sú uložené v zip súboroch čo môže spôsobiť zlyhanie procesu v prípade výskytu duplikátov.<br/>V prípade pochybností ponechajte zaškrtnuté.|

#### Príklady
Súbor 1: tounzipA.zip
```
text.txt
```

Súbor 2: tounzipB.zip
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
Tento výstup spôsobí kolíziu a zlyhanie procesu.

### Vstupy a výstupy

|Názov |Typ | Dátová jednotka | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |vstup| FilesDataUnit | Súbory na rozbalenie |áno|
|output |výstup| FilesDataUnit | Rozbalené súbory |áno|
