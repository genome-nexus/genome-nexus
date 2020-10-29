package org.cbioportal.genome_nexus.service.exception;

public class CuriousCasesCommentNotFoundException extends Exception
{
    private String region;

    public CuriousCasesCommentNotFoundException(String region) {
        super();
        this.region = region;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String getMessage() {
        return "Curious cases comment not found: " + this.getRegion();
    }
}
