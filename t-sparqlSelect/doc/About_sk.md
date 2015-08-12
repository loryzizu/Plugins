### Popis

Transformuje vstupné dáta pomocou SPARQL SELECT dotazu a výsledok uloží do CSV súboru. Nerobí validáciu dát.

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Cieľová cesta** | Cesta a názov cieľového CSV súboru |
|**SPARQL dotaz** | Textové pole určené pre SPARQL dotaz|

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |vstup| RDFDataUnit   | RDF vstupné dáta |áno|
|output |výstup| FilesDataUnit | CSV súbor obsahujúci výsledok z SPARQL SELECT dotazu |áno|
