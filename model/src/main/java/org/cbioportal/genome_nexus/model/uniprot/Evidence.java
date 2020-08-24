package org.cbioportal.genome_nexus.model.uniprot;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

public class Evidence {
    @Field(value = "code")
    private String code;

    @Field(value = "label")
    private String label;

    @Field(value = "source")
    private DbReferenceObject source;

    @Field(value = "ftId")
    private String ftId;

    @Field(value = "description")
    private String description;

    @Field(value = "alternativeSequence")
    private String alternativeSequence;

    @Field(value = "begin")
    private String begin;

    @Field(value = "end")
    private String end;

    @Field(value = "molecule")
    private String molecule;

    @Field(value = "xrefs")
    private List<DbReferenceObject> xrefs;

    @Field(value = "evidences")
    private List<Evidence> evidences;

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