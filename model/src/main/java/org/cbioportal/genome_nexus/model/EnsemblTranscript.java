package org.cbioportal.genome_nexus.model;

import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Ensembl transcript
 */
//@Document(collection=EnsemblRepositoryImpl.TRANSCRIPTS_COLLECTION)
@Document(collection="ensembl.biomart_transcripts")
public class EnsemblTranscript
{
    public final static String TRANSCRIPT_ID_FIELD_NAME = "transcript_stable_id";

    @Field(value=EnsemblTranscript.TRANSCRIPT_ID_FIELD_NAME)
    private String transcriptId;

    @Field(value="gene_stable_id")
    private String geneId;

    @Field(value="hgnc_symbols")
    private List<String> hugoSymbols;

    @Field(value="protein_stable_id")
    private String proteinId;

    @Field(value="protein_length")
    private Integer proteinLength;

    @Field(value="domains")
    private List<PfamDomainRange> pfamDomains;
    
    @Field(value="exons")
    private List<ExonRange> exons;

    public String getTranscriptId() {
        return transcriptId;
    }

    public void setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
    }

    public String getGeneId() {
        return geneId;
    }

    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    public String getProteinId() {
        return proteinId;
    }

    public void setProteinId(String proteinId) {
        this.proteinId = proteinId;
    }

    public Integer getProteinLength() {
        return proteinLength;
    }

    public void setProteinLength(Integer proteinLength) {
        this.proteinLength = proteinLength;
    }

    public List<String> getHugoSymbols() {
        return this.hugoSymbols;
    }

    public void setHugoSymbols(List<String> hugoSymbols) {
        this.hugoSymbols = hugoSymbols;
    }

    public List<PfamDomainRange> getPfamDomains() {
        return this.pfamDomains;
    }

    public void setPfamDomains(List<PfamDomainRange> pfamDomains) {
        this.pfamDomains = pfamDomains;
    }
    
    public List<ExonRange> getExons() {
        return this.exons;
    }

    public void setExons(List<ExonRange> exons) {
        this.exons = exons;
    }
}

