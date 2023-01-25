package org.cbioportal.genome_nexus.model;

import java.util.List;
import java.util.Map;

public class VUEs {

    private String hugoGeneSymbol;
    private String transcriptId;
    private String genomicLocation;
    private int[] pubmedIds;     
    private String context;
    private String comment;
    private String referenceText;
    private String defaultEffect;          
    private List<RevisedProteinEffect> revisedProteinEffects;

    public String gethugoGeneSymbol() {
        return hugoGeneSymbol;
    }

    public void sethugoGeneSymbol(String hugoGeneSymbol) {
        this.hugoGeneSymbol = hugoGeneSymbol;
    }

    public String getgenomicLocation() {
        return genomicLocation;
    }

    public void setgenomicLocation(String genomicLocation) {
        this.genomicLocation = genomicLocation;
    }

    public String gettranscriptId() {
        return transcriptId;
    }

    public void settranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
    }

    public int[] getpubedIds() {
        return pubmedIds;
    }

    public void setpubmedIds(int[] pubmedIds) {
        this.pubmedIds = pubmedIds;
    }

    public String getcontext() {
        return context;
    }

    public void setcontext(String context) {
        this.context = context;
    }

    public String getcomment() {
        return comment;
    }

    public void setcomment(String comment) {
        this.comment = comment;
    }

    public String getreferenceText() {
        return referenceText;
    }

    public void setreferenceText(String referenceText) {
        this.referenceText = referenceText;
    }

    public String getdefaultEffect() {
        return defaultEffect;
    }

    public void setdefaultEffect(String defaultEffect) {
        this.defaultEffect = defaultEffect;
    }

    public List<RevisedProteinEffect> getrevisedProteinEffects() {
        return revisedProteinEffects;
    }

    public void setrevisedProteinEffects(List<RevisedProteinEffect>  revisedProteinEffects) {
        this.revisedProteinEffects = revisedProteinEffects;
    }
}


