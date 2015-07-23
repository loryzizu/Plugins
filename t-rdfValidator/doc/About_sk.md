### Popis

Overuje RDF dáta

### Konfiguračné parametre

| Meno | Popis |
|:----|:----|
|**Validačný dotaz**|SPARQL dotat typu ASK alebo SELECT. Ak ASK vráti hodnotu *TRUE*, validácia zlyhala, ak SELECT vráti nenulovú n-ticu = validácia zlyhala|
|**Zlyhanie vykonávania v prípade chyby validácie**|ak je checkbox aktívny, DPU vyhodí výnimku ak validácia zlyhá a zapríčiní zlyhanie celého procesu. V opačnom prípade DPU iba zaloguje výsledok a ukončí sa úspešne.|

### Vstupy a výstupy ###

|Meno |Typ | Dátová hrana | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|rdfInput|i|RDFDataUnit|Vstup RDF na overenie|x|
|rdfOutput|o|RDFDataUnit|Kópia vstupných dát||