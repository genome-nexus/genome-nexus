package org.cbioportal.genome_nexus.service.cached;

import org.cbioportal.genome_nexus.model.my_variant_info_model.MyVariantInfo;
import org.cbioportal.genome_nexus.persistence.MyVariantInfoRepository;
import org.cbioportal.genome_nexus.persistence.internal.MyVariantInfoRepositoryImpl;
import org.cbioportal.genome_nexus.service.remote.MyVariantInfoDataFetcher;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CachedMyVariantInfoFetcher extends BaseCachedExternalResourceFetcher<MyVariantInfo, MyVariantInfoRepository>
{
    @Autowired
    public CachedMyVariantInfoFetcher(ExternalResourceTransformer<MyVariantInfo> transformer,
                                         MyVariantInfoRepository repository,
                                         MyVariantInfoDataFetcher fetcher)
    {
        super(MyVariantInfoRepositoryImpl.COLLECTION,
            repository,
            MyVariantInfo.class,
            fetcher,
            transformer);
    }
}
