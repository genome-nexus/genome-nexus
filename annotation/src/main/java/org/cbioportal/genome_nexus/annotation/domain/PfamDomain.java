package org.cbioportal.genome_nexus.annotation.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Trim;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author Selcuk Onur Sumer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PfamDomain
{
    @Trim
    @Parsed(field = "Gene stable ID")
    @ApiModelProperty(value = "Ensembl gene id", required = true)
    private String geneId;

    @Trim
    @Parsed(field = "Transcript stable ID")
    @ApiModelProperty(value = "Ensembl transcript id", required = true)
    private String transcriptId;

    @Trim
    @Parsed(field = "Protein stable ID")
    @ApiModelProperty(value = "Ensembl translation id", required = true)
    private String proteinId;

    @Trim
    @Parsed(field = "Gene name")
    @ApiModelProperty(value = "Hugo gene symbol")
    private String geneSymbol;

    @Trim
    @Parsed(field = "Pfam domain ID")
    @ApiModelProperty(value = "PFAM domain id")
    private String pfamDomainId;

    @Trim
    @Parsed(field = "Pfam domain start")
    @ApiModelProperty(value = "PFAM domain start")
    private String pfamDomainStart;

    @Trim
    @Parsed(field = "Pfam domain end")
    @ApiModelProperty(value = "PFAM domain end")
    private String getPfamDomainEnd;

    @ApiModelProperty(value = "PFAM domain name")
    private String pfamDomainName;

    @ApiModelProperty(value = "PFAM domain description")
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

    public String getPfamDomainStart() {
        return pfamDomainStart;
    }

    public void setPfamDomainStart(String pfamDomainStart) {
        this.pfamDomainStart = pfamDomainStart;
    }

    public String getGetPfamDomainEnd() {
        return getPfamDomainEnd;
    }

    public void setGetPfamDomainEnd(String getPfamDomainEnd) {
        this.getPfamDomainEnd = getPfamDomainEnd;
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
