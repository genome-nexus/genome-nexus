package org.cbioportal.genome_nexus.service.remote;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.internal.ExternalResourceTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.Map;

@Component
public class VEPDataFetcher extends BaseExternalResourceFetcher<VariantAnnotation>
{
    private static final String MAIN_QUERY_PARAM = "variant";
    private static final String PLACEHOLDER = "VARIANT";

    private final ExternalResourceTransformer transformer;

    @Autowired
    public VEPDataFetcher(ExternalResourceTransformer externalResourceTransformer,
                          @Value("${vep.url}") String vepUrl)
    {
        super(vepUrl, MAIN_QUERY_PARAM, PLACEHOLDER);
        this.transformer = externalResourceTransformer;
    }

    @Override
    public List<VariantAnnotation> fetchInstances(Map<String, String> queryParams)
        throws HttpClientErrorException, ResourceAccessException, ResourceMappingException
    {
        return this.transformer.transform(this.fetchStringValue(queryParams), VariantAnnotation.class);
    }

    public ExternalResourceTransformer getTransformer() {
        return transformer;
    }
}
