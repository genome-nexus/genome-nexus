package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AlleleFrequencyMixin
{
    @JsonProperty(value = "af", required = true)
    private Float af;

    @JsonProperty(value = "af_afr", required = true)
    private Float afAfr;

    @JsonProperty(value = "af_amr", required = true)
    private Float afAmr;

    @JsonProperty(value = "af_asj", required = true)
    private Float afAsj;

    @JsonProperty(value = "af_eas", required = true)
    private Float afEas;

    @JsonProperty(value = "af_fin", required = true)
    private Float afFin;

    @JsonProperty(value = "af_nfe", required = true)
    private Float afNfe;

    @JsonProperty(value = "af_oth", required = true)
    private Float afOth;

    @JsonProperty(value = "af_sas", required = true)
    private Float afSas;

}