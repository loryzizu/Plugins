### Popis

Nahrávanie RDF grafov do externej Virtuoso inštancie.
Do voliteľného výstupu `rdfOutput` generuje metadáta o grafoch uložených do Virtuoso vrátane mien grafov.

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Virtuoso JDBC URL** | URL pre uskutočnenie JDBC relácie so serverom Virtuoso |
|**Používateľské meno** | Používateľské meno na serveri Virtuoso |
|**Heslo** | Heslo k príslušnému používateľskému menu |
|**Vyčisti cieľový graf pred nahratím (checkbox)** | Intuitívne |
|**Cieľový graf** | URI cieľového grafu. Môže zostať prázdne pre určenie nagrávania 'per-graph'. v tomto móde, každý graf na vstupe je nahraný do samostatného grafu na výstupe, jeho názov sa vytvorí z VirtualGraph, ak VirtualGraph nie je nastavený, použije sa meno interného RDF úložiska |
|**Počet vlákien** | Koľko vlákien sa môže použiť pre urýchlenie nahrávania |
|**Vynechaj súbor pri chybe (checkbox)** | Nezastavuje sa vykonávanie v prípade, že v jednom zo súborov nastala chyba (ak je checkbox aktívny) |

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|rdfInput  |vstup| RDFDataUnit | RDF grafy určené na nahratie |áno|
|rdfOutput |výstup| RDFDataUnit | Metadáta o grafoch uložených do Virtuoso | |