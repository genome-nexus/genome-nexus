package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

public class Dbsnp {
    private String license;
    private String alleleOrigin;
    private List<Alleles> alleles;
    private String alt;
    private String chrom;
    private String _class;
    private Integer dbsnpBuild;
    private List<String> flags;
    private Gene gene;
    private Hg19 hg19;
    private String ref;
    private String rsid;
    private Boolean validated;
    private String varSubtype;
    private String vartype;


    @Field(value = "license")
    public String getLicense()
    {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }
    
    @Field(value = "allele_origin")
    public String getAllele_origin() {
        return alleleOrigin;
    }

    public void setAllele_origin(String alleleOrigin) {
        this.alleleOrigin = alleleOrigin;
    }

    @Field(value = "alleles")
    public List<Alleles> getAlleles() {
        return alleles;
    }

    public void setAlleles(List<Alleles> alleles) {
        this.alleles = alleles;
    }

    @Field(value = "alt")
    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    @Field(value = "chrom")
    public String getChrom() {
        return chrom;
    }

    public void setChrom(String chrom) {
        this.chrom = chrom;
    }

    @Field(value = "_class")
    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    @Field(value = "dbsnp_build")
    public Integer getDbsnpBuild() {
        return dbsnpBuild;
    }

    public void setDbsnpBuild(Integer dbsnpBuild) {
        this.dbsnpBuild = dbsnpBuild;
    }

    @Field(value = "flags")
    public List<String> getFlags() 
    {
    return flags;
    }

    public void setFlag(List<String> flags) 
    {
    this.flags = flags;
    }

    @Field(value = "gene")
    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }

    @Field(value = "hg19")
    public Hg19 getHg19() {
        return hg19;
    }

    public void setHg19(Hg19 hg19) {
        this.hg19 = hg19;
    }

    @Field(value = "ref")
    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    @Field(value = "rsid")
    public String getRsid() {
        return rsid;
    }

    public void setRsid(String rsid) {
        this.rsid = rsid;
    }

    @Field(value = "validated")
    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    @Field(value = "var_subtype")
    public String getVarSubtype() {
        return varSubtype;
    }

    public void setVarSubtype(String var_subtype) {
        this.varSubtype = varSubtype;
    }

    @Field(value = "vartype")
    public String getVartype() {
        return vartype;
    }

    public void setVartype(String vartype) {
        this.vartype = vartype;
    }

}
