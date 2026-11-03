package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class AlphaGenomeAnnotation
{

    @Field(value = "license")
    private String license;

    @Field(value = "annotation")
    private AlphaGenome annotation;


    public String getLicense()
    {
        return license;
    }

    public void setLicense(String license)
    {
        this.license = license;
    }

    public AlphaGenome getAnnotation()
    {
        return annotation;
    }

    public void setAnnotation(AlphaGenome annotation)
    {
        this.annotation = annotation;
    }

}
