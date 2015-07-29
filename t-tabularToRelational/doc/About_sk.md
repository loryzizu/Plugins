### Popis

Parsuje súbor s tabuľovými dátami vo forme CVS, DBF, XLS a výsledok dá na výstup vo forme relačných dát.

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Názov tabuľky** | Meno tabuľky do ktorej budú uložené sparsované dáta |
|**Typ súboru** | Typ vstuponého súboru (XLS/X, CVS, DBF) |
|**Dáta začínajú na riadku** | Definuje na ktorom riadku začínajú dáta |
|**Znaková sada** | Znaková sada vstupného súboru |
|**Znak úvodzoviek** | Znak na ohraničenie jednotlivých polí používaný pri parsovaní CSV súboru, napr. `"` |
|**Oddelovač** | Oddelovač polí oužívaný pri parsovaní CSV súboru, napr. `,` |
|**Mapovacia tabuľka** | Meno a typ stĺpca v CSV súbore. Taktiež treba označiť či je stĺpec použitý ako primárny kľúč |

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |i| FilesDataUnit | List of files to parse |x|
|output |o| RelationalDataUnit| Relational dataunit with parsed data | x|
