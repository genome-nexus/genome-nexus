package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlleleFrequencyMixin
{

    @ApiModelProperty(value = "af", required = false)
    private Integer af;

    @ApiModelProperty(value = "af_afr", required = false)
    private Integer afAfr;

    @ApiModelProperty(value = "af_amr", required = false)
    private Integer afAmr;

    @ApiModelProperty(value = "af_asj", required = false)
    private Integer afAsj;

    @ApiModelProperty(value = "af_eas", required = false)
    private Integer afEas;

    @ApiModelProperty(value = "af_fin", required = false)
    private Integer afFin;

    @ApiModelProperty(value = "af_nfe", required = false)
    private Integer afNfe;

    @ApiModelProperty(value = "af_oth", required = false)
    private Integer afOth;

    @ApiModelProperty(value = "af_sas", required = false)
    private Integer afSas;
}