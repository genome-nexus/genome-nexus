package org.cbioportal.genome_nexus.service.enricher;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.EnsemblService;
import org.cbioportal.genome_nexus.service.OncokbService;
import org.cbioportal.genome_nexus.util.IsoformOverrideSource;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class IsoformAnnotationEnricher extends BaseAnnotationEnricher
{
    String source;
    EnsemblService ensemblService;
    OncokbService  oncokbService;

    public IsoformAnnotationEnricher(
        String id,
        String source,
        EnsemblService ensemblService,
        OncokbService  oncokbService
    ) {
        super(id);
        this.source = source;
        this.ensemblService = ensemblService;
        this.oncokbService = oncokbService;
    }

    @Override
    public void enrich(VariantAnnotation annotation)
    {
        // no transcripts to enrich, abort.
        if (annotation.getTranscriptConsequences() == null) {
            return;
        }

        // get transcript overrides for the given isoform override source
        // if no isoform override source is provided, then the default is uniprot
        Set<String> predefinedCanonicalTranscriptIds = this.ensemblService.getCanonicalTranscriptIdsBySource(
            IsoformOverrideSource.getOrDefault(this.source)
        );

        List<TranscriptConsequence> canonicalTranscriptCandidates = this.findCanonicalTranscriptCandidates(
            annotation.getTranscriptConsequences(),
            predefinedCanonicalTranscriptIds
        );

        List<TranscriptConsequence> canonicalTranscriptCandidatesFilteredByOncokb = canonicalTranscriptCandidates
            .stream()
            .filter(t -> oncokbService.getOncokbGeneSymbolList().contains(t.getGeneSymbol()))
            .collect(Collectors.toList());

        // if at least one canonical transcript candidate is found
        // then mark all transcripts as non-canonical.
        //
        // if no override, then we should leave the transcript list intact
        // (rely on the canonical info provided by the web service in that case).
        if (canonicalTranscriptCandidates.size() > 0) {
            for (TranscriptConsequence transcript: annotation.getTranscriptConsequences()) {
                transcript.setCanonical(null);
            }
        }
        // if there are multiple canonical transcript candidates, we filter by if there are oncokb genes
        // and if only one transcript is oncokb gene, we set it as canonical, and set the rest as non-canonical
        // if there are more than one oncokb gene, we keep them as candidates and set other transcripts as non-canonical
        if (canonicalTranscriptCandidates.size() > 1 && canonicalTranscriptCandidatesFilteredByOncokb.size() > 0) {
            canonicalTranscriptCandidates = canonicalTranscriptCandidatesFilteredByOncokb;
        }

        // override the canonical field for all the candidates
        for (TranscriptConsequence transcript: canonicalTranscriptCandidates) {
            transcript.setCanonical("1");
        }
    }

    /**
     * Tries to find a matching transcript consequence by using
     * the given predefined canonical assignment.
     */
    private List<TranscriptConsequence> findCanonicalTranscriptCandidates(
        List<TranscriptConsequence> transcriptConsequences,
        Set<String> predefinedCanonicalTranscriptIds
    ) {
        List<TranscriptConsequence> transcripts = Collections.emptyList();

        if (predefinedCanonicalTranscriptIds != null) {
            transcripts = transcriptConsequences
                .stream()
                .filter(t -> predefinedCanonicalTranscriptIds.contains(t.getTranscriptId()))
                .collect(Collectors.toList());
        }

        return transcripts;
    }
}
