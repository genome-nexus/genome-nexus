package org.cbioportal.genome_nexus.model;

public class VueReference {
    private Integer pubmedId;
    private String referenceText;

    public Integer getPubmedId() {
        return pubmedId;
    }
    public void setPubmedId(Integer pubmedId) {
        this.pubmedId = pubmedId;
    }
    public String getReferenceText() {
        return referenceText;
    }
    public void setReferenceText(String referenceText) {
        this.referenceText = referenceText;
    }
}
