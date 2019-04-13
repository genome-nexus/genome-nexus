package org.cbioportal.genome_nexus.web.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HomozygotesMixin
{

    @ApiModelProperty(value = "hom", required = false)
    private Integer hom;

    @ApiModelProperty(value = "hom_afr", required = false)
    private Integer homAfr;

    @ApiModelProperty(value = "hom_amr", required = false)
    private Integer homAmr;

    @ApiModelProperty(value = "hom_asj", required = false)
    private Integer homAsj;

    @ApiModelProperty(value = "hom_eas", required = false)
    private Integer homEas;

    @ApiModelProperty(value = "hom_fin", required = false)
    private Integer homFin;

    @ApiModelProperty(value = "hom_nfe", required = false)
    private Integer homNfe;

    @ApiModelProperty(value = "hom_oth", required = false)
    private Integer homOth;

    @ApiModelProperty(value = "hom_sas", required = false)
    private Integer homSas;
}