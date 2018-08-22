package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.cbioportal.genome_nexus.model.my_variant_info_model.Ann;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SnpeffMixin
{
    @JsonProperty(value = "license", required = true)
    private String license;

    @JsonProperty(value = "ann", required = true)
    private List<Ann> ann;
}
