package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IsoformOverrideMixin
{
    @ApiModelProperty(value = "Ensembl transcript id", required = true)
    private String transcriptId;

    @ApiModelProperty(value = "Hugo gene symbol")
    private String geneSymbol;

    @ApiModelProperty(value = "RefSeq ID")
    private String refseqId;

    @ApiModelProperty(value = "CCDS ID")
    private String ccdsId;
}
