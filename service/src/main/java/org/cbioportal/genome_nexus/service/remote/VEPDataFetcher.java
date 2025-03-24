package org.cbioportal.genome_nexus.service.remote;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import java.util.List;
import java.util.Map;

@Component
public class VEPDataFetcher extends BaseExternalResourceFetcher<VariantAnnotation>
{
    private static final String MAIN_QUERY_PARAM = "variant";
    private static final String PLACEHOLDER = "VARIANT";

    private final ExternalResourceTransformer<VariantAnnotation> transformer;

    @Autowired
    public VEPDataFetcher(ExternalResourceTransformer<VariantAnnotation> externalResourceTransformer,
                          @Value("${vep.url}") String vepUrl, 
                          RestTemplate restTemplate)
    {
        super(vepUrl, MAIN_QUERY_PARAM, PLACEHOLDER, restTemplate);
        this.transformer = externalResourceTransformer;
    }

    @Override
    protected DBObject postForObject(String uri, Object requestBody)
    {
        uri = uri.replace("/" + PLACEHOLDER, "");

        return restTemplate.postForObject(uri, requestBody, BasicDBList.class);
    }

    @Override
    public List<VariantAnnotation> fetchInstances(Map<String, String> queryParams)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchRawValue(queryParams), VariantAnnotation.class);
    }

    @Override
    public List<VariantAnnotation> fetchInstances(Object requestBody)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchRawValue(requestBody), VariantAnnotation.class);
    }

    public ExternalResourceTransformer getTransformer() {
        return transformer;
    }
}
