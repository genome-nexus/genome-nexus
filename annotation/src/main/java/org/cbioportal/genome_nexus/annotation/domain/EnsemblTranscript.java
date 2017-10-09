package org.cbioportal.genome_nexus.annotation.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Trim;
import io.swagger.annotations.ApiModelProperty;

/**
 * Ensembl transcript
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnsemblTranscript
{
    @Trim
    @Parsed(field = "transcript_stable_id")
    @ApiModelProperty(value = "Ensembl transcript id", required = true)
    private String transcriptId;

    @Trim
    @Parsed(field = "gene_stable_id")
    @ApiModelProperty(value = "Ensembl gene id", required = true)
    private String geneId;

    @Trim
    @Parsed(field = "protein_stable_id")
    @ApiModelProperty(value = "Ensembl protein id", required = true)
    private String proteinId;

    @Trim
    @Parsed(field = "protein_length")
    @ApiModelProperty(value = "Length of protein")
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

