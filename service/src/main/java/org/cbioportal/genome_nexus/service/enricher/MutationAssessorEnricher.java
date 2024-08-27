package org.cbioportal.genome_nexus.service.enricher;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.MutationAssessorService;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorNotFoundException;

public class MutationAssessorEnricher extends BaseAnnotationEnricher
{
    private static final Log LOG = LogFactory.getLog(MutationAssessorEnricher.class);

    private MutationAssessorService mutationAssessorService;

    public MutationAssessorEnricher(
        String id,
        MutationAssessorService mutationAssessorService
    ) {
        super(id);
        this.mutationAssessorService = mutationAssessorService;
    }

    @Override
    public void enrich(VariantAnnotation annotation) {
        if (annotation != null)
        {
            MutationAssessor mutationAssessor = null;

            try {
                mutationAssessor = mutationAssessorService.getMutationAssessor(annotation);
            } catch (MutationAssessorNotFoundException e) {
                LOG.warn(e.getLocalizedMessage());
            }

            if (mutationAssessor != null)
            {
                annotation.setMutationAssessor(mutationAssessor);
            }
        }
    }

}
