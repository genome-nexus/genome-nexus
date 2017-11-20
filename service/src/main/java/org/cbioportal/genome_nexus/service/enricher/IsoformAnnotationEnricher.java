package org.cbioportal.genome_nexus.service.enricher;

import org.cbioportal.genome_nexus.service.AnnotationEnricher;
import org.cbioportal.genome_nexus.model.IsoformOverride;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.IsoformOverrideService;
import org.cbioportal.genome_nexus.service.exception.IsoformOverrideNotFoundException;

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

        /* This logic handles the case when there are more than one potential genes that a variant could be affecting.
            When two genes are extremely close in the genome/overlapping, VEP will return the transcripts for both genes.
            In the case where we only have an override for one of the genes, we do not want to force those transcripts as canonical
            if the most severe consequence does not exist in that gene, because more likely the correct transcript that the user
            cares about exists in the other gene.

            We only apply isoform overrides if the most severe consequence exists in the gene we want to apply to override to.
            If it does not, do not touch the canonical fields for those transcripts.
        */
        String geneWithMostSevereConsequence = "";
        String mostSevereConsequence = annotation.getMostSevereConsequence();
        for (TranscriptConsequence transcript : annotation.getTranscriptConsequences()) {
            if (transcript.getConsequenceTerms().contains(mostSevereConsequence)) {
                geneWithMostSevereConsequence = transcript.getGeneSymbol();
                break;
            }
        }

        // search override service for the transcripts
        for (TranscriptConsequence transcript: annotation.getTranscriptConsequences())
        {
            IsoformOverride override = null;

            try {
                override = service.getIsoformOverride(source, transcript.getTranscriptId());
            } catch (IsoformOverrideNotFoundException e) {
                // fail silently for this transcript
            }

            // override the canonical field
            if (override != null && transcript.getGeneSymbol().equals(geneWithMostSevereConsequence))
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
