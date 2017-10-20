package org.cbioportal.genome_nexus.annotation.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PfamDomainMixin
{
    @ApiModelProperty(value = "Ensembl gene id", required = true)
    private String geneId;

    @ApiModelProperty(value = "Ensembl transcript id", required = true)
    private String transcriptId;

    @ApiModelProperty(value = "Ensembl translation id", required = true)
    private String proteinId;

    @ApiModelProperty(value = "Hugo gene symbol")
    private String geneSymbol;

    @ApiModelProperty(value = "PFAM domain id")
    private String pfamDomainId;

    @ApiModelProperty(value = "PFAM domain start")
    private Integer pfamDomainStart;

    @ApiModelProperty(value = "PFAM domain end")
    private Integer getPfamDomainEnd;

    @ApiModelProperty(value = "PFAM domain name")
    private String pfamDomainName;

    @ApiModelProperty(value = "PFAM domain description")
    private String pfamDomainDescription;
}
