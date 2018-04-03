package org.cbioportal.genome_nexus.service.enricher;

import org.cbioportal.genome_nexus.model.VariantAnnotationSummary;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.AnnotationEnricher;
import org.cbioportal.genome_nexus.service.VariantAnnotationSummaryService;


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
            this.variantAnnotationSummaryService.getAnnotationSummaryForCanonical(annotation);

        annotation.setAnnotationSummary(annotationSummary);
    }
}
