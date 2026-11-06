package org.cbioportal.genome_nexus.service.exception;

import org.springframework.http.HttpStatus;

public class AlphaGenomeWebServiceException extends Exception
{
    private String responseBody;
    private HttpStatus statusCode;

    public AlphaGenomeWebServiceException(String responseBody) {
        super();
        this.responseBody = responseBody;
    }

    public AlphaGenomeWebServiceException(String responseBody, HttpStatus statusCode) {
        super();
        this.responseBody = responseBody;
        this.statusCode = statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return "AlphaGenome web service exception: " + this.getResponseBody();
    }
}
