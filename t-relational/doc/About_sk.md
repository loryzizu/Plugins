### Popis

Transformuje N vstupných tabuliek do jednej výstupnej tabuľky pomocout SELECT dotayu

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**SQL SELECT dotaz** | SQL SELECT dotaz na extrahovanie dát zo zdrojovej databázy |
|**Meno cieľovej tabuľky** | Meno tabuľky kam sa uložia interne extrahované dáta |

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|inputTables |i| RelationalDataUnit | Ydrojové databázové tabuľky |x|
|outputTable |o| RelationalDataUnit | Výstupná (transformovaná) tabuľka |x|
