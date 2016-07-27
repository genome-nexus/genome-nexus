package org.cbioportal.genome_nexus.annotation.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Trim;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author Selcuk Onur Sumer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IsoformOverride
{
    @Trim
    @Parsed(field = "enst_id")
    @ApiModelProperty(value = "Ensembl transcript id", required = true)
    private String transcriptId;

    @Trim
    @Parsed(field = "gene_name")
    @ApiModelProperty(value = "Hugo gene symbol")
    private String geneSymbol;

    @Trim
    @Parsed(field = "refseq_id")
    @ApiModelProperty(value = "RefSeq ID")
    private String refseqId;

    @Trim
    @Parsed(field = "ccds_id")
    @ApiModelProperty(value = "CCDS ID")
    private String ccdsId;

    public String getTranscriptId()
    {
        return transcriptId;
    }

    public void setTranscriptId(String transcriptId)
    {
        this.transcriptId = transcriptId;
    }

    public String getGeneSymbol()
    {
        return geneSymbol;
    }

    public void setGeneSymbol(String geneSymbol)
    {
        this.geneSymbol = geneSymbol;
    }

    public String getRefseqId()
    {
        return refseqId;
    }

    public void setRefseqId(String refseqId)
    {
        this.refseqId = refseqId;
    }

    public String getCcdsId()
    {
        return ccdsId;
    }

    public void setCcdsId(String ccdsId)
    {
        this.ccdsId = ccdsId;
    }
}
