L-RdfToVirtuoso
----------

### Documentation

* see [Plugin Documentation](./doc/About.md)
* see [Plugin Documentation](./doc/About_sk.md) (in Slovak)

### Technical notes

* Uses Virtuoso Sesame Provider 1.21 (Virtuoso 7 Develop branch) which works with Virtuoso 7
* Depending on number of graphs on the input, it works in 2 modes:
 * In single graph mode: One RDF graph with CKAN Resource set, with VirtualGraph set to real graph name, where the data were loaded.
 * In per-graph mode: RDF graphs from input with CKAN Resource set, with VirtualGraph set to real graph name, where the data were loaded.

### Version history

* see [Changelog](./CHANGELOG.md)
