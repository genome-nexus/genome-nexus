package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Ann
{

	@Field(value = "distance_to_feature")
	public String distanceToFeature;

	public String getDistanceToFeature()
	{
		return distanceToFeature;
	}

	public void setDistanceToFeature(String distanceToFeature)
	{
		this.distanceToFeature = distanceToFeature;
	}
	@Field(value = "effect")
	public String effect;

	public String getEffect()
	{
		return effect;
	}

	public void setEffect(String effect)
	{
		this.effect = effect;
	}
	@Field(value = "feature_id")
	public String featureId;

	public String getFeatureId()
	{
		return featureId;
	}

	public void setFeatureId(String featureId)
	{
		this.featureId = featureId;
	}
	@Field(value = "feature_type")
	public String featureType;

	public String getFeatureType()
	{
		return featureType;
	}

	public void setFeatureType(String featureType)
	{
		this.featureType = featureType;
	}
	@Field(value = "gene_id")
	public String geneId;

	public String getGeneId()
	{
		return geneId;
	}

	public void setGeneId(String geneId)
	{
		this.geneId = geneId;
	}
	@Field(value = "gene_name")
	public String geneName;

	public String getGeneName()
	{
		return geneName;
	}

	public void setGeneName(String geneName)
	{
		this.geneName = geneName;
	}
	@Field(value = "hgvs_coding")
	public String hgvsCoding;

	public String getHgvsCoding()
	{
		return hgvsCoding;
	}

	public void setHgvsCoding(String hgvsCoding)
	{
		this.hgvsCoding = hgvsCoding;
	}
	@Field(value = "putative_impact")
	public String putativeImpact;

	public String getPutativeImpact()
	{
		return putativeImpact;
	}

	public void setPutativeImpact(String putativeImpact)
	{
		this.putativeImpact = putativeImpact;
	}
	@Field(value = "transcript_biotype")
	public String transcriptBiotype;

	public String getTranscriptBiotype()
	{
		return transcriptBiotype;
	}

	public void setTranscriptBiotype(String transcriptBiotype)
	{
		this.transcriptBiotype = transcriptBiotype;
	}

}