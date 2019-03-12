package org.cbioportal.genome_nexus.service.enricher;

import org.cbioportal.genome_nexus.model.VariantAnnotationSummary;
import org.cbioportal.genome_nexus.model.TranscriptConsequenceSummary;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.AnnotationEnricher;
import org.cbioportal.genome_nexus.service.VariantAnnotationSummaryService;

import java.util.ArrayList;
import java.util.List;


public class CanonicalTranscriptAnnotationEnricher implements AnnotationEnricher
{
    private final VariantAnnotationSummaryService variantAnnotationSummaryService;

    public CanonicalTranscriptAnnotationEnricher(VariantAnnotationSummaryService variantAnnotationSummaryService)
    {
        this.variantAnnotationSummaryService = variantAnnotationSummaryService;
    }

    @Override
    public void enrich(VariantAnnotation annotation)
    {
        VariantAnnotationSummary annotationSummary =
            this.variantAnnotationSummaryService.getAnnotationSummary(annotation);
        // For backwards compatibility transcriptConsequences should be a list
        // of one when used as enrichment
        List<TranscriptConsequenceSummary> transcriptConsequences = new ArrayList<>(1);
        if (annotationSummary.getTranscriptConsequenceSummary() != null) {
            transcriptConsequences.add(annotationSummary.getTranscriptConsequenceSummary());
        }
        annotationSummary.setTranscriptConsequences(transcriptConsequences);

        annotation.setAnnotationSummary(annotationSummary);
    }
}
