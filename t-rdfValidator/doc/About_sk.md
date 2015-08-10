### Popis

Zvaliduje RDF dáta

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Validačný dopyt** | ASK alebo SELECT SPARQL dopyt.<br>ASK má byť naformulovaný tak, že vráti TRUE práve vtedy, keď sú dáta NEvalidné<br>SELECT má byť naformulovaný tak, že vráti neprázdny zoznam práve vtedy, ak sú dáta NEvalidné. |
|**Zastav transformáciu ak validácia nie je úspešná** | Pri zaškrtnutí proces sa zastaví ak je validácia neúspešná.<br>Ak nezaškrtnuté, výsledok pri zlyhaní sa zapíše do logu a proces pokračuje. |

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|rdfInput |i| RDFDataUnit | Vstupné RDF dáta na zvalidovanie |x|
|rdfOutput|o| RDFDataUnit | Kópia rdfInput dát ||
