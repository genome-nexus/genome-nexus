package org.cbioportal.genome_nexus.model;

import java.util.List;

public class Vues {
    private String hugoGeneSymbol; 
    private String genomicLocationDescription;
    private String defaultEffect;
    private String comment;
    private String variant; 
    private String genomicLocation;
    private String transcriptId;
    private String revisedProteinEffect;
    private String revisedVariantClassification;
    private String revisedVariantClassificationStandard;
    private String context;
    private String vepPredictedProteinEffect;
    private String vepPredictedVariantClassification;
    private String mutationOrigin;
    private List<VueReference> references;
    
    public List<VueReference> getReferences() {
        return references;
    }
    public void setReferences(List<VueReference> references) {
        this.references = references;
    }
    public String getMutationOrigin() {
        return mutationOrigin;
    }
    public void setMutationOrigin(String mutationOrigin) {
        this.mutationOrigin = mutationOrigin;
    }
    private Boolean confirmed;

    public String getHugoGeneSymbol() {
        return hugoGeneSymbol;
    }
    public void setHugoGeneSymbol(String hugoGeneSymbol) {
        this.hugoGeneSymbol = hugoGeneSymbol;
    }
    public String getGenomicLocationDescription() {
        return genomicLocationDescription;
    }
    public void setGenomicLocationDescription(String genomicLocationDescription) {
        this.genomicLocationDescription = genomicLocationDescription;
    }
    public String getDefaultEffect() {
        return defaultEffect;
    }
    public void setDefaultEffect(String defaultEffect) {
        this.defaultEffect = defaultEffect;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
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
    public void setRevisedVariantClassification(String revisedVariantClassification) {
        this.revisedVariantClassification = revisedVariantClassification;
    } 
    public String getContext() {
        return context;
    }
    public void setContext(String context) {
        this.context = context;
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
    public String getRevisedVariantClassificationStandard() {
        return revisedVariantClassificationStandard;
    }
    public void setRevisedVariantClassificationStandard(String revisedVariantClassificationStandard) {
        this.revisedVariantClassificationStandard = revisedVariantClassificationStandard;
    }
    public Boolean getConfirmed() {
        return confirmed;
    }
    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }
}
