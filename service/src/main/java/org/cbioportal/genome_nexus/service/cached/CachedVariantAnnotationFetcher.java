package org.cbioportal.genome_nexus.service.cached;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.persistence.VariantAnnotationRepository;
import org.cbioportal.genome_nexus.persistence.internal.VariantAnnotationRepositoryImpl;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.cbioportal.genome_nexus.service.remote.VEPDataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CachedVariantAnnotationFetcher extends BaseCachedExternalResourceFetcher<VariantAnnotation, VariantAnnotationRepository>
{
    @Autowired
    public CachedVariantAnnotationFetcher(ExternalResourceTransformer<VariantAnnotation> transformer,
                                          VariantAnnotationRepository repository,
                                          VEPDataFetcher fetcher)
    {
        super(VariantAnnotationRepositoryImpl.COLLECTION,
            repository,
            VariantAnnotation.class,
            fetcher,
            transformer);
    }
}
