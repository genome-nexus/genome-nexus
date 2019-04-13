package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AlleleNumberMixin
{
    @JsonProperty(value = "an", required = true)
    private Integer an;

    @JsonProperty(value = "an_afr", required = true)
    private Integer anAfr;

    @JsonProperty(value = "an_amr", required = true)
    private Integer anAmr;

    @JsonProperty(value = "an_asj", required = true)
    private Integer anAsj;

    @JsonProperty(value = "an_eas", required = true)
    private Integer anEas;

    @JsonProperty(value = "an_fin", required = true)
    private Integer anFin;

    @JsonProperty(value = "an_nfe", required = true)
    private Integer anNfe;

    @JsonProperty(value = "an_oth", required = true)
    private Integer anOth;

    @JsonProperty(value = "an_sas", required = true)
    private Integer anSas;

}