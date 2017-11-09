package org.cbioportal.genome_nexus.service.remote;

import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.service.ExternalResourceFetcher;
import org.cbioportal.genome_nexus.service.internal.ExternalResourceTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MutationAssessorDataFetcher implements ExternalResourceFetcher<MutationAssessor>
{
    @Value("${mutationAssessor.url}")
    private String mutationAssessorUrl;

    private final ExternalResourceTransformer externalResourceTransformer;

    @Autowired
    public MutationAssessorDataFetcher(ExternalResourceTransformer externalResourceTransformer)
    {
        this.externalResourceTransformer = externalResourceTransformer;
    }

    @Override
    public String fetchJsonString(Map<String, String> queryParams)
        throws HttpClientErrorException, ResourceAccessException
    {
        String variant = queryParams.get("variant");
        String uri = this.mutationAssessorUrl;

        if (variant != null && variant.length() > 0) {
            uri = uri.replace("VARIANT", variant);
        }

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, String.class);
    }

    @Override
    public String fetchJsonString(String variant)
        throws HttpClientErrorException, ResourceAccessException
    {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("variant", variant);

        return this.fetchJsonString(queryParams);
    }

    @Override
    public List<MutationAssessor> fetchInstances(Map<String, String> queryParams)
        throws IOException, HttpClientErrorException, ResourceAccessException
    {
        return this.externalResourceTransformer.transform(this.fetchJsonString(queryParams), MutationAssessor.class);
    }

    @Override
    public List<MutationAssessor> fetchInstances(String variant)
        throws IOException, HttpClientErrorException, ResourceAccessException
    {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("variant", variant);

        return this.fetchInstances(queryParams);
    }

    public String getMutationAssessorUrl() {
        return mutationAssessorUrl;
    }

    public void setMutationAssessorUrl(String mutationAssessorUrl) {
        this.mutationAssessorUrl = mutationAssessorUrl;
    }
}
