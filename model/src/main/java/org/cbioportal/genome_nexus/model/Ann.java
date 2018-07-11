package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Ann
{
    private String distance_to_feature;
    private String effect;
    private String feature_id;
    private String feature_type;
    private String gene_id;
    private String gene_name;
    private String hgvs_c;
    private String putative_impact;
    private String transcript_biotype;

    @Field(value="distance_to_feature")
    public String getDistanceToFeature()
    {
      return distance_to_feature;
    }

    public void setDistanceToFeature(String distance_to_feature)
    {
      this.distance_to_feature = distance_to_feature;
    }

    @Field(value="effect")
    public String getEffect()
    {
      return effect;
    }

    public void setEffect(String effect)
    {
      this.effect = effect;
    }

    @Field(value="feature_id")
    public String getFeatureId()
    {
      return feature_id;
    }

    public void setFeatureId(String feature_id)
    {
      this.feature_id = feature_id;
    }

    @Field(value="feature_type")
    public String getFeatureType()
    {
      return feature_type;
    }

    public void setFeatureType(String feature_type)
    {
      this.feature_type = feature_type;
    }

    @Field(value="gene_id")
    public String getGeneId()
    {
      return gene_id;
    }

    public void setGeneId(String gene_id)
    {
      this.gene_id = gene_id;
    }

    @Field(value="gene_name")
    public String getGeneName()
    {
      return gene_name;
    }

    public void setGeneName(String gene_name)
    {
      this.gene_name = gene_name;
    }

    @Field(value="hgvs_c")
    public String getHgvsC()
    {
      return hgvs_c;
    }

    public void setHgvsC(String hgvs_c)
    {
      this.hgvs_c = hgvs_c;
    }

    @Field(value="putative_impact")
    public String getPutativeImpact()
    {
      return putative_impact;
    }

    public void setPutativeImpact(String putative_impact)
    {
      this.putative_impact = putative_impact;
    }

    @Field(value="transcript_biotype")
    public String getTranscriptBiotype()
    {
      return transcript_biotype;
    }

    public void setTranscriptBiotype(String transcript_biotype)
    {
      this.transcript_biotype = transcript_biotype;
    }
}
