### Popis

Získa RDF dáta so súborov ľubovoľného formátu a pridá ich do RDF

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Veľkosť transakcie** | 0 = jeden súbor, jedna transakcia, 1 = automatické pridanie spojenia, n = pridá každých n trojíc |
|**Čo robiť keď zlyhá transformácia jedného súboru** | Zastaviť vykonávanie alebo pokračovať a preskočiť na ďaľší súbor |
|**Ako vyberať symbolické meno výstupu** | Použiť symbolické mená vstupných súborov alebo použiť jedno pevné symbolické meno |

### Vstupy a výstupy ###

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|filesInput |i| FilesDataUnit | Vstupný súbor s dátami |x|
|rdfOutput  |o| RDFDataUnit | Získané RDF dáta |x|
