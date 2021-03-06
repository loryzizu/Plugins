### Popis

Stiahne súbory podľa zoznamu definovaného v konfigurácii. Je možné stiahnuť samostatné súbory alebo aj celé adresáre.

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Zoznam súborov a adresárov na stiahnutie** | Keď je uvedený adresár, stiahnu sa všetky súbory v adresári a jeho podadresároch.|
Ak je zadaný názov každého sťahovaného vstupu, tento názov sa použije na vnútornú identifikáciu daného súboru v ďalšom pokračovaní procesu a tiež ako názov
virtuálnej cesty (cieľové umiestnenie súboru pri nahrávaní mimo UnifiedViews na konci procesu). Ak vás nezaujíma vnútorné pomenovanie súboru alebo názov virtuálnej cesty, napr.
v prípadoch keď potrebujete iba prechádzať stiahnuté súbory v pokračovaní procesu tým istým spôsobom, nie je potrebné špecifikovať meno súboru.

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|config |vstup| RdfDataUnit | Dynamická RDF konfigurácia, pozri Pokročilá konfigurácia | |
|output |výstup| FilesDataUnit | Stiahnuté súbory |áno|


### Pokročilá konfigurácia

Krok je možné nakonfigurovať aj dynamicky cez vstup `config` pomocou RDF dát.

Vzor konfigurácie:

```turtle
<http://localhost/resource/config> 
    <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://unifiedviews.eu/ontology/dpu/filesDownload/Config>;
    <http://unifiedviews.eu/ontology/dpu/filesDownload/hasFile> <http://localhost/resource/file/0>.
```

```turtle
<http://localhost/resource/file/0>
    <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://unifiedviews.eu/ontology/dpu/filesDownload/File>;
    <http://unifiedviews.eu/ontology/dpu/filesDownload/file/uri> "http://www.zmluvy.gov.sk/data/att/117597_dokument.pdf";
    <http://unifiedviews.eu/ontology/dpu/filesDownload/file/fileName> "zmluva.pdf".`
```
