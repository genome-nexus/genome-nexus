package org.cbioportal.genome_nexus.service.remote;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.cbioportal.genome_nexus.model.AlphaGenome;
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

/**
 * POSTs one normalized variant per call to the AlphaGenome sidecar
 * ({@code POST /score}) and reads back the single {@code alpha_genome} JSON
 * object. Unlike the batched MyVariantInfo fetcher, the sidecar takes a
 * single JSON body and returns a single JSON object.
 */
@Component
public class AlphaGenomeDataFetcher extends BaseExternalResourceFetcher<AlphaGenome>
{
    public static final String MAIN_QUERY_PARAM = "variant";
    public static final String PLACEHOLDER = "VARIANT";

    private final ExternalResourceTransformer<AlphaGenome> transformer;

    @Autowired
    public AlphaGenomeDataFetcher(ExternalResourceTransformer<AlphaGenome> transformer,
                                  @Value("${alphagenome.sidecar.url:http://localhost:8000/score}") String sidecarUrl,
                                  RestTemplate restTemplate)
    {
        super(sidecarUrl, MAIN_QUERY_PARAM, PLACEHOLDER, restTemplate);
        this.transformer = transformer;
    }

    @Override
    protected DBObject postForObject(String uri, Object requestBody)
    {
        return restTemplate.postForObject(uri, requestBody, BasicDBObject.class);
    }

    @Override
    public List<AlphaGenome> fetchInstances(Object requestBody)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchRawValue(requestBody), AlphaGenome.class);
    }

    @Override
    public List<AlphaGenome> fetchInstances(Map<String, String> queryParams)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchRawValue(queryParams), AlphaGenome.class);
    }
}
