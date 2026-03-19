package org.cbioportal.genome_nexus.service.internal;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/**
 * Variant annotation service that uses vibe-vep as a subprocess
 * instead of the Ensembl VEP REST API.
 *
 * Activated when the property vibe_vep.enabled=true is set.
 *
 * The vibe-vep subprocess produces VEP-compatible JSON output,
 * so the response maps naturally into the existing VariantAnnotation
 * and TranscriptConsequence model classes.
 */
@Service
@ConditionalOnProperty(name = "vibe_vep.enabled", havingValue = "true")
public class VibeVepVariantAnnotationService {

    private static final Log LOG = LogFactory.getLog(VibeVepVariantAnnotationService.class);

    private final VibeVepProcess vibeVepProcess;
    private final ObjectMapper inputMapper;
    private final ObjectMapper outputMapper;

    public VibeVepVariantAnnotationService(VibeVepProcess vibeVepProcess) {
        this.vibeVepProcess = vibeVepProcess;

        // Input mapper: serialize GenomicLocation to JSON for vibe-vep stdin
        this.inputMapper = new ObjectMapper();
        this.inputMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Output mapper: deserialize VEP-compatible JSON (snake_case) into VariantAnnotation
        this.outputMapper = new ObjectMapper();
        this.outputMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        this.outputMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Annotate a single genomic location using the vibe-vep subprocess.
     *
     * @param genomicLocation the genomic location to annotate
     * @return the variant annotation parsed from vibe-vep output
     * @throws IOException if communication with the subprocess fails
     */
    public VariantAnnotation annotate(GenomicLocation genomicLocation) throws IOException {
        String inputJson = inputMapper.writeValueAsString(genomicLocation);

        LOG.debug("Sending to vibe-vep: " + inputJson);

        String outputJson = vibeVepProcess.annotate(inputJson);

        LOG.debug("Received from vibe-vep: " + outputJson);

        VariantAnnotation annotation = outputMapper.readValue(outputJson, VariantAnnotation.class);

        // Store the raw JSON and mark as successfully annotated
        annotation.setAnnotationJSON(outputJson);
        annotation.setSuccessfullyAnnotated(true);

        // Set the variant identifier from the input field if available
        if (annotation.getVariant() == null) {
            annotation.setVariant(genomicLocation.toString());
        }

        return annotation;
    }
}
