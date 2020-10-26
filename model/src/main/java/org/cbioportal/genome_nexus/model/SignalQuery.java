package org.cbioportal.genome_nexus.model;

public class SignalQuery
{
    private SignalQueryType queryType;
    private SignalMatchType matchType;
    private String hugoSymbol; // e.g: BRCA2, BRAF
    private String alteration; // e.g: V600E
    private String region; // e.g: 13:32968940-32968940
    private String variant; // e.g: 17:g.37880220T>C
    private String description; // optional free-form info

    public SignalQueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(SignalQueryType queryType) {
        this.queryType = queryType;
    }

    public SignalMatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(SignalMatchType matchType) {
        this.matchType = matchType;
    }

    public String getHugoSymbol() {
        return hugoSymbol;
    }

    public void setHugoSymbol(String hugoSymbol) {
        this.hugoSymbol = hugoSymbol;
    }

    public String getAlteration() {
        return alteration;
    }

    public void setAlteration(String alteration) {
        this.alteration = alteration;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
