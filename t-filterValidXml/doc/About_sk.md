### Popis

Validuje XML vstupy tromi spôsobmi:
* skontroluje, či je XML správne formátované
* skontroluje, či vyhovuje zadanej XSD schéme
* validuje prostredníctvom špecifikovanej XSLT šablóny

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**XSD schéma** | XSD schéma, voči ktorej sa vstupné XML súbory validujú |
|**XSLT transformácia** | Prázdny výstup XSLT transformácie znamená bezchybnú validáciu. Hociaký neprázdny výstup znamená chybu pri validácii, pričom výstup zároveň obsahuje informáciu o konkrétnej chybe, ktorá ho spôsobila.|
|**Zlyhanie procesu pri prvej validačnej chybe** | Pri prvom výskyte chyby validácie DPU zastaví vykonávanie procesu |

### Vstupy a výstupy ###

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input        |vstup| FilesDataUnit | Zoznam súborov určených na validáciu |áno|
|outputValid  |výstup| FilesDataUnit | Zoznam súborov, ktoré vyhovujú validačným kritériám ||
|outputInalid |výstup| FilesDataUnit | Zoznam súborov nevyhovujúcich validačným kritériám ||