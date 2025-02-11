package org.cbioportal.genome_nexus.service.factory;

import org.cbioportal.genome_nexus.service.config.AppConfig;
import org.cbioportal.genome_nexus.service.EnsemblService;
import org.cbioportal.genome_nexus.service.OncokbService;
import org.cbioportal.genome_nexus.service.enricher.IsoformAnnotationEnricher;
import org.springframework.stereotype.Component;

@Component
public class IsoformAnnotationEnricherFactory {
    private final EnsemblService ensemblService;
    private final OncokbService oncokbService;
    private final AppConfig properties;

    public IsoformAnnotationEnricherFactory(
        EnsemblService ensemblService,
        OncokbService oncokbService,
        AppConfig properties
    ) {
        this.ensemblService = ensemblService;
        this.oncokbService = oncokbService;
        this.properties = properties;
    }

    public IsoformAnnotationEnricher create(String isoformOverrideSource) {
        return new IsoformAnnotationEnricher(
            isoformOverrideSource,
            isoformOverrideSource,
            ensemblService,
            oncokbService,
            properties.getPrioritizeOncokbGeneTranscripts()
        );
    }
}