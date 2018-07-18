package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnnMixin
{
    @ApiModelProperty(value = "distance_to_feature", required = false)
    private String distance_to_feature;

    @ApiModelProperty(value = "effect", required = false)
    private String effect;

    @ApiModelProperty(value = "feature_id", required = false)
    private String feature_id;

    @ApiModelProperty(value = "feature_type", required = false)
    private String feature_type;

    @ApiModelProperty(value = "gene_id", required = false)
    private String gene_id;

    @ApiModelProperty(value = "gene_name", required = false)
    private String gene_name;

    @ApiModelProperty(value = "hgvs_c", required = false)
    private String hgvs_c;

    @ApiModelProperty(value = "putative_impact", required = false)
    private String putative_impact;

    @ApiModelProperty(value = "transcript_biotype", required = false)
    private String transcript_biotype;
}
