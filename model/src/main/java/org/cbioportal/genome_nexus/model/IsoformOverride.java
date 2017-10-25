package org.cbioportal.genome_nexus.model;

import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Trim;

/**
 * @author Selcuk Onur Sumer
 */
public class IsoformOverride
{
    @Trim
    @Parsed(field = "enst_id")
    private String transcriptId;

    @Trim
    @Parsed(field = "gene_name")
    private String geneSymbol;

    @Trim
    @Parsed(field = "refseq_id")
    private String refseqId;

    @Trim
    @Parsed(field = "ccds_id")
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
