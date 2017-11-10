package org.cbioportal.genome_nexus.web;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.EnrichmentService;
import org.cbioportal.genome_nexus.service.VariantAnnotationService;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
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

    public VariantAnnotation getVariantAnnotation(String variant, EnrichmentService postEnrichmentService)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
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
            try {
                VariantAnnotation annotation = this.getVariantAnnotation(variant, postEnrichmentService);
                variantAnnotations.add(annotation);
            } catch (VariantAnnotationNotFoundException e) {
                e.printStackTrace();
            } catch (VariantAnnotationWebServiceException e) {
                e.printStackTrace();
            }
        }

        return variantAnnotations;
    }

    public VariantAnnotation getVariantAnnotation(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        return this.getVariantAnnotation(variant, null);
    }

    public List<VariantAnnotation> getVariantAnnotations(List<String> variants)
    {
        return this.getVariantAnnotations(variants, null);
    }
}
