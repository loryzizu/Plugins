### Popis

Konvertuje RDF grafy na súbory

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**RDF formát (zoznam)**|<UL><LI>Turtle</LI><LI>RDF/XML</LI><LI>N-Triples</LI><LI>N3</LI><LI>RDFa</LI></UL>|
|**Generuj súbor grafu (checkbox)**|Vyžaduje sa súbor grafu?|
|**Meno výstupného grafu**|v prípade, že je vyžadovaný|
|**Cesta k súboru/názov bez prípony**|intuitívne|

### Vstupy a výstupy ###

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input|i|RDFDataUnit|RDF graf|x|
|output|o|FilesDataUnit|Súbor obsahujúci RDF trojice|x|