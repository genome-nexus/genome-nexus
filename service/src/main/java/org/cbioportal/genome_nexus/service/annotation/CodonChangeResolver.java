package org.cbioportal.genome_nexus.service.annotation;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CodonChangeResolver
{
    private final CanonicalTranscriptResolver canonicalTranscriptResolver;

    @Autowired
    public CodonChangeResolver(CanonicalTranscriptResolver canonicalTranscriptResolver)
    {
        this.canonicalTranscriptResolver = canonicalTranscriptResolver;
    }

    @Nullable
    public String resolve(TranscriptConsequence transcriptConsequence)
    {
        String codonChange = null;

        if(transcriptConsequence != null) {
            codonChange = transcriptConsequence.getCodons();
        }

        return codonChange;
    }

    public String resolve(VariantAnnotation variantAnnotation)
    {
        return this.resolve(this.canonicalTranscriptResolver.resolve(variantAnnotation));
    }

}
