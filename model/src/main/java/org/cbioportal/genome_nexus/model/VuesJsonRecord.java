package org.cbioportal.genome_nexus.model;

import java.util.List;

public class VuesJsonRecord {

    private String hugoGeneSymbol;
    private String genomicLocationDescription;
    private List<Integer> pubmedIds;     
    private String comment;
    private String referenceText;
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

    public List<Integer> getPubmedIds() {
        return pubmedIds;
    }

    public void setPubmedIds(List<Integer> pubmedIds) {
        this.pubmedIds = pubmedIds;
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

    public String getReferenceText() {
        return referenceText;
    }

    public void setReferenceText(String referenceText) {
        this.referenceText = referenceText;
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
}
