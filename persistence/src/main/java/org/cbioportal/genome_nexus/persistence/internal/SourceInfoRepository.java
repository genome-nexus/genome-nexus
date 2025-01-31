package org.cbioportal.genome_nexus.persistence.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Repository
public class SourceInfoRepository {

    private static String NA_STRING = "NA";

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
    public VEPInfo vepInfo(@Value("${vep.url:}") String vepUrl, @Value("${vep.static:true}") boolean isStatic) throws MalformedURLException {
        URL url = new URL(vepUrl);
        RestTemplate restTemplate = new RestTemplate();
        String serverVersion = NA_STRING;
        String vepVersion = NA_STRING;
        String comment = null;
        try {
            VepVersionObject vepVersionObject = restTemplate.getForObject(url.getProtocol() + "://" + url.getAuthority() + "/info/software", VepVersionObject.class);
            serverVersion = (String) vepVersionObject.get("server");
            if (serverVersion == null) {
                serverVersion = NA_STRING;
            }
            vepVersion = String.valueOf(vepVersionObject.get("release"));
        } catch (RestClientException e) {
            comment = "Error fetching version information";
        }
        
        return new VEPInfo(serverVersion, vepVersion, comment, isStatic);
    }

    public AggregateSourceInfo getAggregateSourceInfo() {
        AggregateSourceInfo aggregateSourceInfo = new AggregateSourceInfo(genomeNexusInfo, vepInfo);
        List<SourceVersionInfo> sourceVersionInfos = annotationVersionRepository.findAll();
        aggregateSourceInfo.setAnnotationSourcesInfo(sourceVersionInfos);
        return aggregateSourceInfo;
    }

    private static class VepVersionObject extends HashMap<String, Object> {}
}