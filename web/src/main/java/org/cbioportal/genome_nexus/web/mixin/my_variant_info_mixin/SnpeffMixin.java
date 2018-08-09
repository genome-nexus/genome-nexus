package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.cbioportal.genome_nexus.model.my_variant_info_model.Ann;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SnpeffMixin
{
    @ApiModelProperty(value = "license", required = false)
    private String license;

    // @ApiModelProperty(value = "ann", required = false)
    // private List<Ann> ann;
}
