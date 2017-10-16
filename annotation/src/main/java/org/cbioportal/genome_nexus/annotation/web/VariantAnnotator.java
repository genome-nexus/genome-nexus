package org.cbioportal.genome_nexus.annotation.web;

import org.cbioportal.genome_nexus.annotation.domain.VariantAnnotation;
import org.cbioportal.genome_nexus.annotation.service.EnrichmentService;
import org.cbioportal.genome_nexus.annotation.service.VariantAnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class VariantAnnotator
{
    private final VariantAnnotationService variantAnnotationService;

    @Autowired
    public VariantAnnotator(VariantAnnotationService variantAnnotationService)
    {
        this.variantAnnotationService = variantAnnotationService;
    }

    public VariantAnnotation getVariantAnnotation(String variant,
                                                  EnrichmentService postEnrichmentService)
    {
        VariantAnnotation annotation = this.variantAnnotationService.getAnnotation(variant);

        if (annotation != null &&
            postEnrichmentService != null)
        {
            postEnrichmentService.enrichAnnotation(annotation);
        }

        return annotation;
    }

    public List<VariantAnnotation> getVariantAnnotations(List<String> variants,
                                                         EnrichmentService postEnrichmentService)
    {
        List<VariantAnnotation> variantAnnotations = new ArrayList<>();

        for (String variant: variants)
        {
            VariantAnnotation annotation = this.getVariantAnnotation(
                variant, postEnrichmentService);

            if (annotation != null)
            {
                variantAnnotations.add(annotation);
            }
        }

        return variantAnnotations;
    }

    public VariantAnnotation getVariantAnnotation(String variant)
    {
        return this.getVariantAnnotation(variant, null);
    }

    public List<VariantAnnotation> getVariantAnnotations(List<String> variants)
    {
        return this.getVariantAnnotations(variants, null);
    }
}
