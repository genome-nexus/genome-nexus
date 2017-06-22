package org.cbioportal.genome_nexus.annotation.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

 @JsonIgnoreProperties(ignoreUnknown = true)
 @JsonInclude(JsonInclude.Include.NON_NULL)
    public class MutationAssessor
    {
        private String variant;

        private String hugoSymbol;
        private String functionalImpact;
        private String functionalImpactScore;

        @ApiModelProperty(value = "User-input variants", required = true)
        public String getVariant() { return this.variant; }

        public void setVariant(String variant) { this.variant = variant; }

        @ApiModelProperty(value = "Hugo gene symbol", required = false)
        public String getHugoSymbol()
        {
            return hugoSymbol;
        }

        @JsonProperty("gene")
        public void setHugoSymbol(String hugoSymbol)
        {
            this.hugoSymbol = hugoSymbol;
        }

        @ApiModelProperty(value = "Functional impact", required = false)
        public String getFunctionalImpact()
        {
            return this.functionalImpact;
        }

        @JsonProperty("F_impact")
        public void setFunctionalImpact(String functionalImpact)
        {

            this.functionalImpact = functionalImpact;
        }

        @ApiModelProperty(value = "Functional impact score", required = false)
        public String getFunctionalImpactScore()
        {
            return this.functionalImpactScore;
        }

        @JsonProperty("F_score")
        public void setFunctionalImpactScore(String functionalImpactScore)
        {
            this.functionalImpactScore = functionalImpactScore;
        }

    }
