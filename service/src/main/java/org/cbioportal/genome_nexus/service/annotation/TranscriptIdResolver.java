package org.cbioportal.genome_nexus.service.annotation;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class TranscriptIdResolver
{
    private final CanonicalTranscriptResolver canonicalTranscriptResolver;

    public TranscriptIdResolver(CanonicalTranscriptResolver canonicalTranscriptResolver)
    {
        this.canonicalTranscriptResolver = canonicalTranscriptResolver;
    }

    @Nullable
    public String resolve(TranscriptConsequence transcriptConsequence)
    {
        String transcriptId = null;

        if (transcriptConsequence != null) {
            transcriptId = transcriptConsequence.getTranscriptId();
        }

        return transcriptId;
    }

    @Nullable
    public String resolve(VariantAnnotation variantAnnotation)
    {
        return this.resolve(this.canonicalTranscriptResolver.resolve(variantAnnotation));
    }
}
