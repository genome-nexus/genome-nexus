package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlleleFrequencyMixin
{

    @ApiModelProperty(value = "af", required = false)
    private Float af;

    @ApiModelProperty(value = "af_afr", required = false)
    private Float afAfr;

    @ApiModelProperty(value = "af_amr", required = false)
    private Float afAmr;

    @ApiModelProperty(value = "af_asj", required = false)
    private Float afAsj;

    @ApiModelProperty(value = "af_eas", required = false)
    private Float afEas;

    @ApiModelProperty(value = "af_fin", required = false)
    private Float afFin;

    @ApiModelProperty(value = "af_nfe", required = false)
    private Float afNfe;

    @ApiModelProperty(value = "af_oth", required = false)
    private Float afOth;

    @ApiModelProperty(value = "af_sas", required = false)
    private Float afSas;
}