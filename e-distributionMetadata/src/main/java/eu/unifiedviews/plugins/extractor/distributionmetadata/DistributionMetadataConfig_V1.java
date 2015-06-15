package eu.unifiedviews.plugins.extractor.distributionmetadata;

import java.util.Date;

public class DistributionMetadataConfig_V1 {
    private String name;

    private String description;

    private java.net.URI url;

    private String format;

    private String mimetype;

    private Date created;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public java.net.URI getUrl() {
        return url;
    }

    public void setUrl(java.net.URI url) {
        this.url = url;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
