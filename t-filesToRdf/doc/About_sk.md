### Popis

Získa RDF dáta zo súborov ľubovoľného RDF formátu. Výstupná dátová jednotka RDF obsahuje jeden záznam pre každý vstupný súbor (každý vstupný súbor vyprodukuje jeden graf RDF dát).

Pri predvolenom nastavení je RDF formát detekovaný automaticky na základe prípony vstupného súboru. V prípade, že detekcia bude nesprávna, špecifikujte prosím RDF formát vstupných súborov manuálne.

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**RDF formát vstupných súborov** | RDF formát dát vo vstupných súboroch. AUTO = automatická detekcia RDF formátu vstupných súborov (predvolené) |
|**Veľkosť transakcie** | 0 znamená, že na spracovanie každého súboru sa použije separátna transakcia. 1 znamená automatický commit každej databázovej operácie (autocommit). 2 a viac znamená, že v transakcii sa spracuje daný počet RDF trojíc. |
|**Čo robiť keď zlyhá transformácia jedného súboru** | Zastaviť vykonávanie alebo pokračovať a preskočiť na ďaľší súbor |
|**Ako vyberať symbolické meno výstupu** | Použiť symbolické mená vstupných súborov alebo použiť jedno pevné symbolické meno |

### Vstupy a výstupy ###

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|filesInput |vstup| FilesDataUnit | Vstupné súbory s dátami |áno|
|rdfOutput  |výstup| RDFDataUnit | Získané RDF dáta |áno|
