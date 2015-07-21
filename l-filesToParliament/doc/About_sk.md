### Popis

Nahrá zoznam súborov na úložisko Parliament prostredníctvom hromadného HTTP rozhrania.

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**URL hromadného koncového bodu***|URL hromadného koncového bodu pre úložisko Parliament (napr. http://localhost:8080/parliament/bulk/insert)|
|**RDF formát**|RDF formát vstupných súborov (pre navigáciu Parliament nahrávača).|
|**Vyčistenie cieľového grafu pred nahraním**| Vyčistí cieľový graf pred nahraním nových dát. Inak sú nové dáta len pridané k pôvodným dátam. Prednastavená hodnota: false.|

### Vstupy a výstupy ###

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|filesInput|i|FilesDataUnit|Súbory určené na nahranie na určený cieľ.|x|
