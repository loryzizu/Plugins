### Popis

Transformuje RDF grafy do súborov

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Formát súboru pre RDF dáta (list)** | Formát súboru na výstupe:<BR>- Turtle<BR>- RDF/XML<BR>- N-Triples<BR>- N3<BR>- RDFa |
|**Vygenerovať súbor .graph s názvom výstupného grafu (checkbox)** | Je potrebý grafový súbor? |
|**Názov výstupného grafu** | Názov výstupného grafu (ak je genrovaný grafový súbor) |
|**Názov výstupného súboru (bez prípony, príponu určuje výstupný formát)** | zrejmé |

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |i| RDFDataUnit   | RDF grafuy |x|
|output |o| FilesDataUnit |Súbory obsahujúce RDF triple |x|
|config |i| RdfDataUnit | Dynamická RDF konfigurácia, pozri Pokročilá konfigurácia | |

### Pokročilá konfigurácia

Krok je možné nakonfigurovať aj dznamicky cez vstup `config` pomocou RDF dát.

Vyor konfigurácie:

````turtle
<http://localhost/resources/configuration>
    <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://unifiedviews.eu/ontology/dpu/rdfToFiles/Config>;
    <http://unifiedviews.eu/ontology/dpu/rdfToFiles/config/fileFormat> "TriG";
    <http://unifiedviews.eu/ontology/dpu/rdfToFiles/config/outputUri> "http://output-graph/name";
    <http://unifiedviews.eu/ontology/dpu/rdfToFiles/config/outputFile> "graph-output-file".
```
