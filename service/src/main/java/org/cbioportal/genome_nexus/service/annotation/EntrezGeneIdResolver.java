package org.cbioportal.genome_nexus.service.annotation;

import org.cbioportal.genome_nexus.component.annotation.CanonicalTranscriptResolver;
import org.cbioportal.genome_nexus.model.EnsemblGene;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.EnsemblService;
import org.cbioportal.genome_nexus.service.exception.EnsemblWebServiceException;
import org.cbioportal.genome_nexus.service.exception.NoEnsemblGeneIdForHugoSymbolException;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class EntrezGeneIdResolver
{
    private final CanonicalTranscriptResolver canonicalTranscriptResolver;
    private final EnsemblService ensemblService;

    public EntrezGeneIdResolver(CanonicalTranscriptResolver canonicalTranscriptResolver,
                                EnsemblService ensemblService)
    {
        this.canonicalTranscriptResolver = canonicalTranscriptResolver;
        this.ensemblService = ensemblService;
    }

    @Nullable
    public String resolve(TranscriptConsequence transcriptConsequence) throws EnsemblWebServiceException
    {
        String entrezGeneId = null;

        if (transcriptConsequence != null &&
            transcriptConsequence.getGeneSymbol() != null &&
            !transcriptConsequence.getGeneSymbol().trim().isEmpty())
        {
            EnsemblGene ensemblGene = null;

            try {
                ensemblGene = this.ensemblService
                        .getCanonicalEnsemblGeneIdByHugoSymbol(transcriptConsequence.getGeneSymbol());
            } catch (NoEnsemblGeneIdForHugoSymbolException e) {
                // ignore silently
            }

            if (ensemblGene != null) {
                entrezGeneId = ensemblGene.getEntrezGeneId();
            }
        }

        return entrezGeneId;
    }

    @Nullable
    public String resolve(VariantAnnotation variantAnnotation) throws EnsemblWebServiceException
    {
        return this.resolve(this.canonicalTranscriptResolver.resolve(variantAnnotation));
    }
}
