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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


@Component
public class UniprotDataFetcher extends BaseExternalResourceFetcher<ProteinFeatureInfo> {
    private static final String MAIN_QUERY_PARAM = "accession";
    private static final String PLACEHOLDER = "ACCESSION";

    private final ExternalResourceTransformer<ProteinFeatureInfo> transformer;


    @Autowired
    public UniprotDataFetcher(ExternalResourceTransformer<ProteinFeatureInfo> transformer,
                                       @Value("${uniprot.ptm.url:https://www.ebi.ac.uk/proteins/api/features/ACCESSION?categories=PTM}") String uniprotPtmUrl)
    {
        super(uniprotPtmUrl, MAIN_QUERY_PARAM, PLACEHOLDER);
        this.transformer = transformer;
    }

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

    /**
     * By default assuming that response is a List. For all other response types
     * this method should be overridden.
     */
    @Override
    protected DBObject getForObject(String uri, Map<String, String> queryParams) {
        // RestTemplate restTemplate = new RestTemplate();
        // HttpEntity<String> entity = new HttpEntity<String>(queryParams.toString());

        // ResponseEntity<BasicDBObject> response = restTemplate.exchange(
        //     uri, HttpMethod.GET, entity, BasicDBObject.class);
        // return response.getBody();
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(uri, BasicDBObject.class);
    }

    @Override
    protected DBObject postForObject(String uri, Object requestBody)
    {
        // HttpHeaders httpHeaders = new HttpHeaders();
        // httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);      

        // RestTemplate restTemplate = new RestTemplate();
        // HttpEntity<Object> request = new HttpEntity<>(requestBody, httpHeaders);

        // return restTemplate.postForObject(uri, request, BasicDBList.class);
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