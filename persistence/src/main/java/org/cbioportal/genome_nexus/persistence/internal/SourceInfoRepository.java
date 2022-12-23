package org.cbioportal.genome_nexus.persistence.internal;

import java.util.List;

import org.cbioportal.genome_nexus.model.AggregateSourceInfo;
import org.cbioportal.genome_nexus.model.GenomeNexusInfo;
import org.cbioportal.genome_nexus.model.SourceVersionInfo;
import org.cbioportal.genome_nexus.model.VEPInfo;
import org.cbioportal.genome_nexus.persistence.AnnotationVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

@Repository
public class SourceInfoRepository {

    @Autowired
    private GenomeNexusInfo genomeNexusInfo;

    @Autowired
    private VEPInfo vepInfo;

    @Autowired
    private AnnotationVersionRepository annotationVersionRepository;

    @Bean
    public GenomeNexusInfo genomeNexusInfo(@Value("${genomenexus.server.version:NA}") String serverVersion, @Value("${spring.mongodb.embedded.version:NA}") String dbVersion) {
        return new GenomeNexusInfo(serverVersion, dbVersion);
    }

    @Bean
    public VEPInfo vepInfo(@Value("${gn_vep.server.version:NA}") String serverVersion, @Value("${gn_vep.cache.version:NA}") String cacheVersion, @Value("${gn_vep.region.url:}") String vepRegionURL) {
        return new VEPInfo(serverVersion, cacheVersion, vepRegionURL);
    }

    public AggregateSourceInfo getAggregateSourceInfo() {
        AggregateSourceInfo aggregateSourceInfo = new AggregateSourceInfo(genomeNexusInfo, vepInfo);
        List<SourceVersionInfo> sourceVersionInfos = annotationVersionRepository.findAll();
        aggregateSourceInfo.setAnnotationSourcesInfo(sourceVersionInfos);
        return aggregateSourceInfo;
    }
}
