package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.cbioportal.genome_nexus.model.my_variant_info_model.Gene;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Hg19;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Hg38;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Hgvs;


@JsonIgnoreProperties(ignoreUnknown = true)

public class ClinVarMixin
{
    @JsonProperty(value = "license", required = true)
    private String license;

    @JsonProperty(value = "allele_id", required = true)
    private Integer alleleId;

    @JsonProperty(value = "alt", required = true)
    private String alt;

    @JsonProperty(value = "chrom", required = true)
    private String chrom;

    @JsonProperty(value = "cytogenic", required = true)
    private String cytogenic;

    @JsonProperty(value = "gene", required = true)
    private Gene gene;

    @JsonProperty(value = "hg19", required = true)
    private Hg19 hg19;

    @JsonProperty(value = "hg38", required = true)
    private Hg38 hg38;

    @JsonProperty(value = "hgvs", required = true)
    private Hgvs hgvs;
}