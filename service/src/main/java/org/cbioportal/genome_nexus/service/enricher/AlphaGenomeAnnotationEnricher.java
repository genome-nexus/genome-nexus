package org.cbioportal.genome_nexus.service.enricher;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.AlphaGenome;
import org.cbioportal.genome_nexus.model.AlphaGenomeAnnotation;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.AlphaGenomeService;
import org.cbioportal.genome_nexus.service.exception.AlphaGenomeNotFoundException;
import org.cbioportal.genome_nexus.service.exception.AlphaGenomeWebServiceException;
import org.cbioportal.genome_nexus.util.AlphaGenomePipelineDebugLogger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AlphaGenomeAnnotationEnricher extends BaseAnnotationEnricher
{
    private static final Log LOG = LogFactory.getLog(AlphaGenomeAnnotationEnricher.class);

    /**
     * AlphaGenome model/SDK provenance URL
     */
    public static final String LICENSE = "https://github.com/google-deepmind/alphagenome";

    private AlphaGenomeService alphaGenomeService;

    public AlphaGenomeAnnotationEnricher(
        String id,
        AlphaGenomeService alphaGenomeService
    ) {
        super(id);
        this.alphaGenomeService = alphaGenomeService;
    }

    @Override
    public void enrich(VariantAnnotation annotation) {
        if (annotation != null)
        {
            String varId = annotation.getVariant() != null ? annotation.getVariant() : annotation.getOriginalVariantQuery();
            AlphaGenomePipelineDebugLogger.log("PHASE 6: ENRICHER",
                String.format("Starting enrichment for variant '%s'", varId));

            AlphaGenome alphaGenome = null;

            try {
                alphaGenome = alphaGenomeService.getAlphaGenomeByAnnotation(annotation);
            } catch (AlphaGenomeWebServiceException e) {
                AlphaGenomePipelineDebugLogger.log("PHASE 6: ENRICHER ERROR",
                    String.format("AlphaGenomeWebServiceException for '%s': %s", varId, e.getMessage()));
                LOG.warn(e.getMessage());
            } catch (AlphaGenomeNotFoundException e) {
                AlphaGenomePipelineDebugLogger.log("PHASE 6: ENRICHER",
                    String.format("AlphaGenomeNotFoundException for '%s' (not found / not scored)", varId));
            }

            this.enrichAnnotationWithAlphaGenome(annotation, alphaGenome);
        }
    }

    @Override
    public void enrich(List<VariantAnnotation> annotations) {
        AlphaGenomePipelineDebugLogger.log("PHASE 6: ENRICHER",
            String.format("Starting batch enrichment for %d variants", annotations.size()));
        try {
            List<AlphaGenome> alphaGenomes = alphaGenomeService.getAlphaGenomeByAnnotation(annotations);
            Map<String, AlphaGenome> variantToAlphaGenomeMap =
                alphaGenomes.stream().collect(Collectors.toMap(AlphaGenome::getVariant, v -> v));

            for (VariantAnnotation annotation: annotations) {
                this.enrichAnnotationWithAlphaGenome(annotation,
                    variantToAlphaGenomeMap.get(annotation.getVariant()));
            }
        } catch (AlphaGenomeWebServiceException e) {
            AlphaGenomePipelineDebugLogger.log("PHASE 6: ENRICHER ERROR",
                String.format("Batch AlphaGenomeWebServiceException: %s", e.getMessage()));
            LOG.warn(e.getMessage());
        }
    }

    private void enrichAnnotationWithAlphaGenome(
        VariantAnnotation annotation,
        AlphaGenome alphaGenome
    ) {
        String varId = annotation.getVariant() != null ? annotation.getVariant() : annotation.getOriginalVariantQuery();
        if (alphaGenome != null)
        {
            AlphaGenomeAnnotation alphaGenomeAnnotation = new AlphaGenomeAnnotation();
            alphaGenomeAnnotation.setLicense(LICENSE);
            alphaGenomeAnnotation.setAnnotation(alphaGenome);

            annotation.setAlphaGenomeAnnotation(alphaGenomeAnnotation);
            AlphaGenomePipelineDebugLogger.log("PHASE 6: ENRICHER SUCCESS",
                String.format("Variant '%s' successfully enriched with AlphaGenomeAnnotation (hg38='%s', model='%s', scores count=%d)",
                    varId, alphaGenome.getHg38Variant(), alphaGenome.getModelVersion(),
                    alphaGenome.getScores() != null ? alphaGenome.getScores().size() : 0));
        } else {
            AlphaGenomePipelineDebugLogger.log("PHASE 6: ENRICHER RESULT",
                String.format("Variant '%s' has NO AlphaGenome data (alphaGenome == null). alpha_genome field will be null/omitted.", varId));
        }
    }
}
