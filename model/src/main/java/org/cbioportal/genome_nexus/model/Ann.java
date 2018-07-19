package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Ann {
  private String distanceToFeature;
  private String effect;
  private String featureId;
  private String featureType;
  private String geneId;
  private String geneName;
  private String hgvsCoding;
  private String putativeImpact;
  private String transcriptBiotype;

  @Field(value = "distance_to_feature")
  public String getDistanceToFeature() {
    return distanceToFeature;
  }

  public void setDistanceToFeature(String distanceToFeature) {
    this.distanceToFeature = distanceToFeature;
  }

  @Field(value = "effect")
  public String getEffect() {
    return effect;
  }

  public void setEffect(String effect) {
    this.effect = effect;
  }

  @Field(value = "feature_id")
  public String getFeatureId() {
    return featureId;
  }

  public void setFeatureId(String featureId) {
    this.featureId = featureId;
  }

  @Field(value = "feature_type")
  public String getFeatureType() {
    return featureType;
  }

  public void setFeatureType(String featureType) {
    this.featureType = featureType;
  }

  @Field(value = "gene_id")
  public String getGeneId() {
    return geneId;
  }

  public void setGeneId(String geneId) {
    this.geneId = geneId;
  }

  @Field(value = "genename")
  public String getGeneName() {
    return geneName;
  }

  public void setGeneName(String geneName) {
    this.geneName = geneName;
  }

  @Field(value = "hgvs_c")
  public String getHgvsCoding() {
    return hgvsCoding;
  }

  public void setHgvsC(String hgvsCoding) {
    this.hgvsCoding = hgvsCoding;
  }

  @Field(value = "putative_impact")
  public String getPutativeImpact() {
    return putativeImpact;
  }

  public void setPutativeImpact(String putativeImpact) {
    this.putativeImpact = putativeImpact;
  }

  @Field(value = "transcript_biotype")
  public String getTranscriptBiotype() {
    return transcriptBiotype;
  }

  public void setTranscriptBiotype(String transcriptBiotype) {
    this.transcriptBiotype = transcriptBiotype;
  }
}
