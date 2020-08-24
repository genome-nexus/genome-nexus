package org.cbioportal.genome_nexus.model.uniprot;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

public class Feature {
    @Field(value = "type")
    private String type;

    @Field(value = "category")
    private String category;

    @Field(value = "cvId")
    private String cvId;

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

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getCvId()
    {
        return cvId;
    }

    public void setCvId(String cvId)
    {
        this.cvId = cvId;
    }

    public String getFtId()
    {
        return ftId;
    }

    public void setFtId(String ftId)
    {
        this.ftId = ftId;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getAlternativeSequence()
    {
        return alternativeSequence;
    }

    public void setAlternativeSequence(String alternativeSequence)
    {
        this.alternativeSequence = alternativeSequence;
    }

    public String getBegin()
    {
        return begin;
    }

    public void setBegin(String begin)
    {
        this.begin = begin;
    }

    public String getEnd()
    {
        return end;
    }

    public void setEnd(String end)
    {
        this.end = end;
    }

    public String getMolecule()
    {
        return molecule;
    }

    public void setMolecule(String molecule)
    {
        this.molecule = molecule;
    }

    public List<DbReferenceObject> getXrefs()
    {
        return xrefs;
    }

    public void setXrefs(List<DbReferenceObject> xrefs)
    {
        this.xrefs = xrefs;
    }

    public List<Evidence> getEvidences()
    {
        return evidences;
    }

    public void setEvidences(List<Evidence> evidences)
    {
        this.evidences = evidences;
    }

}