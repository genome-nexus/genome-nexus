package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExonResolver
{
    private final CanonicalTranscriptResolver canonicalTranscriptResolver;

    @Autowired
    public ExonResolver(CanonicalTranscriptResolver canonicalTranscriptResolver)
    {
        this.canonicalTranscriptResolver = canonicalTranscriptResolver;
    }

    @Nullable
    public String resolve(TranscriptConsequence transcriptConsequence)
    {
        String exon = null;

        if (transcriptConsequence != null) {
            exon = transcriptConsequence.getExon();
        }

        return exon;
    }

    @Nullable
    public String resolve(VariantAnnotation variantAnnotation)
    {
        return this.resolve(this.canonicalTranscriptResolver.resolve(variantAnnotation));
    }
}
