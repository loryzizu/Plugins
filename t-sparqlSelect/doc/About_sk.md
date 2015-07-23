### Popis

Transformuje SPARQL výberový dotaz do CSV súboru (bez validácie)

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Cieľová cesta***|cesta a názov cieľového CSV súboru|
|**SPARQL dotaz**|textové pole určené pre SPARQL dotaz|

### Vstupy a výstupy ###

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input|i|RDFDataUnit|RDF graf|x|
|output|o|FilesDataUnit|CSV súbor obsahujúci výsledky SPARQL výberového dotazu|x|