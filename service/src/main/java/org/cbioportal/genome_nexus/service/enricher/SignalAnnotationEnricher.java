package org.cbioportal.genome_nexus.service.enricher;

import org.cbioportal.genome_nexus.model.SignalAnnotation;
import org.cbioportal.genome_nexus.model.SignalMutation;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.AnnotationEnricher;
import org.cbioportal.genome_nexus.service.SignalMutationService;

import java.util.List;

public class SignalAnnotationEnricher implements AnnotationEnricher
{
    private final SignalMutationService signalMutationService;

    public SignalAnnotationEnricher(SignalMutationService signalMutationService) {
        this.signalMutationService = signalMutationService;
    }

    @Override
    public void enrich(VariantAnnotation annotation) {
        SignalAnnotation signalAnnotation = new SignalAnnotation();
        String hgvsg = annotation.getHgvsg();

        if (hgvsg != null) {
            List<SignalMutation> mutations = this.signalMutationService.getSignalMutationsByHgvsg(hgvsg);
            signalAnnotation.setAnnotation(mutations);
        }

        annotation.setSignalAnnotation(signalAnnotation);
    }
}
