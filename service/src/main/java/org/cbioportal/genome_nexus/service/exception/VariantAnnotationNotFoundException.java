package org.cbioportal.genome_nexus.service.exception;

public class VariantAnnotationNotFoundException extends Exception
{
    private String variant;
    private String response;

    public VariantAnnotationNotFoundException(String variant, String response) {
        super();
        this.variant = variant;
        this.response = response;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
