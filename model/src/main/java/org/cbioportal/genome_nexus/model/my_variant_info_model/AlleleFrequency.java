package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class AlleleFrequency
{

    // allele frequency in total
    @Field(value = "af")
    private Float af;

    // allele frequency African
    @Field(value = "af_afr")
    private Float afAfr;

    // allele frequency Latino
    @Field(value = "af_amr")
    private Float afAmr;

    // allele frequency Ashkenazi Jewish
    @Field(value = "af_asj")
    private Float afAsj;

    // allele frequency East Asian
    @Field(value = "af_eas")
    private Float afEas;

    // allele frequency European(Finnish)
    @Field(value = "af_fin")
    private Float afFin;

    // allele frequency European(non-Finnish)
    @Field(value = "af_nfe")
    private Float afNfe;

    // allele frequency Other
    @Field(value = "af_oth")
    private Float afOth;

    // allele frequency South Asian
    @Field(value = "af_sas")
    private Float afSas;

    public Float getAf()
    {
        return af;
    }

    public void setAf(Float af)
    {
        this.af = af;
    }

    public Float getAfAfr()
    {
        return afAfr;
    }

    public void setAfAfr(Float afAfr)
    {
        this.afAfr = afAfr;
    }

    public Float getAfAmr()
    {
        return afAmr;
    }

    public void setAfAmr(Float afAmr)
    {
        this.afAmr = afAmr;
    }

    public Float getAfAsj()
    {
        return afAsj;
    }

    public void setAfAsj(Float afAsj)
    {
        this.afAsj = afAsj;
    }

    public Float getAfEas()
    {
        return afEas;
    }

    public void setAfEas(Float afEas)
    {
        this.afEas = afEas;
    }

    public Float getAfFin()
    {
        return afFin;
    }

    public void setAfFin(Float afFin)
    {
        this.afFin = afFin;
    }

    public Float getAfNfe()
    {
        return afNfe;
    }

    public void setAfNfe(Float afNfe)
    {
        this.afNfe = afNfe;
    }

    public Float getAfOth()
    {
        return afOth;
    }

    public void setAfOth(Float afOth)
    {
        this.afOth = afOth;
    }

    public Float getafSas()
    {
        return afSas;
    }

    public void setafSas(Float afSas)
    {
        this.afSas = afSas;
    }

}