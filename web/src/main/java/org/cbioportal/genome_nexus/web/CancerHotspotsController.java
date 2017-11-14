package org.cbioportal.genome_nexus.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cbioportal.genome_nexus.model.Hotspot;
import org.cbioportal.genome_nexus.service.HotspotService;
import org.cbioportal.genome_nexus.service.exception.CancerHotspotsWebServiceException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.cbioportal.genome_nexus.web.config.InternalApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@InternalApi
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
@Api(tags = "cancer-hotspots-controller", description = "Cancer Hotspots Controller")
public class CancerHotspotsController
{
    private final HotspotService hotspotService;

    @Autowired
    public CancerHotspotsController(HotspotService hotspotService)
    {
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
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException,
        CancerHotspotsWebServiceException
    {
        return this.hotspotService.getHotspotAnnotations(variant);
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
        @RequestBody List<String> variants) throws CancerHotspotsWebServiceException
    {
        return this.hotspotService.getHotspotAnnotations(variants);
    }
}
