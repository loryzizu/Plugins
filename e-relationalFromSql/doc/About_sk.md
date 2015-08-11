### Popis

Načíta relačné dáta (tabuľku) z externej relačnej databázy.

Používa sa na načítanie dát z relačnej databázy pomocou SQL dotazov a následné uloženie do internej dátovej hrany určenej pre relačné dáta.
Tento Krok umožňuje využiť niektoré vlastnosti pre načítanie dát z databázy: zoznam tabuliek v zdrojovej databáze, generovanie SELECT dotazu pre zvolenú tabuľku, preview údajov.
Podporované je aj bezpečné pripojenie do externej databázy cez SSL.

Krok podporuje nasledujúce databázy:
* PostgreSQL
* Oracle
* MySQL
* Microsoft SQL

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Database type** | Typ databázy: PostgreSQL, Oracle, MySQL, MS SQL |
|**Database host** | Database host |
|**Database port** | Database port |
|**Database name** | Meno databázy (pre ORACLE SID) |
|**Instance name** | *(voliteľný)* Meno inštancie databázy - pre MSSQL |
|**User name** | Database user name |
|**User password** | Database password |
|**Connect via SSL** | Use secured connection to the database |
|**Truststore location** | *(optional)* Path to truststore file to be used to validate server's certificate. If not filled, default Java truststore is used |
|**Truststore password** | *(optional)* Truststore password |
|**SQL query** | SQL query to extract data from source database |
|**Target table name** |T able name used to internally store the extracted data |
|**Primary key columns** | *(optional)* Target table primary key |
|**Indexed columns** | *(optional)* Target table indexed columns |

### Vstupy a výstupy

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:---|:---:|:---:|:---|:---:|
|outputTables |o| RelationalDataUnit | Naplnené databázové tabuľky |x|
