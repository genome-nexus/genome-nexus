package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AlleleFrequencyMixin
{
    @JsonProperty(value = "af", required = true)
    private Integer af;

    @JsonProperty(value = "af_afr", required = true)
    private Integer afAfr;

    @JsonProperty(value = "af_amr", required = true)
    private Integer afAmr;

    @JsonProperty(value = "af_asj", required = true)
    private Integer afAsj;

    @JsonProperty(value = "af_eas", required = true)
    private Integer afEas;

    @JsonProperty(value = "af_fin", required = true)
    private Integer afFin;

    @JsonProperty(value = "af_nfe", required = true)
    private Integer afNfe;

    @JsonProperty(value = "af_oth", required = true)
    private Integer afOth;

    @JsonProperty(value = "af_sas", required = true)
    private Integer afSas;

}