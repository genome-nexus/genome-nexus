package org.cbioportal.genome_nexus.model;

import java.util.List;

public class VariantAnnotationSummary
{
    private String variant;
    private GenomicLocation genomicLocation;
    private String strandSign;
    private String variantType;
    private String assemblyName;
    private String canonicalTranscriptId;
    private List<TranscriptConsequenceSummary> transcriptConsequences;

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public GenomicLocation getGenomicLocation() {
        return genomicLocation;
    }

    public void setGenomicLocation(GenomicLocation genomicLocation) {
        this.genomicLocation = genomicLocation;
    }

    public String getStrandSign() {
        return strandSign;
    }

    public void setStrandSign(String strandSign) {
        this.strandSign = strandSign;
    }

    public String getVariantType() {
        return variantType;
    }

    public void setVariantType(String variantType) {
        this.variantType = variantType;
    }

    public String getAssemblyName() {
        return assemblyName;
    }

    public void setAssemblyName(String assemblyName) {
        this.assemblyName = assemblyName;
    }

    public String getCanonicalTranscriptId() {
        return canonicalTranscriptId;
    }

    public void setCanonicalTranscriptId(String canonicalTranscriptId) {
        this.canonicalTranscriptId = canonicalTranscriptId;
    }

    public List<TranscriptConsequenceSummary> getTranscriptConsequences() {
        return transcriptConsequences;
    }

    public void setTranscriptConsequences(List<TranscriptConsequenceSummary> transcriptConsequences) {
        this.transcriptConsequences = transcriptConsequences;
    }
}
