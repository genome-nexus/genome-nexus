package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;


@Component
public class HugoGeneSymbolResolver
{
    @Nullable
    public String resolve(TranscriptConsequence transcriptConsequence)
    {
        String hugoSymbol = null;

        if (transcriptConsequence != null &&
            transcriptConsequence.getGeneSymbol() != null &&
            !transcriptConsequence.getGeneSymbol().trim().isEmpty())
        {
            hugoSymbol = transcriptConsequence.getGeneSymbol();
        }

        return hugoSymbol;
    }
}
