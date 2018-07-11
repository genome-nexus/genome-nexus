package org.cbioportal.genome_nexus.service.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnMixin
{
    @JsonProperty(value = "distance_to_feature", required = true)
    private String distance_to_feature;

    @JsonProperty(value = "effect", required = true)
    private String effect;

    @JsonProperty(value = "feature_id", required = true)
    private String feature_id;

    @JsonProperty(value = "feature_type", required = true)
    private String feature_type;

    @JsonProperty(value = "gene_id", required = true)
    private String gene_id;

    @JsonProperty(value = "gene_name", required = true)
    private String gene_name;

    @JsonProperty(value = "hgvs_c", required = true)
    private String hgvs_c;

    @JsonProperty(value = "putative_impact", required = true)
    private String putative_impact;

    @JsonProperty(value = "transcript_biotype", required = true)
    private String transcript_biotype;
}
