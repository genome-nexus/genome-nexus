package org.cbioportal.genome_nexus.annotation.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String functionalImpact;
    private double functionalImpactScore;

    @ApiModelProperty(value = "User-input variants", required = true)
    public String getVariant() { return this.variant; }

    public void setVariant(String variant) { this.variant = variant; }

    @ApiModelProperty(value = "Hugo gene symbol", required = false)
    public String getHugoSymbol() { return hugoSymbol; }

    @JsonProperty("gene")
    public void setHugoSymbol(String hugoSymbol) { this.hugoSymbol = hugoSymbol; }

    @ApiModelProperty(value = "Functional impact", required = false)
    public String getFunctionalImpact() { return this.functionalImpact; }

    @JsonProperty("F_impact")
    public void setFunctionalImpact(String functionalImpact) { this.functionalImpact = functionalImpact; }

    @ApiModelProperty(value = "Functional impact score", required = false)
    public double getFunctionalImpactScore() {
        return this.functionalImpactScore;
    }

    @JsonProperty("F_score")
    public void setFunctionalImpactScore(double functionalImpactScore) {
        this.functionalImpactScore = functionalImpactScore;
    }

}
