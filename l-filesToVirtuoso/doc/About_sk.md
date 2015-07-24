### Popis

Nahrá RDF dáta do Virtuoso.

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Virtuoso JDBC URL** | URL pre uskutočnenie JDBC spojenia so serverom Virtuoso |
|**Používateľské meno** | Používateľské meno na serveri Virtuoso |
|**Heslo** | Heslo príslušné užívateľskému menu |
|**Vyčistenie cieľového grafu pred nahrávaním (checkbox)** | Inuitívne|
|**Cesta k nahrávanému priečinku** | Cesta k nahrávanému priečinku |
|**Vrátane podadresárov** | Boolean hodnota (acceptsakceptuje 'true' alebo 'false') |
|**Vzor mena súboru** | Vzor pre názvy súborov, ktoré budú nahrávané |
|**Cieľový graf** | URI cieľového grafu |
|**Interval aktualizácie stavu (s)** | Časový úsek medzi dvoma aktualizáciami stavov (v sekundách) |
|**Počet vlákien** | |
|**Vynechať súbor pri chybe (checkbox)** | V prípade výskytu chyby pokračuje proces s ďalším súborom (ak je checkbox aktívny) |

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|config |i| RdfDataUnit | Dynamická RDF konfigurácia, pozri Pokročilá konfigurácia | |

### Pokročilá konfigurácia

Krok je možné nakonfigurovať aj dznamicky cez vstup `config` pomocou RDF dát.

Vzor konfigurácie:

````turtle
<http://localhost/resource/config	
    <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://unifiedviews.eu/ontology/dpu/filesToVirtuoso/Config>;
    <http://unifiedviews.eu/ontology/dpu/filesToVirtuoso/config/fileName> "dataset.trig";
    <http://unifiedviews.eu/ontology/dpu/filesToVirtuoso/config/graphUri> "http://dataset".
```
