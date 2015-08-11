### Popis

Stiahne RDF dáta z externého SPARQL koncového bodu podľa predpisu definovaného v konfigurácii a výsledok vo forme RDF zašle na výstup `output`.

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**URL koncového bodu** | URL SPARQL koncového bodu z ktorého sa majú extrahovať dáta |
|**SPARQL Construct** | SPARQL construct dotaz, ktorým sa dáta zo SPARQL koncového bodu vyextrahujú |

### Vstupy a výstupy 

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|output |o| RdfDataUnit | Extrahované dáta vo forme RDF |x|

