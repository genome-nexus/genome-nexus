package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Vcf
{
    private String alt;
    private String position;
    private String ref;

    @Field(value="alt")
    public String getAlt()
    {
      return alt;
    }

    public void setAlt(String alt)
    {
      this.alt = alt;
    }

    @Field(value= "position")
    public String getPosition()
    {
      return position;
    }

    public void setPosition(String position)
    {
      this.position = position;
    }

    @Field(value="ref")
    public String getRef()
    {
      return ref;
    }

    public void setRef(String ref)
    {
      this.ref = ref;
    }
}
