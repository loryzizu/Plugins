### Popis

Stiahne RDF dáta z externého SPARQL koncového bodu podľa predpisu definovaného v konfigurácii a výsledok vo forme RDF zašle na výstup `output`.
Dopyt je možné prepísať tak, aby sa koncový bod dopytoval po dávkach.

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**URL koncového bodu** | URL SPARQL koncového bodu z ktorého sa majú extrahovať dáta |
|**SPARQL Construct** | SPARQL construct dotaz, ktorým sa dáta zo SPARQL koncového bodu vyextrahujú |
|**Veľkosť dávky:** | Na zamedzenie vypršania časového limit alebo obídenie limitu na počet výsledkov koncového bodu, je možné prepísať dopy tak, že dopytovanie prebieha po menších dávkach. Toto nastavenie umožňuje nastavenie veľkosti jednej dávky. |

### Vstupy a výstupy 

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|output |výstup| RdfDataUnit | Extrahované dáta vo forme RDF |áno|

