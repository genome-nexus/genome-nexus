package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

public class Snpeff
{
    private String license;

    //private List<Ann> ann;

    @Field(value="license")
    public String getLicense()
    {
      return license;
    }

    public void setLicense(String license)
    {
      this.license = license;
    }

    // @Field(value="ann")
    // public List<Ann> getAnn()
    // {
    //   return ann;
    // }

    // public void setAnn(List<Ann> ann)
    // {
    //   this.ann = ann;
    // }
}
