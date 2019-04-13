package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class AlleleCount
{

    // allele count in total
    @Field(value = "ac")
    private Integer ac;

    // allele count African
    @Field(value = "ac_afr")
    private Integer acAfr;

    // allele count Latino
    @Field(value = "ac_amr")
    private Integer acAmr;

    // allele count Ashkenazi Jewish
    @Field(value = "ac_asj")
    private Integer acAsj;

    // allele count East Asian
    @Field(value = "ac_eas")
    private Integer acEas;

    // allele count European(Finnish)
    @Field(value = "ac_fin")
    private Integer acFin;

    // allele count European(non-Finnish)
    @Field(value = "ac_nfe")
    private Integer acNfe;

    // allele count Other
    @Field(value = "ac_oth")
    private Integer acOth;

    // allele count South Asian
    @Field(value = "ac_sas")
    private Integer acSas;

    public Integer getAc()
    {
        return ac;
    }

    public void setAc(Integer ac)
    {
        this.ac = ac;
    }

    public Integer getAcAfr()
    {
        return acAfr;
    }

    public void setAcAfr(Integer acAfr)
    {
        this.acAfr = acAfr;
    }

    public Integer getAcAmr()
    {
        return acAmr;
    }

    public void setAcAmr(Integer acAmr)
    {
        this.acAmr = acAmr;
    }

    public Integer getAcAsj()
    {
        return acAsj;
    }

    public void setAcAsj(Integer acAsj)
    {
        this.acAsj = acAsj;
    }

    public Integer getAcEas()
    {
        return acEas;
    }

    public void setAcEas(Integer acEas)
    {
        this.acEas = acEas;
    }

    public Integer getAcFin()
    {
        return acFin;
    }

    public void setAcFin(Integer acFin)
    {
        this.acFin = acFin;
    }

    public Integer getAcNfe()
    {
        return acNfe;
    }

    public void setAcNfe(Integer acNfe)
    {
        this.acNfe = acNfe;
    }

    public Integer getAcOth()
    {
        return acOth;
    }

    public void setAcOth(Integer acOth)
    {
        this.acOth = acOth;
    }

    public Integer getAcSas()
    {
        return acSas;
    }

    public void setAcSas(Integer acSas)
    {
        this.acSas = acSas;
    }

}