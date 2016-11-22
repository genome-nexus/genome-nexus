/*
 * Copyright (c) 2016 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal Genome Nexus.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.cbioportal.genome_nexus.annotation.domain;

import com.fasterxml.jackson.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TranscriptConsequence
{
    private String transcriptId;

    private String hgvsp;
    private String hgvsc;
    private String variantAllele;
    private String codons;
    private String proteinId;
    private String proteinStart;
    private String proteinEnd;
    private String geneSymbol;
    private String geneId;
    private String aminoAcids;
    private String hgncId;
    private String canonical;

    private List<String> refseqTranscriptIds;
    private List<String> consequenceTerms;

    @JsonIgnore
    private Map<String, Object> dynamicProps;

    public TranscriptConsequence()
    {
        this(null);
    }

    public TranscriptConsequence(String transcriptId)
    {
        this.transcriptId = transcriptId;
        this.dynamicProps = new LinkedHashMap<>();
    }

    @Field(value="transcript_id")
    @JsonProperty(value="transcript_id", required = true)
    @ApiModelProperty(value = "Ensembl transcript id", required = true)
    public String getTranscriptId()
    {
        return transcriptId;
    }

    public void setTranscriptId(String transcriptId)
    {
        this.transcriptId = transcriptId;
    }

    @Field(value="hgvsp")
    @JsonProperty(value="hgvsp", required = true)
    @ApiModelProperty(value = "HGVSp", required = false)
    public String getHgvsp()
    {
        return hgvsp;
    }

    public void setHgvsp(String hgvsp)
    {
        this.hgvsp = hgvsp;
    }

    @Field(value="hgvsc")
    @JsonProperty(value="hgvsc", required = true)
    @ApiModelProperty(value = "HGVSc", required = false)
    public String getHgvsc()
    {
        return hgvsc;
    }

    public void setHgvsc(String hgvsc)
    {
        this.hgvsc = hgvsc;
    }

    @Field(value="variant_allele")
    @JsonProperty(value="variant_allele", required = true)
    @ApiModelProperty(value = "Variant allele", required = false)
    public String getVariantAllele()
    {
        return variantAllele;
    }

    public void setVariantAllele(String variantAllele)
    {
        this.variantAllele = variantAllele;
    }

    @Field(value="codons")
    @JsonProperty(value="codons", required = true)
    @ApiModelProperty(value = "Codons", required = false)
    public String getCodons()
    {
        return codons;
    }

    public void setCodons(String codons)
    {
        this.codons = codons;
    }

    @Field(value="protein_id")
    @JsonProperty(value="protein_id", required = true)
    @ApiModelProperty(value = "Ensembl protein id", required = false)
    public String getProteinId()
    {
        return proteinId;
    }

    public void setProteinId(String proteinId)
    {
        this.proteinId = proteinId;
    }

    @Field(value="protein_start")
    @JsonProperty(value="protein_start", required = true)
    @ApiModelProperty(value = "Protein start position", required = false)
    public String getProteinStart()
    {
        return proteinStart;
    }

    public void setProteinStart(String proteinStart)
    {
        this.proteinStart = proteinStart;
    }

    @Field(value="protein_end")
    @JsonProperty(value="protein_end", required = true)
    @ApiModelProperty(value = "Protein end position", required = false)
    public String getProteinEnd()
    {
        return proteinEnd;
    }

    public void setProteinEnd(String proteinEnd)
    {
        this.proteinEnd = proteinEnd;
    }

    @Field(value="gene_symbol")
    @JsonProperty(value="gene_symbol", required = true)
    @ApiModelProperty(value = "Hugo gene symbol", required = false)
    public String getGeneSymbol()
    {
        return geneSymbol;
    }

    public void setGeneSymbol(String geneSymbol)
    {
        this.geneSymbol = geneSymbol;
    }

    @Field(value="gene_id")
    @JsonProperty(value="gene_id", required = true)
    @ApiModelProperty(value = "Ensembl gene id", required = false)
    public String getGeneId()
    {
        return geneId;
    }

    public void setGeneId(String geneId)
    {
        this.geneId = geneId;
    }

    @Field(value="amino_acids")
    @JsonProperty(value="amino_acids", required = true)
    @ApiModelProperty(value = "Amino acids", required = false)
    public String getAminoAcids()
    {
        return aminoAcids;
    }

    public void setAminoAcids(String aminoAcids)
    {
        this.aminoAcids = aminoAcids;
    }

    @Field(value="hgnc_id")
    @JsonProperty(value="hgnc_id", required = true)
    @ApiModelProperty(value = "HGNC id", required = false)
    public String getHgncId()
    {
        return hgncId;
    }

    public void setHgncId(String hgncId)
    {
        this.hgncId = hgncId;
    }

    @Field(value="canonical")
    @JsonProperty(value="canonical", required = true)
    @ApiModelProperty(value = "Canonical transcript indicator", required = false)
    public String getCanonical()
    {
        return canonical;
    }

    public void setCanonical(String canonical)
    {
        this.canonical = canonical;
    }

    @Field(value="refseq_transcript_ids")
    @JsonProperty(value="refseq_transcript_ids", required = true)
    @ApiModelProperty(value = "List of RefSeq transcript ids", required = false)
    public List<String> getRefseqTranscriptIds()
    {
        return refseqTranscriptIds;
    }

    public void setRefseqTranscriptIds(List<String> refseqTranscriptIds)
    {
        this.refseqTranscriptIds = refseqTranscriptIds;
    }

    @Field(value="consequence_terms")
    @JsonProperty(value="consequence_terms", required = true)
    @ApiModelProperty(value = "List of consequence terms", required = false)
    public List<String> getConsequenceTerms()
    {
        return consequenceTerms;
    }

    public void setConsequenceTerms(List<String> consequenceTerms)
    {
        this.consequenceTerms = consequenceTerms;
    }

    // this is to dynamically add additional properties for this transcript
    // anything added into the dynamic props map will be returned as an additional
    // json property

    @JsonAnySetter
    public void setDynamicProp(String key, Object value)
    {
        this.dynamicProps.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getDynamicProps()
    {
        return this.dynamicProps;
    }
}
