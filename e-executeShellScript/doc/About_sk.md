### Popis

Spustí script (shell script) s definovanou konfiguráciou

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|Názov scriptu|Názov scriptu ktorý sa má vykonať.|
|Konfigurácia|Text, ktorý sa použije ako konfigurácia scriptu.|

### Technické detaily

* Script musí byť pripravený tak, aby očakával 2 alebo 3 parametre. prvý parameter je konfigurácia, druhý (nepovinný paarameter) zoznamom vstupných súborov, tretí parameter je adresár kam pôjdu výstupné súbory scriptu.
* Konfigurácia je textový súbor so zoznamom argumentov
* Zoznam vstupných súborov je textový súbor so zoznamom vstupných súborov s plnými cestami k nim, oddelených novými riadkami.
* Všetky výstupné súbory script musí ukladať do výstupného adresára
* Spúšťaný script musí byť umiestnený v adresári, ktorý je nakonfigurovaný vo frontend.properties aj v backend.properties pod kľúčom shell.scripts.path

### Vstupy a výstupy ###

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|filesInut |vstup|FilesDataUnit| Zoznam vstupných súborov na spracovanie ||
|filesOutput |výstup|FilesDataUnit|Zoznam súborov ktoré boli v kroku vytvorené.|X|
