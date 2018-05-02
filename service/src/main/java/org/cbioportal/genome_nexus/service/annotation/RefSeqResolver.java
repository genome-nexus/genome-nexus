package org.cbioportal.genome_nexus.service.annotation;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RefSeqResolver
{
    private final CanonicalTranscriptResolver canonicalTranscriptResolver;

    @Autowired
    public RefSeqResolver(CanonicalTranscriptResolver canonicalTranscriptResolver)
    {
        this.canonicalTranscriptResolver = canonicalTranscriptResolver;
    }

    @Nullable
    public String resolve(VariantAnnotation variantAnnotation)
    {
        return this.resolve(this.canonicalTranscriptResolver.resolve(variantAnnotation));
    }

    @Nullable
    public List<String> resolveAll(VariantAnnotation variantAnnotation)
    {
        return this.resolveAll(this.canonicalTranscriptResolver.resolve(variantAnnotation));
    }

    @Nullable
    public String resolve(TranscriptConsequence transcriptConsequence)
    {
        String refSeq = null;

        List<String> refseqTranscriptIds = this.resolveAll(transcriptConsequence);

        // just return the first one
        if(refseqTranscriptIds != null && refseqTranscriptIds.size() > 0) {
            refSeq = refseqTranscriptIds.get(0);
        }

        return refSeq;
    }

    @Nullable
    public List<String> resolveAll(TranscriptConsequence transcriptConsequence)
    {
        List<String> refseqTranscriptIds = null;

        if(transcriptConsequence != null)
        {
            if (transcriptConsequence.getRefseqTranscriptIds() != null)
            {
                refseqTranscriptIds = transcriptConsequence.getRefseqTranscriptIds();
            }
        }

        return refseqTranscriptIds;
    }
}
