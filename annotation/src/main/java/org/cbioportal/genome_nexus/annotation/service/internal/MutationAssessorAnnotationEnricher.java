package org.cbioportal.genome_nexus.annotation.service.internal;


import org.cbioportal.genome_nexus.annotation.domain.MutationAssessor;
import org.cbioportal.genome_nexus.annotation.domain.VariantAnnotation;
import org.cbioportal.genome_nexus.annotation.service.AnnotationEnricher;

public class MutationAssessorAnnotationEnricher implements AnnotationEnricher {

    private MutationAssessorService mutationAssessorService;

    public MutationAssessorAnnotationEnricher(MutationAssessorService mutationAssessorService) {
        this.mutationAssessorService = mutationAssessorService;
    }

    @Override
    public void enrich(VariantAnnotation annotation) {
        if (annotation != null)
        {
            MutationAssessor mutationAssessorObj =
                mutationAssessorService.getMutationAssessor(annotation);

            if (mutationAssessorObj != null)
            {
                annotation.setDynamicProp("functionalImpact", mutationAssessorObj.getFunctionalImpact());
                annotation.setDynamicProp("functionalImpactScore", mutationAssessorObj.getFunctionalImpactScore());
            }
        }
    }

}
