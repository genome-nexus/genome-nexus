package org.cbioportal.genome_nexus.annotation.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mutation_assessor")
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

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getHugoSymbol() {
        return hugoSymbol;
    }

    public void setHugoSymbol(String hugoSymbol) {
        this.hugoSymbol = hugoSymbol;
    }

    public String getReferenceGenomeVariant() {
        return referenceGenomeVariant;
    }

    public void setReferenceGenomeVariant(String referenceGenomeVariant) {
        this.referenceGenomeVariant = referenceGenomeVariant;
    }

    public String getReferenceGenomeVariantType() {
        return referenceGenomeVariantType;
    }

    public void setReferenceGenomeVariantType(String referenceGenomeVariantType) {
        this.referenceGenomeVariantType = referenceGenomeVariantType;
    }

    public String getFunctionalImpact() {
        return functionalImpact;
    }

    public void setFunctionalImpact(String functionalImpact) {
        this.functionalImpact = functionalImpact;
    }

    public double getFunctionalImpactScore() {
        return functionalImpactScore;
    }

    public void setFunctionalImpactScore(double functionalImpactScore) {
        this.functionalImpactScore = functionalImpactScore;
    }

    public String getMsaLink() {
        return msaLink;
    }

    public void setMsaLink(String msaLink) {
        this.msaLink = msaLink;
    }

    public String getPdbLink() {
        return pdbLink;
    }

    public void setPdbLink(String pdbLink) {
        this.pdbLink = pdbLink;
    }

    public double getVariantConservationScore() {
        return variantConservationScore;
    }

    public void setVariantConservationScore(double variantConservationScore) {
        this.variantConservationScore = variantConservationScore;
    }

    public double getVariantSpecificityScore() {
        return variantSpecificityScore;
    }

    public void setVariantSpecificityScore(double variantSpecificityScore) {
        this.variantSpecificityScore = variantSpecificityScore;
    }

    public String getMappingIssue() {
        return mappingIssue;
    }

    public void setMappingIssue(String mappingIssue) {
        this.mappingIssue = mappingIssue;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getUniprotId() {
        return uniprotId;
    }

    public void setUniprotId(String uniprotId) {
        this.uniprotId = uniprotId;
    }

    public String getRefseqId() {
        return refseqId;
    }

    public void setRefseqId(String refseqId) {
        this.refseqId = refseqId;
    }

    public double getMsaGaps() {
        return msaGaps;
    }

    public void setMsaGaps(double msaGaps) {
        this.msaGaps = msaGaps;
    }

    public int getMsaHeight() {
        return msaHeight;
    }

    public void setMsaHeight(int msaHeight) {
        this.msaHeight = msaHeight;
    }

    public String getCodonStartPosition() {
        return codonStartPosition;
    }

    public void setCodonStartPosition(String codonStartPosition) {
        this.codonStartPosition = codonStartPosition;
    }

    public int getUniprotPosition() {
        return uniprotPosition;
    }

    public void setUniprotPosition(int uniprotPosition) {
        this.uniprotPosition = uniprotPosition;
    }

    public String getUniprotResidue() {
        return uniprotResidue;
    }

    public void setUniprotResidue(String uniprotResidue) {
        this.uniprotResidue = uniprotResidue;
    }

    public int getRefseqPosition() {
        return refseqPosition;
    }

    public void setRefseqPosition(int refseqPosition) {
        this.refseqPosition = refseqPosition;
    }

    public String getRefseqResidue() {
        return refseqResidue;
    }

    public void setRefseqResidue(String refseqResidue) {
        this.refseqResidue = refseqResidue;
    }

    public int getCosmicCount() {
        return cosmicCount;
    }

    public void setCosmicCount(int cosmicCount) {
        this.cosmicCount = cosmicCount;
    }

    public int getSnpCount() {
        return snpCount;
    }

    public void setSnpCount(int snpCount) {
        this.snpCount = snpCount;
    }
}
