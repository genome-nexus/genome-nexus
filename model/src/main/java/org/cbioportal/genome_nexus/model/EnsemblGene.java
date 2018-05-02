package org.cbioportal.genome_nexus.model;

/**
 * Ensembl transcript
 */
public class EnsemblGene
{
    private String geneId;
    private String[] synonyms;
    private String[] previousSymbols;

    public EnsemblGene(EnsemblCanonical cn)
    {
        this.geneId = cn.getEnsemblCanonicalGeneId();
        this.synonyms = cn.getSynonyms();
        this.previousSymbols = cn.getPreviousSymbols();
    }

    public String getGeneId() {
        return this.geneId;
    }

    public String[] getSynonyms() {
        return this.synonyms;
    }

    public String[] getPreviousSymbols() {
        return this.previousSymbols;
    }
}

