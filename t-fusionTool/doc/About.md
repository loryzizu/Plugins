### Description

The DPU fuses the input RDF data (which may come over "input" or "input2" input data units), taking into account  "sameAs" links (which may come over input data unit "sameAs"). Internally it uses the RDF data integration tool [LD-FusionTool](https://github.com/mifeet/LD-FusionTool)

The DPU is configured via its XML configuration file, which may be inserted in the dialog of the DPU. Sample configuration is available [here](https://github.com/mifeet/FusionTool-DPU/blob/master/examples/sample-config-full.xml).

The core part of the XML configuration is the configuration of conflict resolution policies; every such conflict resolution policy specifies how the conflicting values of the particular predicate are fused. For example, if conflict resolution function "VOTE" is used to resolve conflicting object values of the predicate "gr:legalName", then the following XML fragment should be used in the XML configuration:

```
<ConflictResolution>

  <ResolutionStrategy function=“VOTE”>
      <!-- Definition of properties to which the resolution strategy applies to -->
      <Property id="gr:legalName"/>
  </ResolutionStrategy>
 
</ConflictResolution>
```

Certain function may have also parameters, as for example the function "FILTER" depicted below: 

```
<ConflictResolution>

 <ResolutionStrategy function="FILTER">
      <!-- Definition of properties to which the resolution strategy applies to -->
      <Param name="min" value="1111"/>
      <Param name="max" value="9999"/>
      <Property id="http://www.w3.org/2004/02/skos/core#notation"/>
  </ResolutionStrategy>

</ConflictResolution>
```

The list of supported conflict resolution functions is as follows (the names of functions are case sensitive):

| Name of the function | Description |
|:----|:----|
|**ALL** | Returns all conflicting triples. |
|**ANY** | Returns an arbitrary (non-NULL) value. |
|**VOTE** | Returns the most-frequently occurring (non-NULL) value. |
|**BEST** | Returns the value with the highest data quality value. The quality of the selected value is influenced by the distance from other conflicting values and also by the number of other values supporting the value. It is similar to VOTE.  |
|**LONGEST, SHORTEST** | Returns the longest/shortest (non-NULL) value. |
|**MAX, MIN** | Returns the maximal/minimal (non-NULL) value according to an ordering of input values. |
|**CERTAIN** | If input values contain only one distinct (non-NULL) value, returns it. Otherwise returns NULL or empty output (depending on the underlying data model).|
|**FILTER** | Returns values within a given range. The minimum and/or maximum are given as parameters with names "min", "max", see the example above. |
|**THRESHOLD** | Returns values with data quality higher then a given threshold. The threshold is given as a parameter with name "threshold".|
|**AVG** | Returns the average of all (non-NULL) input values. |
|**MEDIAN** | Returns the median of all (non-NULL) input values. |
|**SUM** | Returns the sum of all (non-NULL) input values. |
|**CONCAT** | Returns a concatenation of all values. The separator of values may be given as a parameter. |
|**RANDOM** | Returns a random (non-NULL) value. The chosen value may differ among calls on the same input |
 
For further information about the library responsible for data fusion, please see [here](http://mifeet.github.io/LD-FusionTool/). 

### Configuration parameters

| Name | Description |
|:----|:----|
|**Configuration** | XML configuration which drives the data fusion. See above. |

### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|input  |i| RdfDataUnit | First source of RDF data to be fused |x|
|input2  |i| RdfDataUnit | Second source of RDF data to be fused | |
|sameAs  |i| RdfDataUnit | ‘owl:sameAs’ links to be used during data fusion | |
|metadata  |i| RdfDataUnit | Metadata used during conflict resolution | |
|output |o| RdfDataUnit | Fused data |x|

