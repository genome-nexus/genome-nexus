package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HotspotMixin
{
    @ApiModelProperty(value = "Transcript id", required = true)
    private String transcriptId;

    @ApiModelProperty(value = "Hugo gene symbol", required = false)
    private String hugoSymbol;

    @ApiModelProperty(value = "Hotspot Residue", required = false)
    private String residue;

    @ApiModelProperty(value = "Protein start position", required = false)
    private String proteinStart;

    @ApiModelProperty(value = "Protein end position", required = false)
    private String proteinEnd;

    @ApiModelProperty(value = "Ensembl gene id", required = false)
    private String geneId;
}
