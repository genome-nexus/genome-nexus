package org.cbioportal.genome_nexus.service.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MutationAssessorMixin
{
    @JsonProperty("gene")
    private String hugoSymbol;

    @JsonProperty("rgaa")
    private String referenceGenomeVariant;

    @JsonProperty("rgvt")
    private String referenceGenomeVariantType;

    @JsonProperty("F_impact")
    private String functionalImpact;

    @JsonProperty("F_score")
    private double functionalImpactScore;

    @JsonProperty("msa")
    private String msaLink;

    @JsonProperty("pdb")
    private String pdbLink;

    @JsonProperty("vc_score")
    private double variantConservationScore;

    @JsonProperty("vs_score")
    private double variantSpecificityScore;

    @JsonProperty("info")
    private String mappingIssue;

    @JsonProperty("var")
    private String variant;

    @JsonProperty("uprot")
    private String uniprotId;

    @JsonProperty("rsprot")
    private String refseqId;

    @JsonProperty("gaps")
    private double msaGaps;

    @JsonProperty("msa_height")
    private int msaHeight;

    @JsonProperty("chr")
    private String codonStartPosition;

    @JsonProperty("up_pos")
    private int uniprotPosition;

    @JsonProperty("up_res")
    private String uniprotResidue;

    @JsonProperty("rs_pos")
    private int refseqPosition;

    @JsonProperty("rs_res")
    private String refseqResidue;

    @JsonProperty("cnt_cosmic")
    private int cosmicCount;

    @JsonProperty("cnt_snps")
    private int snpCount;
}
