package org.cbioportal.genome_nexus.annotation.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mutation_assessor")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MutationAssessor
{
    @Id
    private String variant;

    private String hugoSymbol;
    private int proteinPosition;
    private String aminoAcidVariant;
    private String functionalImpact;
    private double functionalImpactScore;

    @ApiModelProperty(value = "User-input variants", required = true)
    public String getVariant() { return this.variant; }

    public void setVariant(String variant) { this.variant = variant; }

    @ApiModelProperty(value = "Hugo gene symbol", required = false)
    @JsonProperty("hugoSymbol")
    public String getHugoSymbol() { return hugoSymbol; }

    @JsonProperty("gene")
    public void setGene(String hugoSymbol) { this.hugoSymbol = hugoSymbol; }

    @ApiModelProperty(value = "Protein position (RefSeq)", required = false)
    @JsonProperty("proteinPosition")
    public int getProteinPosition() { return this.proteinPosition; }

    @JsonProperty("rs_pos")
    public void setProtPosition(int proteinPosition) { this.proteinPosition = proteinPosition; }

    @ApiModelProperty(value = "Amino acid variant", required = false)
    @JsonProperty("aminoAcidVariant")
    public String getAminoAcidVariant() { return this.aminoAcidVariant; }

    @JsonProperty("rgaa")
    public void setAAVariant(String aminoAcidVariant) { this.aminoAcidVariant = aminoAcidVariant; }

    @ApiModelProperty(value = "Functional impact", required = false)
    @JsonProperty("functionalImpact")
    public String getFunctionalImpact() { return this.functionalImpact; }

    @JsonProperty("F_impact")
    public void setFuncImpact(String functionalImpact) { this.functionalImpact = functionalImpact; }

    @ApiModelProperty(value = "Functional impact score", required = false)
    @JsonProperty("functionalImpactScore")
    public double getFunctionalImpactScore() {
        return this.functionalImpactScore;
    }

    @JsonProperty("F_score")
    public void setFuncImpactScore(double functionalImpactScore) {
        this.functionalImpactScore = functionalImpactScore;
    }

}
