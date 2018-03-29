package org.cbioportal.genome_nexus.service.annotation;

import org.cbioportal.genome_nexus.model.GeneXref;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.GeneXrefService;
import org.cbioportal.genome_nexus.service.exception.EnsemblWebServiceException;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            // TODO in memory cache?
            // check hashmap first before hitting api for gene cross-ref data
//            if (ensemblAccessionEntrezIdMap.containsKey(transcriptConsequence.getGeneId())) {
//                return ensemblAccessionEntrezIdMap.get(transcriptConsequence.getGeneId());
//            }

            // get xrefs from the service
            List<GeneXref> geneXrefs = this.geneXrefService.getGeneXrefs(transcriptConsequence.getGeneId());

            // filter xrefs by gene symbol first
            geneXrefs = geneXrefs.stream().filter(xref ->
                xref.getDbName().equals("EntrezGene") &&
                xref.getDisplayId().equals(transcriptConsequence.getGeneSymbol())
            ).collect(Collectors.toList());

            // if more than one xrefs, then further filter by description
            if (geneXrefs.size() > 1)
            {
                Optional<GeneXref> first = geneXrefs.stream().filter(xref ->
                    !xref.getDescription().toLowerCase().contains("pseudogene") &&
                    !xref.getDescription().toLowerCase().contains("uncharacterized")
                ).findFirst();

                if (first.isPresent()) {
                    entrezGeneId = first.get().getPrimaryId();
                }
            }
            else if (geneXrefs.size() == 1) {
                entrezGeneId = geneXrefs.get(0).getPrimaryId();
            }

            // cache if resolved successfully
            if (entrezGeneId != null) {
                // TODO ensemblAccessionEntrezIdMap.put(transcriptConsequence.getGeneId(), entrezGeneId);
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
