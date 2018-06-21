package org.cbioportal.genome_nexus.service.remote;

import org.cbioportal.genome_nexus.model.Hotspot;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CancerHotspot3dDataFetcher extends CancerHotspotDataFetcher
{
    @Autowired
    public CancerHotspot3dDataFetcher(ExternalResourceTransformer<Hotspot> transformer,
                                      @Value("${hotspots.3d.url}") String hotspotsUrl)
    {
        super(transformer, hotspotsUrl);
    }
}
