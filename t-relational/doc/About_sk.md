### Popis

Transformuje N vstupných tabuliek do jednej výstupnej tabuľky pomocout SQL SELECT dotazu

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**SQL SELECT dotaz** | SQL SELECT dotaz na získanie/transformáciu dát zo vstupných tabuliek |
|**Meno cieľovej tabuľky** | Meno tabuľky, kam sa uloží výstup SQL dotazu |
|**Stĺpce primárneho kľúča** | Čiarkou oddelený zoznam stĺpcov, ktoré tvoria primárny kľúč výslednej tabuľky |
|**Indexované stĺpce** | Čiarkou oddelený zoznam stĺpcov, ktoré budú indexované vo výslednej tabuľke |

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|inputTables |vstup| RelationalDataUnit | Vstupné tabuľky |áno|
|outputTable |výstup| RelationalDataUnit | Výstupná tabuľka |áno|
