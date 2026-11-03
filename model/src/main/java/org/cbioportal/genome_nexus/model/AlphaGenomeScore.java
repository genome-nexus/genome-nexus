package org.cbioportal.genome_nexus.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

/** One gene-filtered scorer-by-track score row */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlphaGenomeScore
{
    private String outputType;
    private String variantScorer;
    private String geneId;
    private String geneName;
    private Integer junctionStart;
    private Integer junctionEnd;
    private String trackName;
    private String ontologyCurie;
    private String biosampleName;
    private Double rawScore;
    private Double quantileScore;

    @Field(value = "output_type")
    @JsonProperty(value = "output_type")
    public String getOutputType()
    {
        return outputType;
    }

    public void setOutputType(String outputType)
    {
        this.outputType = outputType;
    }

    @Field(value = "variant_scorer")
    @JsonProperty(value = "variant_scorer")
    public String getVariantScorer()
    {
        return variantScorer;
    }

    public void setVariantScorer(String variantScorer)
    {
        this.variantScorer = variantScorer;
    }

    @Field(value = "gene_id")
    @JsonProperty(value = "gene_id")
    public String getGeneId()
    {
        return geneId;
    }

    public void setGeneId(String geneId)
    {
        this.geneId = geneId;
    }

    @Field(value = "gene_name")
    @JsonProperty(value = "gene_name")
    public String getGeneName()
    {
        return geneName;
    }

    public void setGeneName(String geneName)
    {
        this.geneName = geneName;
    }

    @Field(value = "junction_start")
    @JsonProperty(value = "junction_start")
    public Integer getJunctionStart()
    {
        return junctionStart;
    }

    public void setJunctionStart(Integer junctionStart)
    {
        this.junctionStart = junctionStart;
    }

    @Field(value = "junction_end")
    @JsonProperty(value = "junction_end")
    public Integer getJunctionEnd()
    {
        return junctionEnd;
    }

    public void setJunctionEnd(Integer junctionEnd)
    {
        this.junctionEnd = junctionEnd;
    }

    @Field(value = "track_name")
    @JsonProperty(value = "track_name")
    public String getTrackName()
    {
        return trackName;
    }

    public void setTrackName(String trackName)
    {
        this.trackName = trackName;
    }

    @Field(value = "ontology_curie")
    @JsonProperty(value = "ontology_curie")
    public String getOntologyCurie()
    {
        return ontologyCurie;
    }

    public void setOntologyCurie(String ontologyCurie)
    {
        this.ontologyCurie = ontologyCurie;
    }

    @Field(value = "biosample_name")
    @JsonProperty(value = "biosample_name")
    public String getBiosampleName()
    {
        return biosampleName;
    }

    public void setBiosampleName(String biosampleName)
    {
        this.biosampleName = biosampleName;
    }

    @Field(value = "raw_score")
    @JsonProperty(value = "raw_score")
    public Double getRawScore()
    {
        return rawScore;
    }

    public void setRawScore(Double rawScore)
    {
        this.rawScore = rawScore;
    }

    @Field(value = "quantile_score")
    @JsonProperty(value = "quantile_score")
    public Double getQuantileScore()
    {
        return quantileScore;
    }

    public void setQuantileScore(Double quantileScore)
    {
        this.quantileScore = quantileScore;
    }
}
