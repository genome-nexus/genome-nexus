package org.cbioportal.genome_nexus.annotation.web;

import org.cbioportal.genome_nexus.annotation.domain.VariantAnnotation;
import org.cbioportal.genome_nexus.annotation.service.EnrichmentService;
import org.cbioportal.genome_nexus.annotation.service.VariantAnnotationService;

import java.util.ArrayList;
import java.util.List;

public class Annotator
{

    public static VariantAnnotation getVariantAnnotation(String variant,
                                                         VariantAnnotationService variantAnnotationService,
                                                         EnrichmentService postEnrichmentService)
    {
        VariantAnnotation annotation = variantAnnotationService.getAnnotation(variant);

        if (annotation != null &&
            postEnrichmentService != null)
        {
            postEnrichmentService.enrichAnnotation(annotation);
        }

        return annotation;
    }

    public static List<VariantAnnotation> getVariantAnnotations(List<String> variants,
                                                                VariantAnnotationService variantAnnotationService,
                                                                EnrichmentService postEnrichmentService)
    {
        List<VariantAnnotation> variantAnnotations = new ArrayList<>();

        for (String variant: variants)
        {
            VariantAnnotation annotation = getVariantAnnotation(
                variant, variantAnnotationService, postEnrichmentService);

            if (annotation != null)
            {
                variantAnnotations.add(annotation);
            }
        }

        return variantAnnotations;
    }

    public static List<VariantAnnotation> getVariantAnnotations(List<String> variants,
                                                                VariantAnnotationService variantAnnotationService)
    {
        return getVariantAnnotations(variants, variantAnnotationService, null);
    }
}
