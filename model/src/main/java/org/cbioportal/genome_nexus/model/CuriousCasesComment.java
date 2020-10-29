package org.cbioportal.genome_nexus.model;

public class CuriousCasesComment {
    private String region;
    private String comment;
    private String pubmedIds;

    public String getComment() {
        return comment;
    }

    public String getRegion() {
        return region;
    }

    public String getPubmedIds() {
        return pubmedIds;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setPubmedIds(String pubmedIds) {
        this.pubmedIds = pubmedIds;
    }
}
