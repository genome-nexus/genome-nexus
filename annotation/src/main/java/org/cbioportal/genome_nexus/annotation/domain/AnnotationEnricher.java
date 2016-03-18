package org.cbioportal.genome_nexus.annotation.domain;

/**
 * @author Selcuk Onur Sumer
 */
public interface AnnotationEnricher
{
    void enrich(VariantAnnotation annotation);
}
