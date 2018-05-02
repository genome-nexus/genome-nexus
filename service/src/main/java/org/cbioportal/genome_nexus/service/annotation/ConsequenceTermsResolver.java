package org.cbioportal.genome_nexus.service.annotation;

import org.apache.commons.lang.StringUtils;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConsequenceTermsResolver
{
    private final CanonicalTranscriptResolver canonicalTranscriptResolver;

    @Autowired
    public ConsequenceTermsResolver(CanonicalTranscriptResolver canonicalTranscriptResolver)
    {
        this.canonicalTranscriptResolver = canonicalTranscriptResolver;
    }

    @Nullable
    public String resolve(TranscriptConsequence transcriptConsequence)
    {
        String consequence = null;
        List<String> consequenceTerms = this.resolveAll(transcriptConsequence);

        if (consequenceTerms != null && consequenceTerms.size() > 0) {
            consequence = StringUtils.join(consequenceTerms, ",");
        }

        return consequence;
    }

    @Nullable
    public String resolve(VariantAnnotation variantAnnotation)
    {
        return this.resolve(this.canonicalTranscriptResolver.resolve(variantAnnotation));
    }

    @Nullable
    public List<String> resolveAll(TranscriptConsequence transcriptConsequence)
    {
        List<String> consequenceTerms = null;

        if (transcriptConsequence != null) {
            consequenceTerms = transcriptConsequence.getConsequenceTerms();
        }

        return consequenceTerms;
    }

    @Nullable
    public List<String> resolveAll(VariantAnnotation variantAnnotation)
    {
        return this.resolveAll(this.canonicalTranscriptResolver.resolve(variantAnnotation));
    }
}
