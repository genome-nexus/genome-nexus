package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AlleleCountMixin
{
    @JsonProperty(value = "ac", required = true)
    private Integer ac;

    @JsonProperty(value = "ac_afr", required = true)
    private Integer acAfr;

    @JsonProperty(value = "ac_amr", required = true)
    private Integer acAmr;

    @JsonProperty(value = "ac_asj", required = true)
    private Integer acAsj;

    @JsonProperty(value = "ac_eas", required = true)
    private Integer acEas;

    @JsonProperty(value = "ac_fin", required = true)
    private Integer acFin;

    @JsonProperty(value = "ac_nfe", required = true)
    private Integer acNfe;

    @JsonProperty(value = "ac_oth", required = true)
    private Integer acOth;

    @JsonProperty(value = "ac_sas", required = true)
    private Integer acSas;

}