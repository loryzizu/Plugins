### Popis

Získa RDF dáta zo súborov ľubovoľného RDF formátu

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Veľkosť transakcie** | 0 znamená, že na spracovanie každého súboru sa použije separátna transakcia. 1 znamená automatický commit každej databázovej operácie (autocommit). 2 a viac znamená, že v transakcii sa spracuje daný počet RDF trojíc. |
|**Čo robiť keď zlyhá transformácia jedného súboru** | Zastaviť vykonávanie alebo pokračovať a preskočiť na ďaľší súbor |
|**Ako vyberať symbolické meno výstupu** | Použiť symbolické mená vstupných súborov alebo použiť jedno pevné symbolické meno |

### Vstupy a výstupy ###

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|filesInput |i| FilesDataUnit | Vstupné súbory s dátami |x|
|rdfOutput  |o| RDFDataUnit | Získané RDF dáta |x|
