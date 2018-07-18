package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeneMixin
{
    @ApiModelProperty(value = "geneid", required = false)
    private String geneId;

    @ApiModelProperty(value = "symbol", required = false)
    private String symbol;
}