package org.cbioportal.genome_nexus.annotation.service.internal;

import org.cbioportal.genome_nexus.annotation.domain.Hotspot;
import org.cbioportal.genome_nexus.annotation.domain.TranscriptConsequence;
import org.cbioportal.genome_nexus.annotation.domain.VariantAnnotation;
import org.cbioportal.genome_nexus.annotation.service.AnnotationEnricher;
import org.cbioportal.genome_nexus.annotation.service.HotspotService;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public class HotspotAnnotationEnricher implements AnnotationEnricher
{
    private HotspotService hotspotService;
    private Boolean fullInfo;

    public HotspotAnnotationEnricher(HotspotService hotspotService)
    {
        this(hotspotService, false);
    }

    public HotspotAnnotationEnricher(HotspotService hotspotService, Boolean fullInfo)
    {
        this.hotspotService = hotspotService;
        this.fullInfo = fullInfo;
    }

    @Override
    public void enrich(VariantAnnotation annotation)
    {
        if (annotation.getTranscriptConsequences() != null)
        {
            for (TranscriptConsequence transcript : annotation.getTranscriptConsequences())
            {
                List<Hotspot> hotspots = hotspotService.getHotspots(transcript);

                if (fullInfo) {
                    enrichWithFullInfo(transcript, hotspots);
                }
                else {
                    enrichWithSummary(transcript, hotspots);
                }
            }
        }
    }

    private void enrichWithSummary(TranscriptConsequence transcript, List<Hotspot> hotspots)
    {
        // add a boolean field to the transcript
        transcript.setDynamicProp("isHotspot", hotspots.size() > 0);
    }

    private void enrichWithFullInfo(TranscriptConsequence transcript, List<Hotspot> hotspots)
    {
        // attach the full list of hotspots
        if (hotspots.size() > 0)
        {
            transcript.setDynamicProp("hotspots", hotspots);
        }
    }
}
