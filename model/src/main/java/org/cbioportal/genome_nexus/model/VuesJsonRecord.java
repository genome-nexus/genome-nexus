package org.cbioportal.genome_nexus.model;

import java.util.List;

public class VuesJsonRecord {
    private String hugoGeneSymbol;
    private String transcriptId;
    private String genomicLocationDescription;
    private String comment;
    private String context;
    private String defaultEffect;          
    private List<RevisedProteinEffectJsonRecord> revisedProteinEffects;

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

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDefaultEffect() {
        return defaultEffect;
    }

    public void setDefaultEffect(String defaultEffect) {
        this.defaultEffect = defaultEffect;
    }

    public List<RevisedProteinEffectJsonRecord> getRevisedProteinEffects() {
        return revisedProteinEffects;
    }

    public void setRevisedProteinEffects(List<RevisedProteinEffectJsonRecord> revisedProteinEffects) {
        this.revisedProteinEffects = revisedProteinEffects;
    }

    public String getTranscriptId() {
        return transcriptId;
    }

    public void setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
    }
}
