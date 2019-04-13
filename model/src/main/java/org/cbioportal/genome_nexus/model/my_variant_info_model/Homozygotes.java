package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Homozygotes
{

    // number of homozygotes in total
    @Field(value = "hom")
    private Integer hom;

    // number of homozygotes African
    @Field(value = "hom_afr")
    private Integer homAfr;

    // number of homozygotes Latino
    @Field(value = "hom_amr")
    private Integer homAmr;

    // number of homozygotes Ashkenazi Jewish
    @Field(value = "hom_asj")
    private Integer homAsj;

    // number of homozygotes East Asian
    @Field(value = "hom_eas")
    private Integer homEas;

    // number of homozygotes European(Finnish)
    @Field(value = "hom_fin")
    private Integer homFin;

    // number of homozygotes European(non-Finnish)
    @Field(value = "hom_nfe")
    private Integer homNfe;

    // number of homozygotes Other
    @Field(value = "hom_oth")
    private Integer homOth;

    // number of homozygotes South Asian
    @Field(value = "hom_sas")
    private Integer homSas;

    public Integer getHom()
    {
        return hom;
    }

    public void setHom(Integer hom)
    {
        this.hom = hom;
    }

    public Integer getHomAfr()
    {
        return homAfr;
    }

    public void setHomAfr(Integer homAfr)
    {
        this.homAfr = homAfr;
    }

    public Integer getHomAmr()
    {
        return homAmr;
    }

    public void setHomAmr(Integer homAmr)
    {
        this.homAmr = homAmr;
    }

    public Integer getHomAsj()
    {
        return homAsj;
    }

    public void setHomAsj(Integer homAsj)
    {
        this.homAsj = homAsj;
    }

    public Integer getHomEas()
    {
        return homEas;
    }

    public void setHomEas(Integer homEas)
    {
        this.homEas = homEas;
    }

    public Integer getHomFin()
    {
        return homFin;
    }

    public void setHomFin(Integer homFin)
    {
        this.homFin = homFin;
    }

    public Integer getHomNfe()
    {
        return homNfe;
    }

    public void setHomNfe(Integer homNfe)
    {
        this.homNfe = homNfe;
    }

    public Integer getHomOth()
    {
        return homOth;
    }

    public void setHomOth(Integer homOth)
    {
        this.homOth = homOth;
    }

    public Integer getHomSas()
    {
        return homSas;
    }

    public void setHomSas(Integer homSas)
    {
        this.homSas = homSas;
    }

}