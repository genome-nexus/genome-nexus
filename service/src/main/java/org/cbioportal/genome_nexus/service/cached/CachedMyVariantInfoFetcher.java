package org.cbioportal.genome_nexus.service.cached;

import org.apache.logging.log4j.util.Strings;
import org.cbioportal.genome_nexus.model.my_variant_info_model.MyVariantInfo;
import org.cbioportal.genome_nexus.persistence.MyVariantInfoRepository;
import org.cbioportal.genome_nexus.persistence.internal.MyVariantInfoRepositoryImpl;
import org.cbioportal.genome_nexus.service.remote.MyVariantInfoDataFetcher;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Set;

@Component
public class CachedMyVariantInfoFetcher extends BaseCachedExternalResourceFetcher<MyVariantInfo, MyVariantInfoRepository>
{
    @Autowired
    public CachedMyVariantInfoFetcher(
        ExternalResourceTransformer<MyVariantInfo> transformer,
        MyVariantInfoRepository repository,
        MyVariantInfoDataFetcher fetcher,
        @Value("${myvariantinfo.max_page_size:200}") Integer maxPageSize
    )
    {
        super(
            MyVariantInfoRepositoryImpl.COLLECTION,
            repository,
            MyVariantInfo.class,
            fetcher,
            transformer,
            maxPageSize
        );
    }

    protected Object buildRequestBody(Set<String> ids)
    {
        MultiValueMap<String, String> requestBody= new LinkedMultiValueMap<>();
        requestBody.add("ids", Strings.join(ids, ','));
        // TODO this way it looks cleaner, but it only fetches one result
        // requestBody.put("ids", new ArrayList<>(ids));

        return requestBody;
    }

    @Override
    protected String extractId(MyVariantInfo instance)
    {
        return instance.getVariant();
    }
}
