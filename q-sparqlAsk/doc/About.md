### Description

Executes given SPARQL ASK query and if it returns “NO”, it is interpreted in a way that examined data fail to satisfy certain constraint. 

In case the SPARQL ASK query returns “NO”, error or warning is thrown (by default, it is error, but user can customize that). User may also customize the error/warning message thrown in that case. 

It copies input data to the output, so it should be placed directly in the middle of the data flow. 

### Configuration parameters

| Name | Description |
|:----|:----|
|**Message Type** | Type of the message thrown in case the SPARQL ASK query returns “NO” |
|**Message** | Message thrown in case the SPARQL ASK query returns “NO” |
|**SPARQL ASK query** | The SPARQL ASK query to be executed. |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input |i| RdfDataUnit | Data to be checked |x|
|output |o| RdfDataUnit | Data checked |x|

