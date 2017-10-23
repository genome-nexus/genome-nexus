package org.cbioportal.genome_nexus.annotation.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnsemblTranscriptMixin {
    @ApiModelProperty(value = "Ensembl transcript id", required = true)
    private String transcriptId;

    @ApiModelProperty(value = "Ensembl gene id", required = true)
    private String geneId;

    @ApiModelProperty(value = "Ensembl protein id", required = true)
    private String proteinId;

    @ApiModelProperty(value = "Length of protein")
    private Integer proteinLength;
}
