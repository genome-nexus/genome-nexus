package org.cbioportal.genome_nexus.web.mixin;

import java.util.List;
import org.cbioportal.genome_nexus.model.Ann;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SnpeffMixin
{
    @ApiModelProperty(value = "license", required = false)
    private String license;

    @ApiModelProperty(value = "ann", required = false)
    private List<Ann> ann;
}
