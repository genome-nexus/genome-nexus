package org.cbioportal.genome_nexus.model;

public class RevisedProteinEffect {
    private String variant; 
    public String getVariant() {
        return variant;
    }
    public void setVariant(String variant) {
        this.variant = variant;
    }
    private String genomicLocation;
    public String getGenomicLocation() {
        return genomicLocation;
    }
    public void setGenomicLocation(String genomicLocation) {
        this.genomicLocation = genomicLocation;
    }
    private String transcriptId;
    public String getTranscriptId() {
        return transcriptId;
    }
    public void setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
    }
    private String revisedProteinEffect;
    public String getRevisedProteinEffect() {
        return revisedProteinEffect;
    }
    public void setRevisedProteinEffect(String revisedProteinEffect) {
        this.revisedProteinEffect = revisedProteinEffect;
    }
    private String variantClassification;
    public String getVariantClassification() {
        return variantClassification;
    }
    public void setVariantClassification(String variantClassification) {
        this.variantClassification = variantClassification;
    } 
}

