### Popis

Extrahuje RDF dáta y externého SPARQL endpoint-u podľa predpisu definovaného v konfigurácii a výsledok vo forme RDF zašle na výstup `output`

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Enpoint URL**| URL  SPARQL endpoint-u z ktorého sa majú extrahovať dáta|
|**SPARQL Construct**| SPARQL construct dotaz ktorým sa dáta zo SPQRQL endpointu vyextrahujú|

### Vstupy a výstupy ###

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|output |o |RdfDataUnit |Extrahované dáta vo forme RDF|x|

