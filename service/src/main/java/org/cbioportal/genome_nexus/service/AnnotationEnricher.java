package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.VariantAnnotation;

/**
 * @author Selcuk Onur Sumer
 */
public interface AnnotationEnricher
{
    void enrich(VariantAnnotation annotation);
}
