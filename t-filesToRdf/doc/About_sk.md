### Popis

Získa RDF dáta so súborov ľubovoľného formátu a pridá ich do RDF

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Rozsah**|0 = jeden súbor, jedna transakcia, 1 = automatické pridanie spojenia, n = pridá každých n trojíc|
|**Symbolické meno**||
|**Formát riadku**||
|**Formát súboru**||

### Vstupy a výstupy ###

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|filesInput|i|FilesDataUnit|Vstupný súbor s dátami.|x|
|rdfOutput|o|RDFDataUnit|Získané RDF dáta.|x|