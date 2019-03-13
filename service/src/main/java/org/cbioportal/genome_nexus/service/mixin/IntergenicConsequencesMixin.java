package org.cbioportal.genome_nexus.service.mixin;

import com.fasterxml.jackson.annotation.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IntergenicConsequencesMixin
{
    @JsonProperty(value="impact", required = true)
    private String impact;

    @JsonProperty(value="variantAllele", required = true)
    private String variantAllele;

    @JsonProperty(value="consequenceTerms", required = true)
    private List<String> consequenceTerms;

}
