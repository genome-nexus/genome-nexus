package org.cbioportal.genome_nexus.annotation.service.internal;

import org.cbioportal.genome_nexus.annotation.service.AnnotationEnricher;
import org.cbioportal.genome_nexus.annotation.domain.IsoformOverride;
import org.cbioportal.genome_nexus.annotation.domain.TranscriptConsequence;
import org.cbioportal.genome_nexus.annotation.domain.VariantAnnotation;
import org.cbioportal.genome_nexus.annotation.service.IsoformOverrideService;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public class IsoformAnnotationEnricher implements AnnotationEnricher
{
    String source;
    IsoformOverrideService service;

    public IsoformAnnotationEnricher(String source, IsoformOverrideService service)
    {
        this.source = source;
        this.service = service;
    }

    @Override
    public void enrich(VariantAnnotation annotation)
    {
        // no transcripts to enrich, abort.
        if (annotation.getTranscriptConsequences() == null)
        {
            return;
        }

        List<TranscriptConsequence> transcripts = new LinkedList<>();

        // search override service for the transcripts
        for (TranscriptConsequence transcript: annotation.getTranscriptConsequences())
        {
            IsoformOverride override = service.getIsoformOverride(source, transcript.getTranscriptId());

            // override the canonical field
            if (override != null)
            // TODO && override.getGeneSymbol().equalsIgnoreCase(transcript.getGeneSymbol())
            {
                transcript.setCanonical("1");
            }
            else
            {
                transcripts.add(transcript);
            }
        }

        // if at least one transcript is overridden as canonical in the previous iteration
        // then mark all other as non-canonical.
        //
        // if no override, then we should leave the transcript list intact
        // (rely on the canonical info provided by the web service in that case).
        if (transcripts.size() < annotation.getTranscriptConsequences().size())
        {
            for (TranscriptConsequence transcript: transcripts)
            {
                transcript.setCanonical(null);
            }
        }
    }
}
