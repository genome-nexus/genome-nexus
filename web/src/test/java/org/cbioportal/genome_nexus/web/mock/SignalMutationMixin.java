package org.cbioportal.genome_nexus.web.mock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cbioportal.genome_nexus.model.CountByTumorType;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SignalMutationMixin
{
    @JsonProperty("hugo_gene_symbol")
    private String hugoGeneSymbol;

    @JsonProperty("chromosome")
    private String chromosome;

    @JsonProperty("start_position")
    private Long startPosition;

    @JsonProperty("end_position")
    private Long endPosition;

    @JsonProperty("reference_allele")
    private String referenceAllele;

    @JsonProperty("variant_allele")
    private String variantAllele;

    @JsonProperty("mutation_status")
    private String mutationStatus;

    @JsonProperty("pathogenic")
    private String pathogenic;

    @JsonProperty("penetrance")
    private String penetrance;

    @JsonProperty("counts_by_tumor_type")
    private List<CountByTumorType> countsByTumorType;

    @JsonProperty("biallelic_counts_by_tumor_type")
    private List<CountByTumorType> biallelicCountsByTumorType;

    @JsonProperty("qc_pass_counts_by_tumor_type")
    private List<CountByTumorType> qcPassCountsByTumorType;
}
