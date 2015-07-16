### Popis

Extrahuje RDF dáta z externého SPARQL koncového bodu podľa predpisu definovaného v konfigurácii a výsledok vo forme RDF zašle na výstup `output`

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Enpoint URL**| URL SPARQL koncového bodu z ktorého sa majú extrahovať dáta|
|**SPARQL Construct**| SPARQL construct ktorým sa dáta zo SPARQL koncového bodu vyextrahujú|

### Vstupy a výstupy ###

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|output |o |RdfDataUnit |Extrahované dáta vo forme RDF|x|

