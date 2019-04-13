package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class AlleleNumber
{

    // allele number in total
    @Field(value = "an")
    private Integer an;

    // allele number African
    @Field(value = "an_afr")
    private Integer anAfr;

    // allele number Latino
    @Field(value = "an_amr")
    private Integer anAmr;

    // allele number Ashkenazi Jewish
    @Field(value = "an_asj")
    private Integer anAsj;

    // allele number East Asian
    @Field(value = "an_eas")
    private Integer anEas;

    // allele number European(Finnish)
    @Field(value = "an_fin")
    private Integer anFin;

    // allele number European(non-Finnish)
    @Field(value = "an_nfe")
    private Integer anNfe;

    // allele number Other
    @Field(value = "an_oth")
    private Integer anOth;

    // allele number South Asian
    @Field(value = "an_sas")
    private Integer anSas;

    public Integer getAn()
    {
        return an;
    }

    public void setAn(Integer an)
    {
        this.an = an;
    }

    public Integer getAnAfr()
    {
        return anAfr;
    }

    public void setAnAfr(Integer anAfr)
    {
        this.anAfr = anAfr;
    }

    public Integer getAnAmr()
    {
        return anAmr;
    }

    public void setAnAmr(Integer anAmr)
    {
        this.anAmr = anAmr;
    }

    public Integer getAnAsj()
    {
        return anAsj;
    }

    public void setAnAsj(Integer anAsj)
    {
        this.anAsj = anAsj;
    }

    public Integer getAnEas()
    {
        return anEas;
    }

    public void setAnEas(Integer anEas)
    {
        this.anEas = anEas;
    }

    public Integer getAnFin()
    {
        return anFin;
    }

    public void setAnFin(Integer anFin)
    {
        this.anFin = anFin;
    }

    public Integer getAnNfe()
    {
        return anNfe;
    }

    public void setAnNfe(Integer anNfe)
    {
        this.anNfe = anNfe;
    }

    public Integer getAnOth()
    {
        return anOth;
    }

    public void setAnOth(Integer anOth)
    {
        this.anOth = anOth;
    }

    public Integer getAnSas()
    {
        return anSas;
    }

    public void setAnSas(Integer anSas)
    {
        this.anSas = anSas;
    }

}