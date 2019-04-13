package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlleleNumberMixin
{

    @ApiModelProperty(value = "an", required = false)
    private Integer an;

    @ApiModelProperty(value = "an_afr", required = false)
    private Integer anAfr;

    @ApiModelProperty(value = "an_amr", required = false)
    private Integer anAmr;

    @ApiModelProperty(value = "an_asj", required = false)
    private Integer anAsj;

    @ApiModelProperty(value = "an_eas", required = false)
    private Integer anEas;

    @ApiModelProperty(value = "an_fin", required = false)
    private Integer anFin;

    @ApiModelProperty(value = "an_nfe", required = false)
    private Integer anNfe;

    @ApiModelProperty(value = "an_oth", required = false)
    private Integer anOth;

    @ApiModelProperty(value = "an_sas", required = false)
    private Integer anSas;
}