package org.cbioportal.genome_nexus.model;

import java.util.List;

public class RevisedProteinEffectJsonRecord {
    private String variant; 
    private String genomicLocation;
    private String transcriptId;
    private String vepPredictedProteinEffect;
    private String vepPredictedVariantClassification;
    private String revisedProteinEffect;
    private String revisedVariantClassification;
    private Boolean confirmed;
    private String mutationOrigin;
    private List<VueReference> references;

    public List<VueReference> getReferences() {
        return references;
    }

    public void setReferences(List<VueReference> references) {
        this.references = references;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getGenomicLocation() {
        return genomicLocation;
    }

    public void setGenomicLocation(String genomicLocation) {
        this.genomicLocation = genomicLocation;
    }

    public String getTranscriptId() {
        return transcriptId;
    }

    public void setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
    }

    public String getRevisedProteinEffect() {
        return revisedProteinEffect;
    }

    public void setRevisedProteinEffect(String revisedProteinEffect) {
        this.revisedProteinEffect = revisedProteinEffect;
    }

    public String getRevisedVariantClassification() {
        return revisedVariantClassification;
    }

    public void setRevisedVariantClassification(String variantClassification) {
        this.revisedVariantClassification = variantClassification;
    }

    public String getVepPredictedProteinEffect() {
        return vepPredictedProteinEffect;
    }

    public void setVepPredictedProteinEffect(String vepPredictedProteinEffect) {
        this.vepPredictedProteinEffect = vepPredictedProteinEffect;
    }

    public String getVepPredictedVariantClassification() {
        return vepPredictedVariantClassification;
    }

    public void setVepPredictedVariantClassification(String vepPredictedVariantClassification) {
        this.vepPredictedVariantClassification = vepPredictedVariantClassification;
    }

    public String getMutationOrigin() {
        return mutationOrigin;
    }

    public void setMutationOrigin(String mutationOrigin) {
        this.mutationOrigin = mutationOrigin;
    }
}