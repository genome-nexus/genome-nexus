package org.cbioportal.genome_nexus.annotation.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// @Document(collection = "hotspots")
 @JsonIgnoreProperties(ignoreUnknown = true)
 @JsonInclude(JsonInclude.Include.NON_NULL)
    public class MutationAssessor
    {

        // user-input variant
        private String variant;

        private String hugoSymbol;
        private String functionalImpact;
        private String fScore;

        // @ApiModelProperty(value = "User-input variants", required = true)
        public String getVariant() { return this.variant; }

        @JsonProperty("input")
        public void setVariant(String variant) { this.variant = variant; }

        // @ApiModelProperty(value = "Hugo gene symbol", required = false)
        public String getHugoSymbol()
        {
            return hugoSymbol;
        }

        @JsonProperty("gene")
        public void setHugoSymbol(String hugoSymbol)
        {
            this.hugoSymbol = hugoSymbol;
        }

        // @ApiModelProperty(value = "Functional impact", required = false)
        public String getFunctionalImpact() { return this.functionalImpact; }

        @JsonProperty("F_impact")
        public void setFunctionalImpact(String functionalImpact)
        {
            this.functionalImpact = functionalImpact;
        }

        // @ApiModelProperty(value = "Functional impact score", required = false)
        public String getfScore()
        {
            return this.fScore;
        }

        @JsonProperty("F_score")
        public void setfScore(String fScore)
        {
            this.fScore = fScore;
        }

        public String getOuput()
        {
            StringBuilder builder = new StringBuilder("\nvariant: " + this.variant);
            builder.append("\nHugo symbol: " + this.hugoSymbol);
            builder.append("\nfunctional impact: " + this.functionalImpact);
            builder.append("\nfunctional impact score: " + this.fScore);
            return builder.toString();
        }

    }
