package org.cbioportal.genome_nexus.model;

public class TranscriptConsequenceSummary
{
    private String transcriptId;
    private String codonChange;
    private String entrezGeneId;
    private String consequenceTerms;
    private String hugoGeneSymbol;
    private String hgvspShort;
    private String hgvsp;
    private String hgvsc;
    private IntegerRange proteinPosition;
    private String refSeq;
    private String variantClassification;

    public String getTranscriptId() {
        return transcriptId;
    }

    public void setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
    }

    public String getCodonChange() {
        return codonChange;
    }

    public void setCodonChange(String codonChange) {
        this.codonChange = codonChange;
    }

    public String getEntrezGeneId() {
        return entrezGeneId;
    }

    public void setEntrezGeneId(String entrezGeneId) {
        this.entrezGeneId = entrezGeneId;
    }

    public String getConsequenceTerms() {
        return consequenceTerms;
    }

    public void setConsequenceTerms(String consequenceTerms) {
        this.consequenceTerms = consequenceTerms;
    }

    public String getHugoGeneSymbol() {
        return hugoGeneSymbol;
    }

    public void setHugoGeneSymbol(String hugoGeneSymbol) {
        this.hugoGeneSymbol = hugoGeneSymbol;
    }

    public String getHgvspShort() {
        return hgvspShort;
    }

    public void setHgvspShort(String hgvspShort) {
        this.hgvspShort = hgvspShort;
    }

    public String getHgvsp() {
        return hgvsp;
    }

    public void setHgvsp(String hgvsp) {
        this.hgvsp = hgvsp;
    }

    public String getHgvsc() {
        return hgvsc;
    }

    public void setHgvsc(String hgvsc) {
        this.hgvsc = hgvsc;
    }

    public IntegerRange getProteinPosition() {
        return proteinPosition;
    }

    public void setProteinPosition(IntegerRange proteinPosition) {
        this.proteinPosition = proteinPosition;
    }

    public String getRefSeq() {
        return refSeq;
    }

    public void setRefSeq(String refSeq) {
        this.refSeq = refSeq;
    }

    public String getVariantClassification() {
        return variantClassification;
    }

    public void setVariantClassification(String variantClassification) {
        this.variantClassification = variantClassification;
    }
}
