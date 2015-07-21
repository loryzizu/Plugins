### Popis

Nahrá vstup z internej databázovej tabuľky do vzdialenej SQL databázy (v súčasnosti je podporovaná PostgreSQL)

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**URL databázy** | JDBC URL zdrojovej databázy (v súčasnosti je podporovaná iba PostgreSQL) |
|**Používateľské meno** | Login používateľa databázy |
|**Heslo** | Heslo k databáze |
|**Pripojenie cez SSL** | Použitie bezpečného pripojenia k databáze |
|**Predpona cieľovej tabuľky** | Názov tabuľky použitej na interné uchovanie extrahovaných dát |
|**Vymazať tabuľku pred vložením** | Vyprázdni tabuľku pred vkladaním |
|**Znovu vytvoriť tabuľku** | Odstráni tabuľku v prípade, ak existuje |

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|inTablesData |i| RelationalDataUnit | Dáta z databázovej tabuľky |x|