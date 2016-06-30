### Popis

Tento krok umožňuje vykonávať HTTP požiadavky (metódy GET a POST) na webové služby a obsah odpovede ďalej posiela vo forme súborov.

Cieľom tohto kroku je umožniť volanie REST aj SOAP webových služieb.

Pri HTTP POST požiadavke môže krok pracovať v 3 módoch (typ posielaných dát)
* multipart (formulárové dáta)
* textové dáta (je možné špecifikovať presný typ obsahu: XML, JSON, ...)
* binárne dáta (súbor) - v tomto prípade je nutné mať na vstupe kroku súbory, ktoré sa potom posielajú ako obsah HTTP požiadavky (HTTP požiadavka pre každý súbor)

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|HTTP metóda | Metóda HTTP požiadavky. Podporované: GET,POST. Na základe metódy sa zobrazujú ďalšie konf. polia |
|URL adresa | URL webovej služby kde sa bude posielať HTTP požiadavka |
|Názov cieľového súboru| Názov vytvoreného súboru, do ktorého sa uloží obsah HTTP odpovede |
|Prípona výsledných súborov | (POST / súbor mód) Prípona vytváraných súborov obsahujúcich obsah HTTP odpovedí. Mená súborov sú potom 001_prípona, 002_prípona, ... |
|Základná autentifikácia | Umožňuje poslať BASIC autentifikáciu (meno / heslo) pre HTTP požiadavku |
|Meno používateľa | (ak zapnutá autentifikácia) Meno pre autentifikáciu |
|Heslo | (ak zapnutá autentifikácia) Heslo pre autentifikáciu |
|Typ posielaných dát | (pre POST metódu) Typ posielaných dát v HTTP požiadavke: multipart, text, súbor |
|Typ obsahu| (POST/text) Typ posielaného textu, nastaví sa do HTTP hlavičky Content-Type (napr. XML, JSON, SOAP, ...)|
|Kódovanie textu obsahu požiadavky | (POST/text) Kódovanie textu v požiadavke, uvádza sa v Content-Type HTTP hlavičke |
|Obsah HTTP požiadavky | (POST/text) Text poslaný v HTTP požiadavke |
|Formulárové dáta| (POST/multipart) Tabuľka posielaných formulárových dát vo forme dvojíc kľúč - hodnota |

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|requestFilesConfig |vstup| FilesDataUnit | Súbory posielané ako obsah HTTP požiadaviek | nie |
|requestOutput |výstup| FilesDataUnit | Súbor(y) obsahujúce obsah HTTP odpovedí |áno|

 