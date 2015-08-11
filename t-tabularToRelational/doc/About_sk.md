### Popis

Načíta súbor s tabuľovými dátami vo formáte CSV, XLS, XLSX alebo DBF a výsledok dá na výstup vo forme relačných dát.

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Názov tabuľky** | Meno tabuľky do ktorej budú uložené načítané dáta |
|**Typ súboru** | Typ vstupného súboru (XLS/X, CSV, DBF) |
|**Dáta začínajú na riadku** | Definuje na ktorom riadku začínajú dáta |
|**Znaková sada** | Znaková sada vstupného súboru |
|**Znak úvodzoviek** | Znak na ohraničenie jednotlivých polí používaný pri parsovaní CSV súboru, napr. `"` |
|**Oddelovač** | Oddelovač polí oužívaný pri parsovaní CSV súboru, napr. `,` |
|**Mapovacia tabuľka** | Meno a typ stĺpca v CSV súbore. Taktiež treba označiť či je stĺpec použitý ako primárny kľúč |

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |vstup| FilesDataUnit | Zoznam súborov, z ktorých majú byť načítané tabuľkové dáta |áno|
|output |výstup| RelationalDataUnit| Výstupná tabuľka/tabuľky |áno|
