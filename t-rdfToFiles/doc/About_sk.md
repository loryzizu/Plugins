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
