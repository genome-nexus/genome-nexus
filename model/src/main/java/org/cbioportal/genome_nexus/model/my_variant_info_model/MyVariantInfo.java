package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "my_variant_info.annotation")
public class MyVariantInfo
{
    @Id
    private String hgvs; // the hgvs id
    private Integer version;

    //private Snpeff snpeff;
    private Vcf vcf;
    private Dbsnp dbsnp;
    private Cosmic cosmic;
    private ClinVar clinVar;
    private Mutdb mutdb;

    @Field(value="id")
    public String getHgvs()
    {
      return hgvs;
    }

    public void setHgvs(String hgvs)
    {
      this.hgvs = hgvs;
    }

    @Field(value="version")
    public Integer getVersion()
    {
      return version;
    }

    public void setVersion(Integer version)
    {
      this.version = version;
    }

    // @Field(value="snpeff")
    // public Snpeff getSnpeff()
    // {
    //   return snpeff;
    // }

    // public void setSnpeff(Snpeff snpeff)
    // {
    //   this.snpeff = snpeff;
    // }

    @Field(value="vcf")
    public Vcf getVcf()
    {
      return vcf;
    }

    public void setVcf(Vcf vcf)
    {
      this.vcf = vcf;
    }

    @Field(value = "dbsnp")
    public Dbsnp getDbsnp() 
    {
      return dbsnp;
    }

    public void setDbsnp(Dbsnp dbsnp) 
    {
      this.dbsnp = dbsnp;
    }
    
    @Field(value = "cosmic")
    public Cosmic getCosmic() 
    {
      return cosmic;
    }

    public void setCosmic(Cosmic cosmic) 
    {
      this.cosmic = cosmic;
    }
    
    @Field(value = "clinvar")
    public ClinVar getClinVar() 
    {
      return clinVar;
    }

    public void setClinVar(ClinVar clinVar) 
    {
      this.clinVar = clinVar;
    }

      @Field(value = "mutdb")
    public Mutdb getMutdb() {
      return mutdb;
    }

    public void setMutdb(Mutdb mutdb) {
      this.mutdb = mutdb;
    }
}
