package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorNotFoundException;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorWebServiceException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;

import java.util.List;

public interface MutationAssessorService
{
    MutationAssessor getMutationAssessorFromEnrichedVariantAnnotation(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException;
    List<MutationAssessor> getMutationAssessorFromEnrichedVariantAnnotation(List<String> variants);

    MutationAssessor getMutationAssessor(VariantAnnotation annotation)
        throws MutationAssessorNotFoundException, MutationAssessorWebServiceException;
    MutationAssessor getMutationAssessor(String variant, String hgvs)
        throws MutationAssessorNotFoundException, MutationAssessorWebServiceException;
}
