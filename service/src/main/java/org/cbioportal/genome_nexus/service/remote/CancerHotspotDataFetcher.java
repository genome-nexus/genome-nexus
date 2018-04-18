package org.cbioportal.genome_nexus.service.remote;

import org.cbioportal.genome_nexus.model.Hotspot;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class CancerHotspotDataFetcher extends BaseExternalResourceFetcher<Hotspot>
{
    public static final String MAIN_QUERY_PARAM = "variables";

    private final String hotspotsUrl;
    private final ExternalResourceTransformer<Hotspot> transformer;

    @Autowired
    public CancerHotspotDataFetcher(ExternalResourceTransformer<Hotspot> transformer,
                                    @Value("${hotspots.url}") String hotspotsUrl)
    {
        super(hotspotsUrl, MAIN_QUERY_PARAM, null);
        this.hotspotsUrl = hotspotsUrl;
        this.transformer = transformer;
    }

    @Override
    public String fetchStringValue(Map<String, String> queryParams)
        throws HttpClientErrorException, ResourceAccessException
    {
        String variables = queryParams.get(MAIN_QUERY_PARAM);
        String uri = this.getHotspotsUrl();

        if (variables != null && variables.length() > 0)
        {
            // TODO partially hardcoded API URI
            uri += "/byTranscript/" + variables;
        }

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, String.class);
    }

    @Override
    public List<Hotspot> fetchInstances(Map<String, String> queryParams)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchStringValue(queryParams), Hotspot.class);
    }

    @Override
    public List<Hotspot> fetchInstances(Object requestBody)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchStringValue(requestBody), Hotspot.class);
    }

    public String getHotspotsUrl() {
        return hotspotsUrl;
    }

    public ExternalResourceTransformer getTransformer() {
        return transformer;
    }
}
