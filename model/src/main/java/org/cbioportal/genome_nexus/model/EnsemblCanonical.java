package org.cbioportal.genome_nexus.model;

import java.util.regex.Pattern;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Ensembl canonical mappings from Hugo Symbol to Ensembl Gene and Transcript.
 */
@Document(collection="ensembl.canonical_transcript_per_hgnc")
public class EnsemblCanonical
{
    @Field(value="hgnc_symbol")
    private String hugoSymbol;
    @Field(value="ensembl_canonical_gene")
    private String ensemblCanonicalGeneId;
    @Field(value="ensembl_canonical_transcript")
    private String ensemblCanonicalTranscriptId;
    @Field(value="genome_nexus_canonical_transcript")
    private String genomeNexusCanonicalTranscriptId;
    @Field(value="uniprot_canonical_transcript")
    private String uniprotCanonicalTranscriptId;
    @Field(value="mskcc_canonical_trancript")
    private String mskccCanonicalTranscriptId;
    @Field(value="hgnc_id")
    private String hgncId;
    @Field(value="approved_name")
    private String approvedName;
    @Field(value="status")
    private String status;
    @Field(value="previous_symbols")
    private String previousSymbols; // comma separated
    @Field(value="synonyms")
    private String synonyms; // comma separated
    @Field(value="accession_numbers")
    private String accessionNumbers; // comma separated
    @Field(value="refseq_ids")
    private String refseqIds;
    @Field(value="uniprot_id")
    private String uniprotId;
    @Field(value="entrez_gene_id")
    private String entrezGeneId;

    private static String[] splitByComma(String input) {
        Pattern p = Pattern.compile("\\s*,\\s*");
        String[] output = p.split(input);
        if (output.length == 0 || (output.length == 1 && output[0].equals(""))) {
            output = null;
        }
        return output;
    }

    public String getCanonicalTranscriptId(String isoformOverrideSource) {
        String canonicalTranscriptId;
        if (isoformOverrideSource == null) {
            return this.uniprotCanonicalTranscriptId;
        } else {
            switch (isoformOverrideSource) {
                case "mskcc":
                    canonicalTranscriptId = this.mskccCanonicalTranscriptId;
                case "uniprot":
                    canonicalTranscriptId = this.uniprotCanonicalTranscriptId;
                case "ensembl":
                    canonicalTranscriptId = this.ensemblCanonicalTranscriptId;
                default:
                    canonicalTranscriptId = this.uniprotCanonicalTranscriptId;
            }
            return canonicalTranscriptId;
        }
    }

    public String getHugoSymbol() {
        return this.hugoSymbol;
    }

    public String[] getSynonyms() {
        return EnsemblCanonical.splitByComma(this.synonyms);
    }

    public String[] getPreviousSymbols() {
        return EnsemblCanonical.splitByComma(this.previousSymbols);
    }

    public String getEnsemblCanonicalGeneId() {
        return this.ensemblCanonicalGeneId;
    }

    public String getEnsemblCanonicalTranscriptId() {
        return this.ensemblCanonicalTranscriptId;
    }

    public String getEntrezGeneId() {
        // remove .0 for backwards compatibility with databases where
        // entrez_gene_id field is a float
        if (this.entrezGeneId != null && this.entrezGeneId.endsWith(".0")) {
            return this.entrezGeneId.substring(0, this.entrezGeneId.length() - 2);
        } else {
            return this.entrezGeneId;
        }
    }
}