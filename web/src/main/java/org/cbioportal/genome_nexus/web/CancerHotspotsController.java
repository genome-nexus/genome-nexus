package org.cbioportal.genome_nexus.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cbioportal.genome_nexus.model.Hotspot;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.HotspotService;
import org.cbioportal.genome_nexus.web.config.InternalApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@InternalApi
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
@Api(tags = "cancer-hotspots-controller", description = "Cancer Hotspots Controller")
public class CancerHotspotsController
{
    private final VariantAnnotator variantAnnotator;
    private final HotspotService hotspotService;

    @Autowired
    public CancerHotspotsController(VariantAnnotator variantAnnotator,
                                    HotspotService hotspotService)
    {
        this.variantAnnotator = variantAnnotator;
        this.hotspotService = hotspotService;
    }

    @ApiOperation(value = "Retrieves hotspot annotation for a specific variant",
        nickname = "fetchHotspotAnnotationGET")
    @RequestMapping(value = "/cancer_hotspots/{variant:.+}",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<Hotspot> fetchHotspotAnnotationGET(
        @ApiParam(value="A variant. For example 7:g.140453136A>T",
            required = true,
            allowMultiple = true)
        @PathVariable String variant)
    {
        return this.getHotspotAnnotations(variant);
    }

    @ApiOperation(value = "Retrieves hotspot annotation for the provided list of variants",
        nickname = "fetchHotspotAnnotationPOST")
    @RequestMapping(value = "/cancer_hotspots",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<Hotspot> fetchHotspotAnnotationPOST(
        @ApiParam(value="List of variants. For example [\"7:g.140453136A>T\",\"12:g.25398285C>A\"]",
            required = true,
            allowMultiple = true)
        @RequestBody List<String> variants)
    {
        return this.getHotspotAnnotations(variants);
    }

    private List<Hotspot> getHotspotAnnotations(String variant)
    {
        VariantAnnotation variantAnnotation = this.variantAnnotator.getVariantAnnotation(variant);
        List<Hotspot> hotspots = new ArrayList<>();

        if (variantAnnotation != null &&
            variantAnnotation.getTranscriptConsequences() != null)
        {
            for (TranscriptConsequence transcript : variantAnnotation.getTranscriptConsequences())
            {
                hotspots.addAll(getHotspotAnnotation(transcript));
            }
        }

        return hotspots;
    }

    private List<Hotspot> getHotspotAnnotations(List<String> variants)
    {
        List<VariantAnnotation> variantAnnotations = this.variantAnnotator.getVariantAnnotations(variants);

        List<Hotspot> hotspots = new ArrayList<>();

        for (VariantAnnotation variantAnnotation : variantAnnotations)
        {
            if (variantAnnotation.getTranscriptConsequences() != null)
            {
                for (TranscriptConsequence transcript : variantAnnotation.getTranscriptConsequences())
                {
                    hotspots.addAll(getHotspotAnnotation(transcript));
                }
            }
        }

        return hotspots;
    }

    private List<Hotspot> getHotspotAnnotation(TranscriptConsequence transcript)
    {

        // String transcriptId = transcript.getTranscriptId();
        // Hotspot hotspot = hotspotRepository.findOne(transcriptId);

        // hotspotService.setHotspotsURL("http://cancerhotspots.org/api/hotspots/single/");
        // get the hotspot(s) from the web service
        List<Hotspot> hotspots = hotspotService.getHotspots(transcript);

        // do not cache anything for now
        // hotspotRepository.save(hotspots);

        return hotspots;
    }
}
