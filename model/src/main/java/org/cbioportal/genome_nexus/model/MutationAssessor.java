package org.cbioportal.genome_nexus.model;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    
    @Field("f_score")
    private Double functionalImpactScore;
    
    public Double getFunctionalImpactScore() {
        return functionalImpactScore;
    }
    
    public void setFunctionalImpactScore(Double functionalImpactScore) {
        this.functionalImpactScore = functionalImpactScore;
    }

    @Field("f_impact")
    private String functionalImpactPrediction;
    
    public String getFunctionalImpactPrediction() {
        return functionalImpactPrediction;
    }
    
    public void setFunctionalImpactPrediction(String functionalImpactPrediction) {
        this.functionalImpactPrediction = functionalImpactPrediction;
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
