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
     * Parse a variant string into a GenomicLocation.
     * Supports two formats:
     *   - Comma-separated: "7,140753336,140753336,A,T"
     *   - HGVS-like: "7:g.140753336A>T" or "12:g.25245351C>A"
     */
    private GenomicLocation parseGenomicLocation(String str) {
        // Try comma-separated first.
        if (str.contains(",")) {
            String[] parts = str.split(",");
            if (parts.length >= 5) {
                return new GenomicLocation(
                    parts[0],
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    parts[3],
                    parts[4]
                );
            }
        }

        // Try HGVS genomic notation: "7:g.140753336A>T"
        if (str.contains(":g.")) {
            return parseHgvsGenomic(str);
        }

        throw new IllegalArgumentException("Cannot parse variant: " + str);
    }

    /**
     * Parse HGVS genomic notation into a GenomicLocation.
     * Examples: "7:g.140753336A>T", "12:g.25245351C>A", "1:g.65325832_65325833insG", "3:g.114058003del"
     */
    private GenomicLocation parseHgvsGenomic(String hgvs) {
        int colonIdx = hgvs.indexOf(':');
        if (colonIdx < 0) {
            throw new IllegalArgumentException("Invalid HGVS: " + hgvs);
        }
        String chrom = hgvs.substring(0, colonIdx);
        String rest = hgvs.substring(colonIdx + 1);

        // Strip "g." prefix
        if (rest.startsWith("g.")) {
            rest = rest.substring(2);
        }

        // Handle different mutation types (check delins before del since del is a substring)
        if (rest.contains(">")) {
            // SNV: 140753336A>T
            int snvIdx = -1;
            for (int i = 0; i < rest.length(); i++) {
                if (!Character.isDigit(rest.charAt(i))) {
                    snvIdx = i;
                    break;
                }
            }
            if (snvIdx < 0) {
                throw new IllegalArgumentException("Invalid HGVS SNV: " + hgvs);
            }
            int pos = Integer.parseInt(rest.substring(0, snvIdx));
            String alleles = rest.substring(snvIdx); // "A>T"
            int gtIdx = alleles.indexOf('>');
            String ref = alleles.substring(0, gtIdx);
            String alt = alleles.substring(gtIdx + 1);
            return new GenomicLocation(chrom, pos, pos, ref, alt);
        } else if (rest.contains("ins")) {
            // Insertion: 65325832_65325833insG
            int insIdx = rest.indexOf("ins");
            String posPart = rest.substring(0, insIdx);
            String alt = rest.substring(insIdx + 3);
            int start, end;
            if (posPart.contains("_")) {
                String[] positions = posPart.split("_");
                start = Integer.parseInt(positions[0]);
                end = Integer.parseInt(positions[1]);
            } else {
                start = Integer.parseInt(posPart);
                end = start + 1;
            }
            return new GenomicLocation(chrom, start, end, "-", alt);
        } else if (rest.contains("delins")) {
            // Delins: 9057113_9057114delinsCTG
            int delinsIdx = rest.indexOf("delins");
            String posPart = rest.substring(0, delinsIdx);
            String alt = rest.substring(delinsIdx + 6);
            int start, end;
            if (posPart.contains("_")) {
                String[] positions = posPart.split("_");
                start = Integer.parseInt(positions[0]);
                end = Integer.parseInt(positions[1]);
            } else {
                start = Integer.parseInt(posPart);
                end = start;
            }
            return new GenomicLocation(chrom, start, end, "-", alt);
        } else if (rest.contains("del")) {
            // Deletion: 114058003del or 114058003_114058005del
            int delIdx = rest.indexOf("del");
            String posPart = rest.substring(0, delIdx);
            String deleted = rest.substring(delIdx + 3); // may be empty or contain deleted bases
            int start, end;
            if (posPart.contains("_")) {
                String[] positions = posPart.split("_");
                start = Integer.parseInt(positions[0]);
                end = Integer.parseInt(positions[1]);
            } else {
                start = Integer.parseInt(posPart);
                end = start;
            }
            String ref = deleted.isEmpty() ? "-" : deleted;
            return new GenomicLocation(chrom, start, end, ref, "-");
        }

        throw new IllegalArgumentException("Unrecognized HGVS pattern: " + hgvs);
    }
}
