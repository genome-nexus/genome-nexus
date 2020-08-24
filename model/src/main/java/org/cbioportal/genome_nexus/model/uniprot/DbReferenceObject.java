package org.cbioportal.genome_nexus.model.uniprot;

import org.springframework.data.mongodb.core.mapping.Field;

public class DbReferenceObject {
    @Field(value = "name")
    private String name;

    @Field(value = "id")
    private String id;

    @Field(value = "url")
    private String url;

    @Field(value = "alternativeUrl")
    private String alternativeUrl;

    @Field(value = "reviewed")
    private Boolean reviewed;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getAlternativeUrl()
    {
        return alternativeUrl;
    }

    public void setAlternativeUrl(String alternativeUrl)
    {
        this.alternativeUrl = alternativeUrl;
    }

    public Boolean getReviewed()
    {
        return reviewed;
    }

    public void setReviewed(Boolean reviewed)
    {
        this.reviewed = reviewed;
    }
}