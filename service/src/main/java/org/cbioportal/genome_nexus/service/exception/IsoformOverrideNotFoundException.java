package org.cbioportal.genome_nexus.service.exception;

public class IsoformOverrideNotFoundException extends Exception
{
    private String source;
    private String transcriptId;

    public IsoformOverrideNotFoundException(String source)
    {
        super();
        this.source = source;
        this.transcriptId = null;
    }

    public IsoformOverrideNotFoundException(String source, String transcriptId)
    {
        super();
        this.source = source;
        this.transcriptId = transcriptId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTranscriptId() {
        return transcriptId;
    }

    public void setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
    }
}
