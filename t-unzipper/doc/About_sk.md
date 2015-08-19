### Popis

Rozbalí vstupný súbor komprimovaný metódou ZIP na jednotlivé súbory.

### Konfiguračné parametre

| Názov | Popis |
|:----|:----|
|**Prevencia duplicitných názvov** | Ak je zaškrtnuté, DPU zabráni kolízii názvov súborov na výstupe v prípade, že je rozbalených viac súborov s rovnakou alebo podobnou štruktúrou.<br/>Odznačenie pošle na výstup súbory pomenované rovnako, ako sú uložené v yip súboroch.<br/>V prípade pochybností ponechajte zaškrtnuté.|

### Vstupy a výstupy

|Názov |Typ | Dátová jednotka | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |vstup| FilesDataUnit | Súbory na rozbalenie |áno|
|output |výstup| FilesDataUnit | Rozbalené súbory |áno|
