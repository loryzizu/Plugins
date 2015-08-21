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
|**Mapovacia tabuľka** | Uvedie sa definícia tabuľky, do ktorej sa majú uložiť tabuľkové dáta načítané zo súboru. Je potrebné uviesť želané názvy stĺpcov. Ich názvy nemusia byť zhodné s tými zo súboru. Stĺpce zo súboru sa premapujú do tabuľky automaticky podľa poradia (prvý stĺpec zo súboru na prvý stĺpec tabuľky, druhý na druhý atď). Dátový typ každého stĺpca je text (bez obmedzenia veľkosti). Voľba *Kompozitný kľúč?* znamená, že daný stĺpec sa stane súčasťou primárneho klúča vytváranej tabuľky. Vhodná voľba primárneho kľúču tabuľky môže urýchliť nasledovné spracovanie tabuľky v procese.

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |vstup| FilesDataUnit | Zoznam súborov, z ktorých majú byť načítané tabuľkové dáta |áno|
|output |výstup| RelationalDataUnit| Výstupná tabuľka/tabuľky |áno|
