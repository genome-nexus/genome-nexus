package org.cbioportal.genome_nexus.service.annotation;

import org.cbioportal.genome_nexus.model.GeneXref;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.GeneXrefService;
import org.cbioportal.genome_nexus.service.exception.EnsemblWebServiceException;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class EntrezGeneIdResolver
{
    private final CanonicalTranscriptResolver canonicalTranscriptResolver;
    private final GeneXrefService geneXrefService;

    public EntrezGeneIdResolver(CanonicalTranscriptResolver canonicalTranscriptResolver,
                                GeneXrefService geneXrefService)
    {
        this.canonicalTranscriptResolver = canonicalTranscriptResolver;
        this.geneXrefService = geneXrefService;
    }

    @Nullable
    public String resolve(TranscriptConsequence transcriptConsequence) throws EnsemblWebServiceException
    {
        String entrezGeneId = null;

        if (transcriptConsequence != null &&
            transcriptConsequence.getGeneId() != null &&
            !transcriptConsequence.getGeneId().trim().isEmpty())
        {
            GeneXref geneXref = this.geneXrefService.getEntrezGeneXref(transcriptConsequence.getGeneId(),
                transcriptConsequence.getGeneSymbol());

            if (geneXref != null) {
                entrezGeneId = geneXref.getPrimaryId();
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
