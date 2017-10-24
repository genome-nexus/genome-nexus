package org.cbioportal.genome_nexus.model;

import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Trim;

/**
 * @author Selcuk Onur Sumer
 */
public class PfamDomain
{
    @Trim
    @Parsed(field = "Gene stable ID")
    private String geneId;

    @Trim
    @Parsed(field = "Transcript stable ID")
    private String transcriptId;

    @Trim
    @Parsed(field = "Protein stable ID")
    private String proteinId;

    @Trim
    @Parsed(field = "Gene name")
    private String geneSymbol;

    @Trim
    @Parsed(field = "Pfam domain ID")
    private String pfamDomainId;

    @Trim
    @Parsed(field = "Pfam domain start")
    private Integer pfamDomainStart;

    @Trim
    @Parsed(field = "Pfam domain end")
    private Integer pfamDomainEnd;

    private String pfamDomainName;

    private String pfamDomainDescription;

    public String getGeneId() {
        return geneId;
    }

    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    public String getTranscriptId() {
        return transcriptId;
    }

    public void setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
    }

    public String getProteinId() {
        return proteinId;
    }

    public void setProteinId(String proteinId) {
        this.proteinId = proteinId;
    }

    public String getGeneSymbol() {
        return geneSymbol;
    }

    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol;
    }

    public String getPfamDomainId() {
        return pfamDomainId;
    }

    public void setPfamDomainId(String pfamDomainId) {
        this.pfamDomainId = pfamDomainId;
    }

    public Integer getPfamDomainStart() {
        return pfamDomainStart;
    }

    public void setPfamDomainStart(Integer pfamDomainStart) {
        this.pfamDomainStart = pfamDomainStart;
    }

    public Integer getPfamDomainEnd() {
        return pfamDomainEnd;
    }

    public void setPfamDomainEnd(Integer pfamDomainEnd) {
        this.pfamDomainEnd = pfamDomainEnd;
    }

    public String getPfamDomainName() {
        return pfamDomainName;
    }

    public void setPfamDomainName(String pfamDomainName) {
        this.pfamDomainName = pfamDomainName;
    }

    public String getPfamDomainDescription() {
        return pfamDomainDescription;
    }

    public void setPfamDomainDescription(String pfamDomainDescription) {
        this.pfamDomainDescription = pfamDomainDescription;
    }
}
