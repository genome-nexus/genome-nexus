package org.cbioportal.genome_nexus.service.exception;

public class AlphaGenomeNotFoundException extends Exception
{
    private String variant;

    public AlphaGenomeNotFoundException(String variant) {
        super();
        this.variant = variant;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    @Override
    public String getMessage() {
        return "AlphaGenome annotation not found for variant: " + this.getVariant();
    }
}
