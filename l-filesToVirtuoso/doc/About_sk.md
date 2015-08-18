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
|**Vrátane podadresárov (checkbox)** | Ak je checkbox zaškrtnutý, na Virtuoso sa nahrá aj obsah podadresárov |
|**Vzor mena súboru** | Vzor pre názvy súborov, ktoré budú nahrávané podľa normy SQL (reťazec znakov nahrádza symbol '%'). Napr. '%.ttl' |
|**Cieľový graf** | URI cieľového grafu |
|**Interval aktualizácie stavu (s)** | Časový úsek medzi dvoma aktualizáciami stavov (v sekundách) |
|**Počet vlákien** | |
|**Vynechať súbor pri chybe (checkbox)** | V prípade výskytu chyby pokračuje proces s ďalším súborom (ak je checkbox aktívny) |

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|config |vstup| RdfDataUnit | Dynamická RDF konfigurácia, pozri Pokročilá konfigurácia | |

Vstup tohto kroku (RDF dáta) sú súbory, ktoré sa musia nachádzať v nahrávanom priečiku na lokálnom disku. Všetky súbory musia byť vo formátoch 
application/rdf+xml alebo text/turtle (prípony .rdf resp. .ttl). Užívateľ musí zabezpečiť spustenie tohto kroku až po tom, čo je priečinok naplnený súbormi.
To je možné dosiahnuť zaradením l-filesUpload s 'spustiť po' hranou na toto DPU.  
Príklad transformácie:
    --------------------      ----------------      -----------------                       ---------------------
    |                  |      |              |      |               |                       |                   |
    | e-sparqlEndpoint | ---> | t-rdfToFiles | ---> | l-filesUpload | --spustiť po hrana--> | l-FilesToVirtuoso |
    |                  |      |              |      |               |                       |                   |
    --------------------      ----------------      -----------------                       ---------------------
 

### Pokročilá konfigurácia

Krok je možné nakonfigurovať aj dznamicky cez vstup `config` pomocou RDF dát.

Vzor konfigurácie:

````turtle
<http://localhost/resource/config	
    <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://unifiedviews.eu/ontology/dpu/filesToVirtuoso/Config>;
    <http://unifiedviews.eu/ontology/dpu/filesToVirtuoso/config/fileName> "dataset.trig";
    <http://unifiedviews.eu/ontology/dpu/filesToVirtuoso/config/graphUri> "http://dataset".
```
