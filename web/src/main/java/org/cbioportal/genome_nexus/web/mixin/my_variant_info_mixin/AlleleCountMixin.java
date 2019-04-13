package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlleleCountMixin
{

    @ApiModelProperty(value = "ac", required = false)
    private Integer ac;

    @ApiModelProperty(value = "ac_afr", required = false)
    private Integer acAfr;

    @ApiModelProperty(value = "ac_amr", required = false)
    private Integer acAmr;

    @ApiModelProperty(value = "ac_asj", required = false)
    private Integer acAsj;

    @ApiModelProperty(value = "ac_eas", required = false)
    private Integer acEas;

    @ApiModelProperty(value = "ac_fin", required = false)
    private Integer acFin;

    @ApiModelProperty(value = "ac_nfe", required = false)
    private Integer acNfe;

    @ApiModelProperty(value = "ac_oth", required = false)
    private Integer acOth;

    @ApiModelProperty(value = "ac_sas", required = false)
    private Integer acSas;
}