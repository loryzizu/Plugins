### Popis

Validuje XML vstupy tromi spôsobmi:
* skontroluje, či je XML správne formátované
* skontroluje, či vyhovuje zadanej XSD schéme
* validuje prostredníctvom špecifikovanej XSLT šablóny

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**XSD schéma** | XSD schéma, voči ktorej sa vstupné XML súbory porovnávajú |
|**XSLT transformácia** | Každý výstupný súbor, ktorý XSLT vytvorí je považovaný za chybovú správu spôsobujúcu zlyhanie validácie |
|**Zlyhanie procesu pri prvej validačnej chybe** | Pri prvom výskyte chyby validácie DPU zastaví vykonávanie procesu |

### Vstupy a výstupy ###

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input        |i| FilesDataUnit | Zoznam súborov určených na validáciu |x|
|outputValid  |o| FilesDataUnit | Zoznam súborov, ktoré vyhovujú validačným kritériám ||
|outputInalid |o| FilesDataUnit | Zoznam súborov nevyhovujúcich validačným kritériám ||