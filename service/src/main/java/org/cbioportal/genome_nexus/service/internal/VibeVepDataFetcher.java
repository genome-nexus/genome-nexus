package org.cbioportal.genome_nexus.service.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.ExternalResourceFetcher;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

/**
 * Fetches variant annotations from a vibe-vep subprocess instead of the
 * Ensembl VEP REST API. Drop-in replacement for VEPDataFetcher.
 *
 * Parses the comma-separated genomic location strings (e.g. "7,140753336,140753336,A,T")
 * back into GenomicLocation JSON, pipes them to vibe-vep, and returns the
 * VEP-compatible JSON response.
 */
@Component
@Primary
@ConditionalOnProperty(name = "vibe_vep.enabled", havingValue = "true")
public class VibeVepDataFetcher implements ExternalResourceFetcher<VariantAnnotation> {

    private static final Log LOG = LogFactory.getLog(VibeVepDataFetcher.class);

    private final VibeVepProcess vibeVepProcess;
    private final ObjectMapper inputMapper;
    private final ObjectMapper outputMapper;

    public VibeVepDataFetcher(VibeVepProcess vibeVepProcess) {
        this.vibeVepProcess = vibeVepProcess;

        this.inputMapper = new ObjectMapper();

        this.outputMapper = new ObjectMapper();
        this.outputMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        this.outputMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Boolean hasValidURI() {
        return true;
    }

    @Override
    public DBObject fetchRawValue(Map<String, String> queryParams)
            throws HttpClientErrorException, ResourceAccessException {
        String variant = queryParams.get("variant");
        if (variant == null) {
            return new BasicDBList();
        }
        return fetchRawForVariants(List.of(variant));
    }

    @Override
    public DBObject fetchRawValue(String param)
            throws HttpClientErrorException, ResourceAccessException {
        return fetchRawForVariants(List.of(param));
    }

    @Override
    @SuppressWarnings("unchecked")
    public DBObject fetchRawValue(Object requestBody)
            throws HttpClientErrorException, ResourceAccessException {
        // requestBody is a Map with "hgvs_notations" key containing a Set<String>
        // (despite the key name, these are actually comma-separated genomic locations)
        if (requestBody instanceof Map) {
            Map<String, Object> body = (Map<String, Object>) requestBody;
            Object notations = body.get("hgvs_notations");
            if (notations instanceof Set) {
                return fetchRawForVariants(new ArrayList<>((Set<String>) notations));
            }
            if (notations instanceof List) {
                return fetchRawForVariants((List<String>) notations);
            }
        }
        return new BasicDBList();
    }

    @Override
    public List<VariantAnnotation> fetchInstances(Map<String, String> queryParams)
            throws HttpClientErrorException, ResourceAccessException, ResourceMappingException {
        String variant = queryParams.get("variant");
        if (variant == null) {
            return List.of();
        }
        return annotateVariants(List.of(variant));
    }

    @Override
    public List<VariantAnnotation> fetchInstances(String param)
            throws HttpClientErrorException, ResourceAccessException, ResourceMappingException {
        return annotateVariants(List.of(param));
    }

    @Override
    public List<VariantAnnotation> fetchInstances(Object requestBody)
            throws HttpClientErrorException, ResourceAccessException, ResourceMappingException {
        DBObject raw = fetchRawValue(requestBody);
        // The cached fetcher expects fetchInstances(Object) to work with the request body
        // but also calls fetchRawValue separately. Return annotations from raw.
        return parseAnnotations(raw);
    }

    private DBObject fetchRawForVariants(List<String> variantStrings) {
        BasicDBList results = new BasicDBList();
        for (String variantStr : variantStrings) {
            try {
                GenomicLocation gl = parseGenomicLocation(variantStr);
                String glJson = inputMapper.writeValueAsString(gl);
                String resultJson = vibeVepProcess.annotate(glJson);
                DBObject parsed = (DBObject) JSON.parse(resultJson);
                results.add(parsed);
            } catch (Exception e) {
                LOG.warn("Failed to annotate variant: " + variantStr, e);
            }
        }
        return results;
    }

    private List<VariantAnnotation> annotateVariants(List<String> variantStrings) {
        List<VariantAnnotation> results = new ArrayList<>();
        for (String variantStr : variantStrings) {
            try {
                GenomicLocation gl = parseGenomicLocation(variantStr);
                String glJson = inputMapper.writeValueAsString(gl);
                String resultJson = vibeVepProcess.annotate(glJson);
                VariantAnnotation ann = outputMapper.readValue(resultJson, VariantAnnotation.class);
                ann.setSuccessfullyAnnotated(true);
                ann.setAnnotationJSON(resultJson);
                if (ann.getVariant() == null) {
                    ann.setVariant(variantStr);
                }
                results.add(ann);
            } catch (Exception e) {
                LOG.warn("Failed to annotate variant: " + variantStr, e);
                VariantAnnotation errorAnn = new VariantAnnotation(variantStr);
                errorAnn.setErrorMessage("vibe-vep error: " + e.getMessage());
                results.add(errorAnn);
            }
        }
        return results;
    }

    private List<VariantAnnotation> parseAnnotations(DBObject raw) {
        List<VariantAnnotation> results = new ArrayList<>();
        if (raw instanceof BasicDBList) {
            for (Object item : (BasicDBList) raw) {
                try {
                    String json = item.toString();
                    VariantAnnotation ann = outputMapper.readValue(json, VariantAnnotation.class);
                    ann.setSuccessfullyAnnotated(true);
                    results.add(ann);
                } catch (Exception e) {
                    LOG.warn("Failed to parse annotation", e);
                }
            }
        }
        return results;
    }

    /**
     * Parse a comma-separated genomic location string.
     * Format: "chromosome,start,end,referenceAllele,variantAllele"
     * Example: "7,140753336,140753336,A,T"
     */
    private GenomicLocation parseGenomicLocation(String str) {
        String[] parts = str.split(",");
        if (parts.length < 5) {
            throw new IllegalArgumentException("Invalid genomic location: " + str);
        }
        return new GenomicLocation(
            parts[0],                    // chromosome
            Integer.parseInt(parts[1]),  // start
            Integer.parseInt(parts[2]),  // end
            parts[3],                    // referenceAllele
            parts[4]                     // variantAllele
        );
    }
}
