package org.cbioportal.genome_nexus.service.exception;

public class EnsemblTranscriptNotFoundException extends Exception
{
    private String id;

    public EnsemblTranscriptNotFoundException(String id)
    {
        super();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
