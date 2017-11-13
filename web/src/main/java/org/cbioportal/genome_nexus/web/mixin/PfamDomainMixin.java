package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PfamDomainMixin
{
    @ApiModelProperty(value = "PFAM domain accession", required = true)
    private String pfamAccession;

    @ApiModelProperty(value = "PFAM domain name", required = true)
    private String name;

    @ApiModelProperty(value = "PFAM domain description")
    private String description;

    // TODO move this into Ensembl model...
    @ApiModelProperty(value = "PFAM domain start")
    private Integer pfamDomainStart;

    @ApiModelProperty(value = "PFAM domain end")
    private Integer pfamDomainEnd;
}
