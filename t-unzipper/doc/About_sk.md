### Description

Rozbalí súbory komprimované metódou ZIP.

### Configuration parameters

| Name | Description |
|:----|:----|
|**Prevencia duplicitných názvov** | Ak je zaškrtnuté, je pred názov súboru z archívu pridaný náhodný reťazec, čím sa predíde duplicitným názvom v procese v prípade, že by existoval v procese nejaký iný súbor s rovnakým menom.

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |vstup| FilesDataUnit | Súbory na rozbalenie |áno|
|output |výstup| FilesDataUnit | Rozbalené súbory |áno|
