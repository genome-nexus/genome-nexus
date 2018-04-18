package org.cbioportal.genome_nexus.service.remote;

import org.cbioportal.genome_nexus.service.ExternalResourceFetcher;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base abstract implementation of ExternalResourceFetcher.
 * Provides a generic implementation for convenience.
 *
 * @author Selcuk Onur Sumer
 */
public abstract class BaseExternalResourceFetcher<T> implements ExternalResourceFetcher<T>
{
    protected String URI;
    protected String mainQueryParam;
    protected String placeholder;

    public BaseExternalResourceFetcher(String URI, String mainQueryParam, String placeholder)
    {
        this.URI = URI;
        this.mainQueryParam = mainQueryParam;
        this.placeholder = placeholder;
    }

    /**
     * Base implementation for a single parameter query.
     * This method should be overridden if the query has more than one parameter.
     */
    @Override
    public String fetchStringValue(Map<String, String> queryParams)
        throws HttpClientErrorException, ResourceAccessException
    {
        // get the value of the main (single) parameter
        String paramValue = queryParams.get(this.mainQueryParam);
        String uri = this.URI;

        // replace the placeholder with the value in the uri
        if (paramValue != null && paramValue.length() > 0) {
            uri = uri.replace(this.placeholder, paramValue);
        }

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, String.class);
    }

    @Override
    public String fetchStringValue(String param)
        throws HttpClientErrorException, ResourceAccessException
    {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(this.mainQueryParam, param);

        return this.fetchStringValue(queryParams);
    }

    @Override
    public String fetchStringValue(Object requestBody)
        throws HttpClientErrorException, ResourceAccessException
    {
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.postForObject(this.URI, requestBody, String.class);
    }

    @Override
    public List<T> fetchInstances(String param)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(this.mainQueryParam, param);

        return this.fetchInstances(queryParams);
    }
}
