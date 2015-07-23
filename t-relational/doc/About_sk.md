### Popis

Transformuje N vstupných tabuliek na 1 výstupnú použitím výberových SQL dotazov

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**SQL dotaz**|SQL dotaz pre výber dát zo zdrojovej databázy|
|**Názov cieľovej tabuľky**|Názov tabuľky použitej pre interné uchovanie vybraných dát|

### Vstupy a výstupy ###

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|inputTables|i|RelationalDataUnit|Zdrojové databázové tabuľky||
|outputTable|o|RelationalDataUnit|Výstupná tabuľka||