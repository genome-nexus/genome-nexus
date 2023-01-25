package org.cbioportal.genome_nexus.model;

import java.util.List;

public class VUEs {

    private String hugoGeneSymbol;
    public String getHugoGeneSymbol() {
        return hugoGeneSymbol;
    }
    public void setHugoGeneSymbol(String hugoGeneSymbol) {
        this.hugoGeneSymbol = hugoGeneSymbol;
    }
    private String transcriptId;
    public String getTranscriptId() {
        return transcriptId;
    }
    public void setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
    }
    private String genomicLocation;
    public String getGenomicLocation() {
        return genomicLocation;
    }
    public void setGenomicLocation(String genomicLocation) {
        this.genomicLocation = genomicLocation;
    }
    private int[] pubmedIds;     
    public int[] getPubmedIds() {
        return pubmedIds;
    }
    public void setPubmedIds(int[] pubmedIds) {
        this.pubmedIds = pubmedIds;
    }
    private String context;
    public String getContext() {
        return context;
    }
    public void setContext(String context) {
        this.context = context;
    }
    private String comment;
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    private String referenceText;
    public String getReferenceText() {
        return referenceText;
    }
    public void setReferenceText(String referenceText) {
        this.referenceText = referenceText;
    }
    private String defaultEffect;          
    public String getDefaultEffect() {
        return defaultEffect;
    }
    public void setDefaultEffect(String defaultEffect) {
        this.defaultEffect = defaultEffect;
    }
    private List<RevisedProteinEffect> revisedProteinEffects;
    public List<RevisedProteinEffect> getRevisedProteinEffects() {
        return revisedProteinEffects;
    }
    public void setRevisedProteinEffects(List<RevisedProteinEffect> revisedProteinEffects) {
        this.revisedProteinEffects = revisedProteinEffects;
    }
}
