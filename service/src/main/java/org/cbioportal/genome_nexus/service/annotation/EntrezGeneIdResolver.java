package org.cbioportal.genome_nexus.service.annotation;

import java.util.*;
import org.cbioportal.genome_nexus.component.annotation.CanonicalTranscriptResolver;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.EnsemblService;
import org.cbioportal.genome_nexus.service.exception.EnsemblWebServiceException;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class EntrezGeneIdResolver
{
    private final EnsemblService ensemblService;

    public EntrezGeneIdResolver(EnsemblService ensemblService)
    {
        this.ensemblService = ensemblService;
    }

    @Nullable
    public String resolve(TranscriptConsequence transcriptConsequence) throws EnsemblWebServiceException
    {
        return this.resolve(transcriptConsequence, null);
    }

    /**
     * Resolves the Entrez Gene Id for the given transcript consequence.
     *
     * If a resolvedHugoGeneSymbol is provided (e.g. the up-to-date official HGNC symbol
     * produced by HugoGeneSymbolResolver, which may differ from the raw VEP gene symbol
     * when the gene has been renamed), it is tried first since the Entrez lookup map is
     * keyed by current official symbols. Falls back to the raw VEP gene symbol otherwise,
     * to preserve prior behavior for callers that don't have a resolved symbol available.
     *
     * @param transcriptConsequence
     * @param resolvedHugoGeneSymbol
     * @return
     */
    @Nullable
    public String resolve(TranscriptConsequence transcriptConsequence, @Nullable String resolvedHugoGeneSymbol) throws EnsemblWebServiceException
    {
        String entrezGeneId = null;

        if (resolvedHugoGeneSymbol != null && !resolvedHugoGeneSymbol.trim().isEmpty())
        {
            entrezGeneId = this.ensemblService.getEntrezGeneIdByHugoSymbol(resolvedHugoGeneSymbol);
        }

        if (entrezGeneId == null &&
            transcriptConsequence != null &&
            transcriptConsequence.getGeneSymbol() != null &&
            !transcriptConsequence.getGeneSymbol().trim().isEmpty())
        {
            // NOTE: Transcript consequence does not have an entrez gene id field, therefore can
            // only search for the entrez gene id by the hugo symbol.
            // TODO: allow searching in gene aliases and/or by entrez gene id to get the hugo symbol
            entrezGeneId = this.ensemblService.getEntrezGeneIdByHugoSymbol(transcriptConsequence.getGeneSymbol());
        }

        return entrezGeneId;
    }
}
