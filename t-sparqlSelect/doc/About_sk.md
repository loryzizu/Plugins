### Popis

Transformuje vstupné dáta pomocou SPARQL SELECT dotazu a výsledok uloží do CSV súboru. Neorbí validáciu dát

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Target path*** | Path and target CSV file name |
|**SPARQL query**| Text area dedicated for SPARQL SELECT query | 

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |i | RDFDataUnit   | RDF vstupné dáta |x|
|output |o | FilesDataUnit | CSV súbor obsahujúci výsledok z SPARQL SELECT dotazu |x|
