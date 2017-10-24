package org.cbioportal.genome_nexus.service.internal;


import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.model.MutationAssessorAnnotation;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.AnnotationEnricher;

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

            MutationAssessorAnnotation mutationAnnotation = new MutationAssessorAnnotation();

            if (mutationAssessorObj != null && mutationAssessorObj.getMappingIssue().length() == 0)
            {
                mutationAnnotation.setAnnotation(mutationAssessorObj);
                annotation.setDynamicProp("mutation_assessor", mutationAnnotation);
            }
        }
    }

}
