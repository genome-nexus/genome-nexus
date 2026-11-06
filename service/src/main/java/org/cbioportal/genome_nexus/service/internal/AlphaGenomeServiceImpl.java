package org.cbioportal.genome_nexus.service.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.component.annotation.AlphaGenomeVariantNormalizer;
import org.cbioportal.genome_nexus.component.annotation.NormalizedAlphaGenomeVariant;
import org.cbioportal.genome_nexus.model.AlphaGenome;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.AlphaGenomeService;
import org.cbioportal.genome_nexus.service.cached.CachedAlphaGenomeFetcher;
import org.cbioportal.genome_nexus.service.exception.AlphaGenomeNotFoundException;
import org.cbioportal.genome_nexus.service.exception.AlphaGenomeWebServiceException;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.util.AlphaGenomePipelineDebugLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Normalizes, then returns cached AlphaGenome annotation */
@Service
public class AlphaGenomeServiceImpl implements AlphaGenomeService
{
    private static final Log LOG = LogFactory.getLog(AlphaGenomeServiceImpl.class);

    private final CachedAlphaGenomeFetcher cachedAlphaGenomeFetcher;
    private final AlphaGenomeVariantNormalizer normalizer;

    @Autowired
    public AlphaGenomeServiceImpl(CachedAlphaGenomeFetcher cachedAlphaGenomeFetcher,
                                  AlphaGenomeVariantNormalizer normalizer)
    {
        this.cachedAlphaGenomeFetcher = cachedAlphaGenomeFetcher;
        this.normalizer = normalizer;
    }

