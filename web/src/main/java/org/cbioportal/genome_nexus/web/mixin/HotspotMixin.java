package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.cbioportal.genome_nexus.model.IntegerRange;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HotspotMixin
{
    @ApiModelProperty(value = "Transcript id", required = true)
    private String transcriptId;

    @ApiModelProperty(value = "Hugo gene symbol", required = false)
    private String hugoSymbol;

    @ApiModelProperty(value = "Hotspot Residue", required = false)
    private String residue;

    @ApiModelProperty(value = "Hotspot type", required = false)
    private String type;

    @ApiModelProperty(value = "Amino acid position (start - end)", required = false)
    private IntegerRange aminoAcidPosition;

    @ApiModelProperty(value = "Tumor type count", required = false)
    private Integer tumorTypeCount;

    @ApiModelProperty(value = "Tumor count", required = false)
    private Integer tumorCount;
}
