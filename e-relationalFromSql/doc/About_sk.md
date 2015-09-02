### Popis

Načíta relačné dáta (tabuľku) z externej relačnej databázy.

Používa sa na načítanie dát z relačnej databázy pomocou SQL dotazov a následné uloženie do internej dátovej hrany určenej pre relačné dáta.
Tento Krok umožňuje využiť niektoré vlastnosti pre načítanie dát z databázy: zoznam tabuliek v zdrojovej databáze, generovanie SELECT dotazu pre zvolenú tabuľku, preview údajov.
Podporované je aj bezpečné pripojenie do externej databázy cez SSL.
Kvoli bezpečnosti sa silne odporúča používať databázového používateľa, ktorý má práva iba na čítanie z danej databázy.

Krok podporuje nasledujúce databázy:
* PostgreSQL
* Oracle
* MySQL
* Microsoft SQL

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Typ databázy** | Typ databázy: PostgreSQL, Oracle, MySQL, MS SQL |
|**Host** | Host adresa databázy |
|**Port** | Port databázy |
|**Meno databázy** | Meno databázy (pre ORACLE SID) |
|**Meno inštancie** | *(nepovinné)* Meno inštancie databázy - pre MSSQL |
|**Meno používateľa** | heslo na prihlásenie do databázy |
|**Heslo** | heslo na prihlásenie do databázy |
|**Pripojiť cez SSL** | Či sa má použiť zabezpečené pripojenie (SSL). |
|**Meno cieľovej tabuľky** | Meno tabuľky v procese, do ktorej sa uložia dáta extrahované z externej databázy. |
|**SQL dotaz** | SQL dotaz, ktorý získa dáta z externej databázy. |
|**Stĺpce primárneho kľúča** | *(nepovinné)* Mená stĺpcov, ktoré tvoria primárny kľúč tabuľky, oddelené čiarkami |
|**Indexed columns** | *(nepovinné)* Mená stĺpcov, pre ktoré vytvoriť indexy, oddelené čiarkami. Môže zvýšiť výkon databázy |

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:---|:---:|:---:|:---|:---:|
|outputTables |výstup| RelationalDataUnit | Naplnené databázové tabuľky |áno|
