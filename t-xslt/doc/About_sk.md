### Popis

Vykoná XSL Transformáciu vstupných súborov na `files` vstupe s využitím statického predpisu xslt.
Transformované súbory dá na výstup `files`.

V XSLT je podporované generovanie náhodných UUID pomocou `randomUUID()` funkcie v mennom priestore `uuid-functions`.

Príklad použitia:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    xmlns:uuid="uuid-functions"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="2.0">
    <xsl:template match="/">
        <xsl:value-of select="uuid:randomUUID()"/>
    </xsl:template>
</xsl:stylesheet>
```

### Konfiguračné parametre

| Name | Description |
|:----|:----|
| **Preskoč súbor ak sa vyskytne chyba** | Pri zaškrtnutej voľbe sa v prípade chyby pri transformácii súboru daná transformácia skončí a pokračuje sa ďalším súborom. Inak sa transformácia zastaví. |
| **Prípona výstupného súboru** | Pridá uvedenú príponu k menu vstupného súboru pre výstupný súbor |
| **Počet vlákien navyše** | Počet vlákien použitých pri transformácii. Ak sa nastaví 0, spustí sa jedno vlákno rovnako ako pri 1.<br>Toto nastavenie je užitočné pri transformáciách, ktoré trvajú dlhší čas.<br>Viac vlákien však znamená nielen zvýšenie rýchlosti, ale aj zvýšenie spotreby pamäte, takže treba zvážiť vhodné nastavenie |
| **XSLT šablóna** | XSLT šablóna použitá na tranformáciu |

### Vstupy a výstupy

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|files  |i| FilesDataUnit | Súbory určené na transformáciu  |x|
|files  |o| FilesDataUnit | Transformované súbory |x|
|config |i| RdfDataUnit | Dynamická RDF konfigurácia, pozri Pokročilá konfigurácia | |

### Pokročilá konfigurácia

Krok je možné nakonfigurovať aj dynamicky cez vstup `config` pomocou RDF dát.

Vzor konfigurácie:

```turtle
<http://localhost/resource/config> 
    <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://linked.opendata.cz/ontology/uv/dpu/xslt/Config>;
    <http://linked.opendata.cz/ontology/uv/dpu/xslt/fileInfo> <http://localhost/resource/fileInfo/0>;
    <http://linked.opendata.cz/ontology/uv/dpu/xslt/outputFileExtension> “.ttl”.
```

```turtle
<http://localhost/resource/fileInfo/0>
    <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://linked.opendata.cz/ontology/uv/dpu/xslt/FileInfo>;
    <http://linked.opendata.cz/ontology/uv/dpu/xslt/param> <http://localhost/resource/param/0>;
    <http://unifiedviews.eu/DataUnit/MetadataDataUnit/symbolicName> “smlouva.ttl”.
```

```turtle
<http://localhost/resource/param/0>
    <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://linked.opendata.cz/ontology/uv/dpu/xslt/Param>;
    <http://linked.opendata.cz/ontology/uv/dpu/xslt/param/name> “paramName”;
    <http://linked.opendata.cz/ontology/uv/dpu/xslt/param/value> “paramValue”.
```
