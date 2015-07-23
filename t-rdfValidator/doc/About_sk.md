### Popis

Validuje RDF dáta

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Validačný dopyt** | ASK alebo SELECT SPARQL dopyt.<br>ASK vracia TRUE pri zlyhaní validácie<br>SELECT vracia neprázdny zoznam pri zlyhané validácie |
|**Zastav transformáciu ak validácia nie je úspešná** | Pri zaškrtnutí proces sa zastaví ak je validácia neúspešná.<br>Ak neyaškrtnuté, výsledok pri zlyhaní sa zapíše do logu a proces pokjračuje |

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|rdfInput |i| RDFDataUnit | Input RDF to be validated |x|
|rdfOutput|o| RDFDataUnit | Copy of rdfInput data ||
