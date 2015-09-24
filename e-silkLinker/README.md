E-SilkLinker
----------

### Documentation

* see [Plugin Documentation](./doc/About.md)
* see [Plugin Documentation](./doc/About_sk.md) (in Slovak)

### Technical notes

* Limitation: Silk 2.6 is not supported due to change in the definition of outputs, see: https://github.com/UnifiedViews/Plugins/issues/337
* Limitation: Silk as a tool for linking RDF data is called as an external process. This should be improved in the future and silk library should be packed inside the DPU. So far, before using the DPU, the pipeline designer has to place somewhere the Silk executable JAR file and specify the path to such Silk JAR file in the configuration of the DPU. Please make sure that the DPU may access the Silk executable JAR. 
* DPU cannot be cancelled as the linkage job is running.

### Version history

* see [Changelog](./CHANGELOG.md)

