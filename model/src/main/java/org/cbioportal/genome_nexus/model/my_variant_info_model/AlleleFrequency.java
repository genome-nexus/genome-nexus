package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class AlleleFrequency
{

    // allele frequency in total
    @Field(value = "af")
    private Integer af;

    // allele frequency African
    @Field(value = "af_afr")
    private Integer afAfr;

    // allele frequency Latino
    @Field(value = "af_amr")
    private Integer afAmr;

    // allele frequency Ashkenazi Jewish
    @Field(value = "af_asj")
    private Integer afAsj;

    // allele frequency East Asian
    @Field(value = "af_eas")
    private Integer afEas;

    // allele frequency European(Finnish)
    @Field(value = "af_fin")
    private Integer afFin;

    // allele frequency European(non-Finnish)
    @Field(value = "af_nfe")
    private Integer afNfe;

    // allele frequency Other
    @Field(value = "af_oth")
    private Integer afOth;

    // allele frequency South Asian
    @Field(value = "af_sas")
    private Integer afSas;

    public Integer getAf()
    {
        return af;
    }

    public void setAf(Integer af)
    {
        this.af = af;
    }

    public Integer getAfAfr()
    {
        return afAfr;
    }

    public void setAfAfr(Integer afAfr)
    {
        this.afAfr = afAfr;
    }

    public Integer getAfAmr()
    {
        return afAmr;
    }

    public void setAfAmr(Integer afAmr)
    {
        this.afAmr = afAmr;
    }

    public Integer getAfAsj()
    {
        return afAsj;
    }

    public void setAfAsj(Integer afAsj)
    {
        this.afAsj = afAsj;
    }

    public Integer getAfEas()
    {
        return afEas;
    }

    public void setAfEas(Integer afEas)
    {
        this.afEas = afEas;
    }

    public Integer getAfFin()
    {
        return afFin;
    }

    public void setAfFin(Integer afFin)
    {
        this.afFin = afFin;
    }

    public Integer getAfNfe()
    {
        return afNfe;
    }

    public void setAfNfe(Integer afNfe)
    {
        this.afNfe = afNfe;
    }

    public Integer getAfOth()
    {
        return afOth;
    }

    public void setAfOth(Integer afOth)
    {
        this.afOth = afOth;
    }

    public Integer getafSas()
    {
        return afSas;
    }

    public void setafSas(Integer afSas)
    {
        this.afSas = afSas;
    }

}