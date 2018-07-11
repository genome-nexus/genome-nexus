package org.cbioportal.genome_nexus.service.mixin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.cbioportal.genome_nexus.model.Ann;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SnpeffMixin
{
    @JsonProperty(value = "license", required = true)
    private String license;

    @JsonProperty(value = "ann", required = true)
    private List<Ann> ann;
}
