### Popis

Nahrávanie RDF grafov do externej Virtuoso inštancie.

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Virtuoso JDBC URL**|URL pre uskutočnenie JDBC relácie so serverom Virtuoso.|
|**Používateľské meno**|Používateľské meno na serveri Virtuoso.|
|**Heslo**|Heslo k príslušnému používateľskému menu.|
|**Vyčisti cieľový graf pred nahratím (checkbox)**|Intuitívne|
|**Cieľový graf**|URI cieľového grafu. Môže zostať prázdne pre určenie nagrávania 'per-graph'. v tomto móde, každý graf na vstupe je nahraný do samostatného grafu na výstupe, jeho názov sa vytvorí z VirtualGraph, ak VirtualGraph nie je nastavený, použije sa meno interného RDF úložiska.|
|**Počet vlákien**|Koľko vlákien sa môže použiť pre urýchlenie nahrávania|
|**Vynechaj súbor pri chybe (checkbox)**|Nezastavuje sa vykonávanie v prípade, že v jednom zo súborov nastala chyba (ak je checkbox aktívny).|

### Vstupy a výstupy ###

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|rdfInput|i|RDFDataUnit|RDF grafy určené na nahratie|x|
|rdfOutput|o|RDFDataUnit|v per-graph móde: RDF grafy zo vstupu s nastaveným CKAN zdrojom, s VirtualGraph nastaveným na meno skutočného grafu, kam boli dáta nahraté. V single graph móde: Jeden RDF graf s nastaveným CKAN zdrojom, s VirtualGraph nastaveným na názov skutočeho grafu, kam boli dáta nahraté.||