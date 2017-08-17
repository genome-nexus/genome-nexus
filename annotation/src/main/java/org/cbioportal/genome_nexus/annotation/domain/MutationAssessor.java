package org.cbioportal.genome_nexus.annotation.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mutation_assessor")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MutationAssessor
{
    @Id
    private String input;

    private String hugoSymbol;

    private String referenceGenomeVariant;
    private String referenceGenomeVariantType;

    private String functionalImpact;
    private double functionalImpactScore;

    private String msaLink;
    private String pdbLink;

    private double variantConservationScore;
    private double variantSpecificityScore;

    private String mappingIssue;
    private String variant;
    private String uniprotId;
    private String refseqId;

    private double msaGaps;
    private int msaHeight;

    private String codonStartPosition;
    private int uniprotPosition;
    private String uniprotResidue;
    private int refseqPosition;
    private String refseqResidue;

    private int cosmicCount;
    private int snpCount;

    @ApiModelProperty(value = "User-input variants", required = true)
    public String getInput() { return this.input; }

    public void setInput(String input) { this.input = input; }

    @ApiModelProperty(value = "Hugo gene symbol", required = false)
    @JsonProperty("hugoSymbol")
    public String getHugoSymbol() { return hugoSymbol; }

    @JsonProperty("gene")
    public void setGene(String hugoSymbol) { this.hugoSymbol = hugoSymbol; }

    @ApiModelProperty(value = "Reference genome variant", required = false)
    @JsonProperty("referenceGenomeVariant")
    public String getReferenceGenomeVariant() { return this.referenceGenomeVariant; }

    @JsonProperty("rgaa")
    public void setRgaa(String referenceGenomeVariant) { this.referenceGenomeVariant = referenceGenomeVariant; }

    @ApiModelProperty(value = "Reference genome variant type", required = false)
    @JsonProperty("referenceGenomeVariantType")
    public String getReferenceGenomeVariantType() { return this.referenceGenomeVariantType; }

    @JsonProperty("rgvt")
    public void setRgvt(String referenceGenomeVariantType) { this.referenceGenomeVariantType = referenceGenomeVariantType; }

    @ApiModelProperty(value = "Functional impact", required = false)
    @JsonProperty("functionalImpact")
    public String getFunctionalImpact() { return this.functionalImpact; }

    @JsonProperty("F_impact")
    public void setF_impact(String functionalImpact) { this.functionalImpact = functionalImpact; }

    @ApiModelProperty(value = "Functional impact score", required = false)
    @JsonProperty("functionalImpactScore")
    public double getFunctionalImpactScore() {
        return this.functionalImpactScore;
    }

    @JsonProperty("F_score")
    public void setF_score(double functionalImpactScore) {
        this.functionalImpactScore = functionalImpactScore;
    }

    @ApiModelProperty(value = "Link to multiple sequence alignment", required = false)
    @JsonProperty("msaLink")
    public String getMsaLink() { return this.msaLink; }

    @JsonProperty("msa")
    public void setMSA(String msaLink) { this.msaLink = msaLink; }

    @ApiModelProperty(value = "Link to 3d structure browser", required = false)
    @JsonProperty("pdbLink")
    public String getPdbLink() { return this.pdbLink; }

    @JsonProperty("pdb")
    public void setPDB(String pdbLink) { this.pdbLink = pdbLink; }

    @ApiModelProperty(value = "Variant conservation score", required = false)
    @JsonProperty("variantConservationScore")
    public double getVariantConservationScore() { return this.variantConservationScore; }

    @JsonProperty("vc_score")
    public void setVc_score(double variantConservationScore) { this.variantConservationScore = variantConservationScore; }

    @ApiModelProperty(value = "Variant specificity score", required = false)
    @JsonProperty("variantSpecificityScore")
    public double getVariantSpecificityScore() { return this.variantSpecificityScore; }

    @JsonProperty("vs_score")
    public void setVs_score(double variantSpecificityScore) { this.variantSpecificityScore = variantSpecificityScore; }

    @ApiModelProperty(value = "Mapping issue info", required = false)
    @JsonProperty("mappingIssue")
    public String getMappingIssue() { return this.mappingIssue; }

    @JsonProperty("info")
    public void setInfo(String mappingIssue) { this.mappingIssue = mappingIssue; }

    @ApiModelProperty(value = "Amino acid substitution", required = false)
    @JsonProperty("variant")
    public String getVariant() { return this.variant; }

    @JsonProperty("var")
    public void setVar(String variant) { this.variant = variant; }

    @ApiModelProperty(value = "Uniprot protein accession ID", required = false)
    @JsonProperty("uniprotId")
    public String getUniprotId() { return this.uniprotId; }

    @JsonProperty("uprot")
    public void setUprot(String uniprotId) { this.uniprotId = uniprotId; }

    @ApiModelProperty(value = "Refseq protein ID", required = false)
    @JsonProperty("refseqId")
    public String getRefseqId() { return this.refseqId; }

    @JsonProperty("rsprot")
    public void setRsprot(String refseqId) { this.refseqId = refseqId; }

    @ApiModelProperty(value = "Portion of gaps in variant position in multiple sequence alignment", required = false)
    @JsonProperty("msaGaps")
    public double getMsaGaps() { return this.msaGaps; }

    @JsonProperty("gaps")
    public void setGaps(double msaGaps) { this.msaGaps = msaGaps; }

    @ApiModelProperty(value = "Number of diverse sequences in multiple sequence alignment (identical or highly similar sequences filtered out)", required = false)
    @JsonProperty("msaHeight")
    public int getMsaHeight() { return this.msaHeight; }

    @JsonProperty("msa_height")
    public void setMsa_height(int msaHeight) { this.msaHeight = msaHeight; }

    @ApiModelProperty(value = "Codon start position", required = false)
    @JsonProperty("codonStartPosition")
    public String getCodonStartPosition() { return this.codonStartPosition; }

    @JsonProperty("chr")
    public void setChr(String codonStartPosition) { this.codonStartPosition = codonStartPosition; }

    @ApiModelProperty(value = "Variant position in Uniprot protein, can be different from the one in Refseq", required = false)
    @JsonProperty("uniprotPosition")
    public int getUniprotPosition() { return this.uniprotPosition; }

    @JsonProperty("up_pos")
    public void setUp_pos(int uniprotPosition) { this.uniprotPosition = uniprotPosition; }

    @ApiModelProperty(value = "Reference residue in Uniprot protein, can be different from the one in Refseq", required = false)
    @JsonProperty("uniprotResidue")
    public String getUniprotResidue() { return this.uniprotResidue; }

    @JsonProperty("up_res")
    public void setUp_res(String uniprotResidue) { this.uniprotResidue = uniprotResidue; }

    @ApiModelProperty(value = "Variant position in Refseq protein, can be different from the one in Uniprot", required = false)
    @JsonProperty("refseqPosition")
    public int getRefseqPosition() { return this.refseqPosition; }

    @JsonProperty("rs_pos")
    public void setRs_pos(int refseqPosition) { this.refseqPosition = refseqPosition; }

    @ApiModelProperty(value = "Reference residue in Refseq protein, can be different from the one in Uniprot", required = false)
    @JsonProperty("refseqResidue")
    public String getRefseqResidue() { return this.refseqResidue; }

    @JsonProperty("rs_res")
    public void setRs_res(String refseqResidue) { this.refseqResidue = refseqResidue; }

    @ApiModelProperty(value = "Number of mutations in COSMIC for this protein", required = false)
    @JsonProperty("cosmicCount")
    public int getCosmicCount() { return this.cosmicCount; }

    @JsonProperty("cnt_cosmic")
    public void setCnt_cosmic(int cosmicCount) { this.cosmicCount = cosmicCount; }

    @ApiModelProperty(value = "Number of SNPs in dbSNP for this protein", required = false)
    @JsonProperty("snpCount")
    public int getSnpCount() { return this.snpCount; }

    @JsonProperty("cnt_snps")
    public void setCnt_snps(int snpCount) { this.snpCount = snpCount; }
}
