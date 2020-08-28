package org.cbioportal.genome_nexus.model.uniprot;

import org.springframework.data.mongodb.core.mapping.Field;

public class Evidence {
    @Field(value = "code")
    private String code;

    @Field(value = "label")
    private String label;

    @Field(value = "source")
    private DbReferenceObject source;

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public DbReferenceObject getSource()
    {
        return source;
    }

    public void setSource(DbReferenceObject source)
    {
        this.source = source;
    }
}