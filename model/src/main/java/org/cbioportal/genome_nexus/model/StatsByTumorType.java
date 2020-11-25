package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class StatsByTumorType {

    @Field("tumor_type")
    private String tumorType;

    @Field("n_cancer_type_count")
    private Integer nCancerTypeCount;

    @Field("f_cancer_type_count")
    private Double fCancerTypeCount;

    @Field("f_biallelic")
    private Double fBiallelic;

    @Field("age_at_dx")
    private Integer ageAtDx;

    @Field("tmb")
    private Double tmb;

    @Field("msi_score")
    private Double msiScore;

    @Field("n_with_sig")
    private Integer nWithSig;

    @Field("hrd_score")
    private HrdScore hrdScore;

    public String getTumorType() {
        return tumorType;
    }

    public Integer getNWithSig() {
        return nWithSig;
    }

    public void setNWithSig(Integer nWithSig) {
        this.nWithSig = nWithSig;
    }

    public Double getMsiScore() {
        return msiScore;
    }

    public void setMsiScore(Double msiScore) {
        this.msiScore = msiScore;
    }

    public Double getTmb() {
        return tmb;
    }

    public void setTmb(Double tmb) {
        this.tmb = tmb;
    }

    public Integer getAgeAtDx() {
        return ageAtDx;
    }

    public void setAgeAtDx(Integer ageAtDx) {
        this.ageAtDx = ageAtDx;
    }

    public Double getfBiallelic() {
        return fBiallelic;
    }

    public void setfBiallelic(Double fBiallelic) {
        this.fBiallelic = fBiallelic;
    }

    public Double getfCancerTypeCount() {
        return fCancerTypeCount;
    }

    public void setfCancerTypeCount(Double fCancerTypeCount) {
        this.fCancerTypeCount = fCancerTypeCount;
    }

    public Integer getnCancerTypeCount() {
        return nCancerTypeCount;
    }

    public void setnCancerTypeCount(Integer nCancerTypeCount) {
        this.nCancerTypeCount = nCancerTypeCount;
    }

    public HrdScore getHrdScore() {
        return hrdScore;
    }

    public void setHrdScore(HrdScore hrdScore) {
        this.hrdScore = hrdScore;
    }

    public void setTumorType(String tumorType) {
        this.tumorType = tumorType;
    }


}
