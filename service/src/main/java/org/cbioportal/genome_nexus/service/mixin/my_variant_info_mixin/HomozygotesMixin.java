package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class HomozygotesMixin
{
    @JsonProperty(value = "hom", required = true)
    private Integer hom;

    @JsonProperty(value = "hom_afr", required = true)
    private Integer homAfr;

    @JsonProperty(value = "hom_amr", required = true)
    private Integer homAmr;

    @JsonProperty(value = "hom_asj", required = true)
    private Integer homAsj;

    @JsonProperty(value = "hom_eas", required = true)
    private Integer homEas;

    @JsonProperty(value = "hom_fin", required = true)
    private Integer homFin;

    @JsonProperty(value = "hom_nfe", required = true)
    private Integer homNfe;

    @JsonProperty(value = "hom_oth", required = true)
    private Integer homOth;

    @JsonProperty(value = "hom_sas", required = true)
    private Integer homSas;

}