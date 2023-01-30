package org.cbioportal.genome_nexus.model;

import java.util.List;

public class Vues {
    private String hugoGeneSymbol; 
    private String genomicLocationDescription;
    private String defaultEffect;
    private String comment;
    private List<Integer> pubmedIds;
    private String referenceText;
    private String variant; 
    private String genomicLocation;
    private String transcriptId;
    private String revisedProteinEffect;
    private String variantClassification;

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
    public List<Integer> getPubmedIds() {
        return pubmedIds;
    }
    public void setPubmedIds(List<Integer> pubmedIds) {
        this.pubmedIds = pubmedIds;
    }
    public String getReferenceText() {
        return referenceText;
    }
    public void setReferenceText(String referenceText) {
        this.referenceText = referenceText;
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
    public String getVariantClassification() {
        return variantClassification;
    }
    public void setVariantClassification(String variantClassification) {
        this.variantClassification = variantClassification;
    } 

}
