E-FilesDownload
----------

### Documentation

* see [Plugin Documentation](./doc/About.md)
* see [Plugin Documentation](./doc/About_sk.md) (in Slovak)

### Technical notes

*  Replaces
 * E-FilesFromLocal (eu.unifiedviews.plugins.uv-e-filesFromLocal)
 * E-HttpDownload (cz.cuni.mff.xrg.uv.e-HttpDownload).
 * E-HttpDownload (eu.unifiedviews.plugins.uv-e-httpDownload)
 * E-FilesFromScp (cz.cuni.mff.xrg.uv.e-filesFromScp)

*** 

By default, E-FilesDownload allows all protocols supported by Apache VFS in default configuration, see [doc page](https://commons.apache.org/proper/commons-vfs/filesystems.html)
 
Allowed protocols can be configured in UnifiedViews both **backend.properties** and **frontend.properties** configuration files.

| Property Name | Description |
|:----|:----|
|`dpu.uv-e-filesDownload.supported.protocols`  | Comma separated list of allowed protocols |

Example:

```INI
dpu.uv-e-filesDownload.supported.protocols = http,https,file,ftp,ftps,sftp
``` 
 
### Version history

* see [Changelog](./CHANGELOG.md)

