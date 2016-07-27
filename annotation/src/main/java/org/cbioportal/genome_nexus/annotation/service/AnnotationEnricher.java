package org.cbioportal.genome_nexus.annotation.service;

import org.cbioportal.genome_nexus.annotation.domain.VariantAnnotation;

/**
 * @author Selcuk Onur Sumer
 */
public interface AnnotationEnricher
{
    void enrich(VariantAnnotation annotation);
}
