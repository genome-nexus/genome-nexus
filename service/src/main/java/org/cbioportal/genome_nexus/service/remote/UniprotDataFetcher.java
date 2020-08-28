package org.cbioportal.genome_nexus.service.remote;

import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.cbioportal.genome_nexus.model.uniprot.ProteinFeatureInfo;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class UniprotDataFetcher extends BaseExternalResourceFetcher<ProteinFeatureInfo> {
    private static final String MAIN_QUERY_PARAM = "accession";
    private static final String PLACEHOLDER = "ACCESSION";

    private final ExternalResourceTransformer<ProteinFeatureInfo> transformer;

    @Autowired
    public UniprotDataFetcher(ExternalResourceTransformer<ProteinFeatureInfo> transformer,
                                       @Value("${uniprot.features.url:https://www.ebi.ac.uk/proteins/api/features/ACCESSION}") String uniprotFeaturesUrl)
    {
        super(uniprotFeaturesUrl, MAIN_QUERY_PARAM, PLACEHOLDER);
        this.transformer = transformer;
    }

    // TODO: Functions below are not been used for now, will be useful when we have Uniprot data in database

    @Override
    public List<ProteinFeatureInfo> fetchInstances(Map<String, String> queryParams)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchRawValue(queryParams), ProteinFeatureInfo.class);
    }

    @Override
    public List<ProteinFeatureInfo> fetchInstances(Object requestBody)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchRawValue(requestBody), ProteinFeatureInfo.class);
    }

    @Override
    protected DBObject getForObject(String uri, Map<String, String> queryParams) {
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(uri, BasicDBObject.class);
    }

    @Override
    protected DBObject postForObject(String uri, Object requestBody)
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Object> request = new HttpEntity<>(requestBody, httpHeaders);

        return restTemplate.postForObject(uri, request, BasicDBList.class);
    }

    public ExternalResourceTransformer getTransformer() {
        return transformer;
    }
}