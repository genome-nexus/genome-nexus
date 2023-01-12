package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "version")
public class SourceVersionInfo {
    @Indexed
    @Field("id")
    public String id;
    public String name;
    public String version;
    public String type;
    public String description;
    public String url;

    public String getId() {
        return id;
    };

    public void setid(String id) {
        this.id = id;
    };

    public String getName() {
        return name;
    };

    public void setName(String name) {
        this.name = name;
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    };

    public void setType(String type) {
        this.type = type;
    };

    public String getDescription() {
        return description;
    };

    public void setDescription(String description) {
        this.description = description;
    };
}
