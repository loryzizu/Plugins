### Popis

Filtruje súbory na základe filtrovacieho vzoru definovaného v konfigurácii. Podporované sú aj regulárne výrazy.

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Použitý filter** | Existujú dva filtre na vykonávanie filtrovania: <br> - symbolické meno <br> - virtuálna cesta |
|**Aktuálna šablóna** | Filtrovací vzor, napr. '.*csv' |
|**Použiť regulárny výraz (checkbox)** | Ak je checkbox aktívny, na vstupe sú akceptované [regulárne výrazy](http://www.w3.org/TR/xpath-functions/#regex-syntax) |

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |vstup| FilesDataUnit | Zoznam súborov pre filtrovanie |áno|
|output |výstup| FilesDataUnit | Zoznam súborov vyhovujúcich filtru |áno|