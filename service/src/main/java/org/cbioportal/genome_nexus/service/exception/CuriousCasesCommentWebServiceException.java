package org.cbioportal.genome_nexus.service.exception;

import org.springframework.http.HttpStatus;

public class CuriousCasesCommentWebServiceException extends DefaultWebServiceException
{
    private String region;

    public CuriousCasesCommentWebServiceException(String region, String responseBody) {
        super(responseBody);
        this.region = region;
    }

    public CuriousCasesCommentWebServiceException(String region, String responseBody, HttpStatus statusCode) {
        super(responseBody, statusCode);
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
        return "Curious cases web service error for " + this.getRegion();
    }
}
