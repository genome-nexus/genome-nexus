package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class ClinVar
{

    @Field(value = "_license")
    private String license;

    @Field(value = "allele_id")
    private Integer alleleId;

    @Field(value = "alt")
    private String alt;

    @Field(value = "chrom")
    private String chrom;

    @Field(value = "cytogenic")
    private String cytogenic;

    @Field(value = "gene")
    private Gene gene;

    @Field(value = "hg19")
    private Hg19 hg19;

    @Field(value = "hg38")
    private Hg38 hg38;

    @Field(value = "hgvs")
    private Hgvs hgvs;


    public String get_license()
    {
        return license;
    }

    public void set_license(String license)
    {
        this.license = license;
    }
    public Integer getAlleleId()
    {
        return alleleId;
    }

    public void setAlleleId(Integer alleleId)
    {
        this.alleleId = alleleId;
    }
    public String getAlt()
    {
        return alt;
    }

    public void setAlt(String alt)
    {
        this.alt = alt;
    }
    public String getChrom()
    {
        return chrom;
    }

    public void setChrom(String chrom)
    {
        this.chrom = chrom;
    }
    public String getCytogenic()
    {
        return cytogenic;
    }

    public void setCytogenic(String cytogenic)
    {
        this.cytogenic = cytogenic;
    }
    public Gene getGene()
    {
        return gene;
    }

    public void setGene(Gene gene)
    {
        this.gene = gene;
    }
    public Hg19 getHg19()
    {
        return hg19;
    }

    public void setHg19(Hg19 hg19)
    {
        this.hg19 = hg19;
    }
    public Hg38 getHg38()
    {
        return hg38;
    }

    public void setHg38(Hg38 hg38)
    {
        this.hg38 = hg38;
    }
    public Hgvs getHgvs()
    {
        return hgvs;
    }

    public void setHgvs(Hgvs hgvs)
    {
        this.hgvs = hgvs;
    }

}