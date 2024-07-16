package org.cbioportal.genome_nexus.model;

import java.util.List;

public class IntergenicConsequenceSummary {
    private String impact;

    public String getImpact()
    {
        return impact;
    }

    public void setImpact(String impact)
    {
        this.impact = impact;
    }

    private String variantAllele;

    public String getVariantAllele()
    {
        return variantAllele;
    }

    public void setVariantAllele(String variantAllele)
    {
        this.variantAllele = variantAllele;
    }

    private List<String> consequenceTerms;

    public List<String> getConsequenceTerms()
    {
        return consequenceTerms;
    }

    public void setConsequenceTerms(List<String> consequenceTerms)
    {
        this.consequenceTerms = consequenceTerms;
    }

    private String variantClassification;

    public String getVariantClassification()
    {
        return variantClassification;
    }

    public void setVariantClassification(String variantClassification)
    {
        this.variantClassification = variantClassification;
    }
}
