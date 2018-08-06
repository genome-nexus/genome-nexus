package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VcfMixin
{
    @ApiModelProperty(value = "alt", required = false)
    private String alt;

    @ApiModelProperty(value = "position", required = false)
    private String position;

    @ApiModelProperty(value = "ref", required = false)
    private String ref;
}
