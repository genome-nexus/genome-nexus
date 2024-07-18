package org.cbioportal.genome_nexus.model;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mutation_assessor.annotation")
public class MutationAssessor
{
    private String uniprotId;
    
    public String getUniprotId() {
        return uniprotId;
    }
    
    public void setUniprotId(String uniprotId) {
        this.uniprotId = uniprotId;
    }
    
    private Integer sv;
    
    public Integer getSv() {
        return sv;
    }
    
    public void setSv(Integer sv) {
        this.sv = sv;
    }
    
    private String hgvspShort;
    
    public String getHgvspShort() {
        return hgvspShort;
    }
    
    public void setHgvspShort(String hgvspShort) {
        this.hgvspShort = hgvspShort;
    }
    
    private Double f_score;
    
    public Double getF_score() {
        return f_score;
    }
    
    public void setF_score(Double f_score) {
        this.f_score = f_score;
    }
    
    private String f_impact;
    
    public String getF_impact() {
        return f_impact;
    }
    
    public void setF_impact(String f_impact) {
        this.f_impact = f_impact;
    }
   
    private String msa;
    
    public String getMsa() {
        return msa;
    }
    
    public void setMsa(String msa) {
        this.msa = msa;
    }
   
    private Integer mav;
  
    public Integer getMav() {
        return mav;
    }
   
    public void setMav(Integer mav) {
        this.mav = mav;
    }
}
