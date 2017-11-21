package org.cbioportal.genome_nexus.service.enricher;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.model.MutationAssessorAnnotation;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.AnnotationEnricher;
import org.cbioportal.genome_nexus.service.MutationAssessorService;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorNotFoundException;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorWebServiceException;

public class MutationAssessorAnnotationEnricher implements AnnotationEnricher
{
    private static final Log LOG = LogFactory.getLog(MutationAssessorAnnotationEnricher.class);

    private MutationAssessorService mutationAssessorService;

    public MutationAssessorAnnotationEnricher(MutationAssessorService mutationAssessorService) {
        this.mutationAssessorService = mutationAssessorService;
    }

    @Override
    public void enrich(VariantAnnotation annotation) {
        if (annotation != null)
        {
            MutationAssessor mutationAssessorObj = null;

            try {
                mutationAssessorObj = mutationAssessorService.getMutationAssessor(annotation);
            } catch (MutationAssessorWebServiceException e) {
                LOG.warn(e.getLocalizedMessage());
            } catch (MutationAssessorNotFoundException e) {
                // fail silently for this variant annotation
            }

            if (mutationAssessorObj != null &&
                mutationAssessorObj.getMappingIssue().length() == 0)
            {
                MutationAssessorAnnotation mutationAnnotation = new MutationAssessorAnnotation();
                mutationAnnotation.setAnnotation(mutationAssessorObj);

                annotation.setDynamicProp("mutation_assessor", mutationAnnotation);
            }
        }
    }

}
