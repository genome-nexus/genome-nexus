package org.cbioportal.genome_nexus.annotation.domain;

import org.cbioportal.genome_nexus.annotation.domain.internal.EnsemblRepositoryImpl;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Ensembl transcript
 */
@Document(collection=EnsemblRepositoryImpl.TRANSCRIPTS_COLLECTION)
public class EnsemblTranscript
{
    public final static String TRANSCRIPT_ID_FIELD_NAME = "transcript_stable_id";

    @Field(value=EnsemblTranscript.TRANSCRIPT_ID_FIELD_NAME)
    private String transcriptId;

    @Field(value="gene_stable_id")
    private String geneId;

    @Field(value="protein_stable_id")
    private String proteinId;

    @Field(value="protein_length")
    private String proteinLength;

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

    public String getProteinLength() {
        return proteinLength;
    }

    public void setProteinLength(String proteinLength) {
        this.proteinLength = proteinLength;
    }
}

