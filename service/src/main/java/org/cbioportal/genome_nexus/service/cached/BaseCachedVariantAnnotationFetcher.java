package org.cbioportal.genome_nexus.service.cached;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.persistence.VariantAnnotationRepository;
import org.cbioportal.genome_nexus.service.ExternalResourceFetcher;
import org.cbioportal.genome_nexus.service.ResourceTransformer;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.springframework.web.client.HttpClientErrorException;

import com.google.gson.Gson;
import com.mongodb.DBObject;

public abstract class BaseCachedVariantAnnotationFetcher
    extends BaseCachedExternalResourceFetcher<VariantAnnotation, VariantAnnotationRepository>
{
    private static final Log LOG = LogFactory.getLog(BaseCachedVariantAnnotationFetcher.class);

    protected String collection;
    protected VariantAnnotationRepository repository;
    protected Class<VariantAnnotation> type;
    protected ExternalResourceFetcher<VariantAnnotation> fetcher;
    protected ResourceTransformer<VariantAnnotation> transformer;
    protected Integer maxPageSize;

    public BaseCachedVariantAnnotationFetcher(String collection,
                                             VariantAnnotationRepository repository,
                                             Class<VariantAnnotation> type,
                                             ExternalResourceFetcher<VariantAnnotation> fetcher,
                                             ResourceTransformer<VariantAnnotation> transformer)
    {
        super(collection, repository, type, fetcher, transformer, Integer.MAX_VALUE);
    }

    public BaseCachedVariantAnnotationFetcher(String collection,
                                             VariantAnnotationRepository repository,
                                             Class<VariantAnnotation> type,
                                             ExternalResourceFetcher<VariantAnnotation> fetcher,
                                             ResourceTransformer<VariantAnnotation> transformer,
                                             Integer maxPageSize)
    {
        super(collection, repository, type, fetcher, transformer, maxPageSize);
    }
   
    @Override
    protected String extractId(VariantAnnotation instance)
    {
        // VEP error objects have "input" but no "id" — fall back to variant so the
        // annotation is stored at the correct map key instead of null.
        String id = instance.getVariantId();
        return id != null ? id : instance.getVariant();
    }

    @Override
    protected String extractId(DBObject dbObject)
    {
        return (String)dbObject.get("id");
    }

    @Override 
    public VariantAnnotation fetchAndCache(String id) throws ResourceMappingException
    {
        VariantAnnotation variantAnnotation = null;
        try {
            variantAnnotation = super.fetchAndCache(id);
            if (variantAnnotation == null) {
                variantAnnotation = new VariantAnnotation(id);
            } else {
                variantAnnotation.setSuccessfullyAnnotated(true);
            }
        }
        catch (HttpClientErrorException e) {
            variantAnnotation = new VariantAnnotation(id);
            String errorMessage = "Error from VEP";
            try {
                errorMessage = new Gson().fromJson(e.getResponseBodyAsString(), Map.class).getOrDefault("error", errorMessage).toString();
            } catch (Exception ignored) {
                String body = e.getResponseBodyAsString();
                if (body != null && !body.isEmpty()) {
                    errorMessage = body;
                }
            }
            variantAnnotation.setErrorMessage(errorMessage);
            return variantAnnotation;
        }
        catch (Exception e) {
            return new VariantAnnotation(id);
        }
        return variantAnnotation;
    }
    
    @Override
    public List<VariantAnnotation> fetchAndCache(List<String> ids) throws ResourceMappingException
    {
        Map<String, VariantAnnotation> variantResponse = this.constructFetchedMap(ids); 
        for (String variantId : variantResponse.keySet()) {
            VariantAnnotation annotation = variantResponse.get(variantId);
            if (annotation == null) {
                VariantAnnotation variantAnnotation = new VariantAnnotation(variantId);
                variantAnnotation.setErrorMessage("Error from VEP for: " + variantId);
                variantResponse.put(variantId, variantAnnotation);
            } else if (annotation.getErrorMessage() == null) {
                // Only mark successful when VEP didn't return an error for this variant.
                annotation.setSuccessfullyAnnotated(true);
            }
        }
        // some util function to return response (values) with duplicates
        // need to pass in a list representing indexes of original request
        List<VariantAnnotation> values = new ArrayList();
        for (String id : ids) {
            // Copy constructor only necessary due to constructFetchedMap.
            // Should refactor to use lists instead of maps so duplicate keys can have their own annotation
            values.add(new VariantAnnotation(variantResponse.get(id)));
        } 
        return values;
    }
}
