package org.cbioportal.genome_nexus.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * AlphaGenome annotation for one normalized GRCh38 variant and its
 * canonical gene
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "alphagenome.annotation")
public class AlphaGenome
{
    @Id
    private String variant;
    private String hg38Variant;
    private String scoredInterval;
    private String modelVersion;
    private String canonicalGeneId;
    private String canonicalGene;
    private Double mergedSplicingScore;
    private String scoreLevel;
    private List<AlphaGenomeScore> scores;

    public String getVariant()
    {
        return variant;
    }

    public void setVariant(String variant)
    {
        this.variant = variant;
    }

    @Field(value = "hg38_variant")
    @JsonProperty(value = "hg38_variant")
    public String getHg38Variant()
    {
        return hg38Variant;
    }

    public void setHg38Variant(String hg38Variant)
    {
        this.hg38Variant = hg38Variant;
    }

    @Field(value = "scored_interval")
    @JsonProperty(value = "scored_interval")
    public String getScoredInterval()
    {
        return scoredInterval;
    }

    public void setScoredInterval(String scoredInterval)
    {
        this.scoredInterval = scoredInterval;
    }

    @Field(value = "model_version")
    @JsonProperty(value = "model_version")
    public String getModelVersion()
    {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion)
    {
        this.modelVersion = modelVersion;
    }

    @Field(value = "canonical_gene_id")
    @JsonProperty(value = "canonical_gene_id")
    public String getCanonicalGeneId()
    {
        return canonicalGeneId;
    }

    public void setCanonicalGeneId(String canonicalGeneId)
    {
        this.canonicalGeneId = canonicalGeneId;
    }

    @Field(value = "canonical_gene")
    @JsonProperty(value = "canonical_gene")
    public String getCanonicalGene()
    {
        return canonicalGene;
    }

    public void setCanonicalGene(String canonicalGene)
    {
        this.canonicalGene = canonicalGene;
    }

    @Field(value = "merged_splicing_score")
    @JsonProperty(value = "merged_splicing_score")
    public Double getMergedSplicingScore()
    {
        return mergedSplicingScore;
    }

    public void setMergedSplicingScore(Double mergedSplicingScore)
    {
        this.mergedSplicingScore = mergedSplicingScore;
    }

    @Field(value = "score_level")
    @JsonProperty(value = "score_level")
    public String getScoreLevel()
    {
        return scoreLevel;
    }

    public void setScoreLevel(String scoreLevel)
    {
        this.scoreLevel = scoreLevel;
    }

    @Field(value = "scores")
    @JsonProperty(value = "scores")
    public List<AlphaGenomeScore> getScores()
    {
        return scores;
    }

    public void setScores(List<AlphaGenomeScore> scores)
    {
        this.scores = scores;
    }
}
