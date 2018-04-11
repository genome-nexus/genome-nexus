package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ColocatedVariantMixin {
    @ApiModelProperty(value = "GnomAD NFE MAF", position=1)
    private String gnomad_nfe_maf;
}