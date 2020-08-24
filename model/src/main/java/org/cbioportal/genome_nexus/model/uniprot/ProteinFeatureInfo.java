package org.cbioportal.genome_nexus.model.uniprot;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

public class ProteinFeatureInfo {

    @Field(value = "version")
    private String version;

    @Field(value = "accession")
    private String accession;

    @Field(value = "entryName")
    private String entryName;

    @Field(value = "proteinName")
    private String proteinName;

    @Field(value = "geneName")
    private String geneName;

    @Field(value = "organismName")
    private String organismName;

    @Field(value = "proteinExistence")
    private String proteinExistence;

    @Field(value = "sequence")
    private String sequence;

    @Field(value = "sequenceChecksum")
    private String sequenceChecksum;

    @Field(value = "sequenceVersion")
    private Integer sequenceVersion;

    @Field(value = "geteGeneId")
    private String geteGeneId;

    @Field(value = "geteProteinId")
    private String geteProteinId;

    @Field(value = "taxid")
    private Integer taxid;

    @Field(value = "features")
    private List<Feature> features;

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getAccession()
    {
        return accession;
    }

    public void setAccession(String accession)
    {
        this.accession = accession;
    }

    public String getEntryName()
    {
        return entryName;
    }

    public void setEntryName(String entryName)
    {
        this.entryName = entryName;
    }

    public String getProteinName()
    {
        return proteinName;
    }

    public void setProteinName(String proteinName)
    {
        this.proteinName = proteinName;
    }

    public String getGeneName()
    {
        return geneName;
    }

    public void setGeneName(String geneName)
    {
        this.geneName = geneName;
    }

    public String getOrganismName()
    {
        return organismName;
    }

    public void setOrganismName(String organismName)
    {
        this.organismName = organismName;
    }

    public String getProteinExistence()
    {
        return proteinExistence;
    }

    public void setProteinExistence(String proteinExistence)
    {
        this.proteinExistence = proteinExistence;
    }

    public String getSequence()
    {
        return sequence;
    }

    public void setSequence(String sequence)
    {
        this.sequence = sequence;
    }

    public String getSequenceChecksum()
    {
        return sequenceChecksum;
    }

    public void setSequenceChecksum(String sequenceChecksum)
    {
        this.sequenceChecksum = sequenceChecksum;
    }

    public Integer getSequenceVersion()
    {
        return sequenceVersion;
    }

    public void setSequenceVersion(Integer sequenceVersion)
    {
        this.sequenceVersion = sequenceVersion;
    }

    public String getGeteGeneId()
    {
        return geteGeneId;
    }

    public void setGeteGeneId(String geteGeneId)
    {
        this.geteGeneId = geteGeneId;
    }

    public String getGeteProteinId()
    {
        return geteProteinId;
    }

    public void setGeteProteinId(String geteProteinId)
    {
        this.geteProteinId = geteProteinId;
    }

    public Integer getTaxid()
    {
        return taxid;
    }

    public void setTaxid(Integer taxid)
    {
        this.taxid = taxid;
    }

    public List<Feature> getFeatures()
    {
        return features;
    }

    public void setFeatures(List<Feature> features)
    {
        this.features = features;
    }
}