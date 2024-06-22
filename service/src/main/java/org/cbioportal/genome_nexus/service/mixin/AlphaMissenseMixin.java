package org.cbioportal.genome_nexus.service.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlphaMissenseMixin {

    @JsonProperty(value = "am_class", required = true)
    private String pathogenicity;

    @JsonProperty(value = "am_pathogenicity", required = true)
    private Double score;
}
