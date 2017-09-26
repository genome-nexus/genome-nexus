package org.cbioportal.genome_nexus.annotation.util.parse;

import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Trim;

public class HugoSymbolRecord {
    @Trim
    @Parsed(field = "hgnc_symbol")
    private String hugoSymbol;

    @Trim
    @Parsed(field = "ensembl_canonical_transcript")
    private String ensemblCanonicalTranscript;

    @Trim
    @Parsed(field = "genome_nexus_canonical_transcript")
    private String genomeNexusCanonicalTranscript;

    @Trim
    @Parsed(field = "uniprot_canonical_transcript")
    private String uniprotCanonicalTranscript;

    @Trim
    @Parsed(field = "mskcc_canonical_trancript")
    private String mskccCanonicalTranscript;

    public String getHugoSymbol() {
        return hugoSymbol;
    }

    public void setHugoSymbol(String hugoSymbol) {
        this.hugoSymbol = hugoSymbol;
    }

    public String getEnsemblCanonicalTranscript() {
        return ensemblCanonicalTranscript;
    }

    public void setEnsemblCanonicalTranscript(String ensemblCanonicalTranscript) {
        this.ensemblCanonicalTranscript = ensemblCanonicalTranscript;
    }

    public String getGenomeNexusCanonicalTranscript() {
        return genomeNexusCanonicalTranscript;
    }

    public void setGenomeNexusCanonicalTranscript(String genomeNexusCanonicalTranscript) {
        this.genomeNexusCanonicalTranscript = genomeNexusCanonicalTranscript;
    }

    public String getUniprotCanonicalTranscript() {
        return uniprotCanonicalTranscript;
    }

    public void setUniprotCanonicalTranscript(String uniprotCanonicalTranscript) {
        this.uniprotCanonicalTranscript = uniprotCanonicalTranscript;
    }

    public String getMskccCanonicalTranscript() {
        return mskccCanonicalTranscript;
    }

    public void setMskccCanonicalTranscript(String mskccCanonicalTranscript) {
        this.mskccCanonicalTranscript = mskccCanonicalTranscript;
    }
}
