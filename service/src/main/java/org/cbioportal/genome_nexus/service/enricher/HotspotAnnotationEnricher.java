package org.cbioportal.genome_nexus.service.enricher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.Hotspot;
import org.cbioportal.genome_nexus.model.HotspotAnnotation;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.AnnotationEnricher;
import org.cbioportal.genome_nexus.service.CancerHotspotService;
import org.cbioportal.genome_nexus.service.exception.CancerHotspotsWebServiceException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public class HotspotAnnotationEnricher implements AnnotationEnricher
{
    private static final Log LOG = LogFactory.getLog(HotspotAnnotationEnricher.class);

    private CancerHotspotService hotspotService;
    private Boolean fullInfo;

    public HotspotAnnotationEnricher(CancerHotspotService hotspotService)
    {
        this(hotspotService, false);
    }

    public HotspotAnnotationEnricher(CancerHotspotService hotspotService, Boolean fullInfo)
    {
        this.hotspotService = hotspotService;
        this.fullInfo = fullInfo;
    }

    @Override
    public void enrich(VariantAnnotation annotation)
    {
        if (annotation.getTranscriptConsequences() != null)
        {
            List<List<Hotspot>> hotspotsList = new ArrayList<>();

            for (TranscriptConsequence transcript : annotation.getTranscriptConsequences())
            {
                try {
                    hotspotsList.add(hotspotService.getHotspots(transcript, annotation));
                } catch (CancerHotspotsWebServiceException e) {
                    LOG.warn(e.getLocalizedMessage());
                }
            }

            HotspotAnnotation hotspotAnnotation = new HotspotAnnotation();

            if (hotspotsList.size() > 0)
            {
                hotspotAnnotation.setAnnotation(hotspotsList);
            }

            annotation.setHotspotAnnotation(hotspotAnnotation);
        }
    }
}
