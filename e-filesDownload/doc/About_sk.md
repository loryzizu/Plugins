### Popis

Stiahne súbory podľa zoznamu definovaného v konfigurácii. Je možné stiahnuť samostatné súborz alebo aj celé adresáre.

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Zoznam súborov a adresárov na extrakciu** | Keď sa uvedie adresár, všetky súbory v adresári a jeho podadresároch sa stiahnu |

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|output |o| FilesDataUnit | Stiahnuté súbory |x|
|config |i| RdfDataUnit | Dynamická RDF konfigurácia, pozri Pokročilá konfigurácia | |

### Pokročilá konfigurácia

Krok je možné nakonfigurovať aj dznamicky cez vstup `config` pomocou RDF dát.

Vyor konfigurácie:

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