    @Override
    public AlphaGenome getAlphaGenomeByAnnotation(VariantAnnotation annotation)
        throws AlphaGenomeNotFoundException, AlphaGenomeWebServiceException
    {
        if (annotation == null) {
            return null;
        }
        String varId = annotation.getVariant() != null ? annotation.getVariant() : annotation.getOriginalVariantQuery();
        AlphaGenomePipelineDebugLogger.log("PHASE 3: QUERY TO ALPHAGENOME INPUT",
            String.format("Processing single variant '%s'", varId));

        Optional<NormalizedAlphaGenomeVariant> normalized = normalizer.normalize(annotation);
        if (!normalized.isPresent()) {
            AlphaGenomePipelineDebugLogger.log("PHASE 3: QUERY TO ALPHAGENOME INPUT",
                String.format("Variant '%s' was rejected or not normalized. Skipping sidecar query.", varId));
            return null;
        }
        Map<String, Object> body = buildRequestBody(annotation.getVariant(), normalized.get());
        AlphaGenomePipelineDebugLogger.log("PHASE 3: QUERY TO ALPHAGENOME INPUT",
            String.format("Variant '%s' normalized. Staging payload for sidecar: %s", varId, body.toString()));

        Map<String, Map<String, Object>> bodies = new LinkedHashMap<>();
        bodies.put(annotation.getVariant(), body);
        try {
            AlphaGenomePipelineDebugLogger.log("PHASE 3: QUERY TO ALPHAGENOME INPUT",
                String.format("Invoking cachedAlphaGenomeFetcher.fetchAndCache for variant '%s'", varId));
            List<AlphaGenome> results = cachedAlphaGenomeFetcher.fetchAndCache(bodies);
            if (results.isEmpty()) {
                AlphaGenomePipelineDebugLogger.log("PHASE 5: RESPONSE FROM ALPHAGENOME SIDECAR",
                    String.format("No AlphaGenome result returned for variant '%s'", varId));
                throw new AlphaGenomeNotFoundException(annotation.getVariant());
            }
            AlphaGenome alphaGenome = results.get(0);
            AlphaGenomePipelineDebugLogger.log("PHASE 5: RESPONSE FROM ALPHAGENOME SIDECAR",
                String.format("Successfully resolved AlphaGenome for '%s': hg38='%s', model='%s', scores count=%d, merged splicing=%s",
                    varId, alphaGenome.getHg38Variant(), alphaGenome.getModelVersion(),
                    alphaGenome.getScores() != null ? alphaGenome.getScores().size() : 0,
                    alphaGenome.getMergedSplicingScore()));
            return alphaGenome;
        } catch (ResourceMappingException e) {
            AlphaGenomePipelineDebugLogger.log("PHASE 5: RESPONSE ERROR",
                String.format("ResourceMappingException for '%s': %s", varId, e.getMessage()));
            throw new AlphaGenomeWebServiceException(e.getMessage());
        } catch (HttpClientErrorException e) {
            AlphaGenomePipelineDebugLogger.log("PHASE 5: RESPONSE ERROR",
                String.format("HttpClientErrorException for '%s': status=%s, body=%s",
                    varId, e.getStatusCode(), e.getResponseBodyAsString()));
            throw new AlphaGenomeWebServiceException(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (ResourceAccessException e) {
            AlphaGenomePipelineDebugLogger.log("PHASE 5: RESPONSE ERROR",
                String.format("ResourceAccessException for '%s': %s", varId, e.getMessage()));
            throw new AlphaGenomeWebServiceException(e.getMessage());
        }
    }

    @Override
    public List<AlphaGenome> getAlphaGenomeByAnnotation(List<VariantAnnotation> annotations)
        throws AlphaGenomeWebServiceException
    {
        Map<String, Map<String, Object>> bodies = new LinkedHashMap<>();
        for (VariantAnnotation annotation : annotations) {
            if (annotation == null) {
                continue;
            }
            String varId = annotation.getVariant() != null ? annotation.getVariant() : annotation.getOriginalVariantQuery();
            Optional<NormalizedAlphaGenomeVariant> normalized = normalizer.normalize(annotation);
            if (normalized.isPresent()) {
                Map<String, Object> body = buildRequestBody(annotation.getVariant(), normalized.get());
                bodies.put(annotation.getVariant(), body);
                AlphaGenomePipelineDebugLogger.log("PHASE 3: QUERY TO ALPHAGENOME INPUT",
                    String.format("Batch item '%s' normalized. Staging payload: %s", varId, body.toString()));
            } else {
                AlphaGenomePipelineDebugLogger.log("PHASE 3: QUERY TO ALPHAGENOME INPUT",
                    String.format("Batch item '%s' rejected during normalization. Not staged for sidecar.", varId));
            }
        }
        if (bodies.isEmpty()) {
            AlphaGenomePipelineDebugLogger.log("PHASE 3: QUERY TO ALPHAGENOME INPUT",
                "Batch query: no variants passed normalization. Returning empty list.");
            return Collections.emptyList();
        }
        try {
            AlphaGenomePipelineDebugLogger.log("PHASE 3: QUERY TO ALPHAGENOME INPUT",
                String.format("Batch query: dispatching %d variants to cachedAlphaGenomeFetcher", bodies.size()));
            List<AlphaGenome> results = cachedAlphaGenomeFetcher.fetchAndCache(bodies);
            AlphaGenomePipelineDebugLogger.log("PHASE 5: RESPONSE FROM ALPHAGENOME SIDECAR",
                String.format("Batch query returned %d AlphaGenome results", results.size()));
            return results;
        } catch (ResourceMappingException e) {
            AlphaGenomePipelineDebugLogger.log("PHASE 5: RESPONSE ERROR",
                String.format("Batch ResourceMappingException: %s", e.getMessage()));
            throw new AlphaGenomeWebServiceException(e.getMessage());
        } catch (HttpClientErrorException e) {
            AlphaGenomePipelineDebugLogger.log("PHASE 5: RESPONSE ERROR",
                String.format("Batch HttpClientErrorException: status=%s, body=%s",
                    e.getStatusCode(), e.getResponseBodyAsString()));
            throw new AlphaGenomeWebServiceException(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (ResourceAccessException e) {
            AlphaGenomePipelineDebugLogger.log("PHASE 5: RESPONSE ERROR",
                String.format("Batch ResourceAccessException: %s", e.getMessage()));
            throw new AlphaGenomeWebServiceException(e.getMessage());
        }
    }

    private Map<String, Object> buildRequestBody(String originalVariant,
                                                   NormalizedAlphaGenomeVariant normalized)
    {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("variant", originalVariant);
        body.put("chromosome", normalized.getChrom());
        body.put("position", normalized.getPos());
        body.put("reference_allele", normalized.getRef());
        body.put("alternate_allele", normalized.getAlt());
        body.put("canonical_gene_id", normalized.getGeneId());
        body.put("canonical_gene", normalized.getGeneSymbol());
        return body;
    }
}
