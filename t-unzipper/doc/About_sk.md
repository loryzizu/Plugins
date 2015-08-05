### Description

Extrahuje súbory komprimované metódou ZIP

### Configuration parameters

| Name | Description |
|:----|:----|
|**Preskoč súbory, ktorých spracovanie skončilo chybou (checkbox)** | Ake je zaškrtnuté, DPU zabráni prípadnej kolízii mien súborov v prípade extrahovania z viacerých zip súborov uložených v rovnakej alebo podobnej štruktúre.<br>Ak nie je zaškrtnuté, na výstup sa pošlú súbory s menami ako sú v zip súbore.<br>V prípade nejasností, nechajte voľbu zaškrtnutú |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |i| FilesDataUnit | Súbory na extrakciu |x|
|output |o| FilesDataUnit | Extrahované súbory |x|
