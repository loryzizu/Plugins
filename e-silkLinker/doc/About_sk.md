### Popis

Vytvorí prepojenia medzi RDF zdrojmi založenými na Silk Link Specification Language (LSL), https://www.assembla.com/spaces/silk/wiki/Link_Specification_Language. Skript obsahujúci špecifikáciu prepojovacej úlohy môže byť nahraný z XML súboru alebo priamo nakopírovaný do konfigurácie DPU. Výstupná časť takéhoto skriptu je vždy ignorovaná, výstup sa zapíše do dvoch výstupných dátových jednotiek DPU - "links_confirmed", "links_to_be_verified". DPU konfigurácia musí takisto špecifikovať vstupy pre tieto dve DPU - vstupy pre prepojenia, ktoré sú potvrdené a ktoré by mali byť overené.

### Konfiguračné parametre

| Názov | Popis |
|:----|:----|
|**Umiestnenie Silk knižnice** | Cesta k vykonateľnému Silk JAR súboru, keďže Silk je vykonávaný ako externý proces |
|**Silk konfiguračný súbor** | XML konfiguračný súbor pre Silk. Môťe byť nahraný zo súboru alebo priamo vložený do textového poľa nižšie |
|**Minimálna hodnota pre prepojenia považované za potvrdené** | Zadajte prosím minimálnu hodnotu (v rozsahu 0-1) pre prepojenia, ktoré sa majú považovať za potvrdené (teda sú pravdepodobne správne) |
|**Minimálna hodnota pre prepojenia, ktoré majú byť overené** | Zadajte prosím minimálnu hodnotu (v rozsahu 0-1) pre prepojenia, ktoré môžu byť správne, ale vyžadujú dodatočné overenie. Táto hodnota musí byť nižšia než ‘Minimálna hodnota pre prepojenia považované za potvrdené’ |

### Vstupy a výstupy

|Názov |Typ | Dátová jednotka | Popis | Povinné |
|:--------|:------:|:------:|:-------------|:---------------------:|
|links_confirmed |o |RdfDataUnit |Potvrdené prepojenia |x|
|links_to_be_verified |o |RdfDataUnit |Prepojenia, ktoré majú byť overené | |
