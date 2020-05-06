package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AminoAcidsResolver
{
    private final CanonicalTranscriptResolver canonicalTranscriptResolver;

    @Autowired
    public AminoAcidsResolver(CanonicalTranscriptResolver canonicalTranscriptResolver)
    {
        this.canonicalTranscriptResolver = canonicalTranscriptResolver;
    }

    @Nullable
    public String resolve(TranscriptConsequence transcriptConsequence)
    {
        String aminoAcids = null;

        if(transcriptConsequence != null) {
            aminoAcids = transcriptConsequence.getAminoAcids();
        }

        return aminoAcids;
    }

    @Nullable
    private String[] getSplitAminoAcids(String aminoAcids) {
        if (aminoAcids != null) {
            if (aminoAcids.contains("/")) {
                return aminoAcids.split("/");
            } else {
                // is silent mutation e.g. E -> E
                String[] splitAAs = { aminoAcids, aminoAcids };
                return splitAAs;
            }
        } else {
            return null;
        }
    }

    @Nullable
    public String getRefAminoAcid(TranscriptConsequence transcriptConsequence)
    {
        String[] aminoAcids = this.getSplitAminoAcids(this.resolve(transcriptConsequence));

        if (aminoAcids != null) {
            return aminoAcids[0];
        } else {
            return null;
        }
    }

    @Nullable
    public String getAltAminoAcid(TranscriptConsequence transcriptConsequence)
    {
        String[] aminoAcids = this.getSplitAminoAcids(this.resolve(transcriptConsequence));

        if (aminoAcids != null) {
            return aminoAcids[1];
        } else {
            return null;
        }
    }

    public String resolve(VariantAnnotation variantAnnotation)
    {
        return this.resolve(this.canonicalTranscriptResolver.resolve(variantAnnotation));
    }

}
