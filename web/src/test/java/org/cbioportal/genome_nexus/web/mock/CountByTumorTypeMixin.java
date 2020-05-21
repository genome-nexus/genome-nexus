package org.cbioportal.genome_nexus.web.mock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountByTumorTypeMixin {
    @JsonProperty("tumor_type")
    private String tumorType;

    @JsonProperty("tumor_type_count")
    private Integer tumorTypeCount;

    @JsonProperty("variant_count")
    private Integer variantCount;
}
