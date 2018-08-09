package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Snpeff
{

    @Field(value = "license")
    private String license;

    // @Field(value = "ann")
    // private List<Ann> ann;


    public String getLicense()
    {
        return license;
    }

    public void setLicense(String license)
    {
        this.license = license;
    }
    // public List<Ann> getAnn()
    // {
    //     return ann;
    // }

    // public void setAnn(List<Ann> ann)
    // {
    //     this.ann = ann;
    // }

}