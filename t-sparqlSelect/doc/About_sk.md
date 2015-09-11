### Popis

Transformuje vstupné dáta pomocou výberového SPARQL dotazu a výsledok uloží do CSV súboru. Nerobí validáciu dát.

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Cieľová cesta** | Cesta a názov cieľového CSV súboru |
|**SPARQL výberový dotaz** | Textové pole určené pre SPARQL výberový dotaz|

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |vstup| RDFDataUnit   | RDF vstupné dáta |áno|
|output |výstup| FilesDataUnit | CSV súbor obsahujúci výsledok z SPARQL SELECT dotazu |áno|
