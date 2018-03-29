package org.cbioportal.genome_nexus.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cbioportal.genome_nexus.model.IsoformOverride;
import org.cbioportal.genome_nexus.service.IsoformOverrideService;
import org.cbioportal.genome_nexus.service.exception.IsoformOverrideNotFoundException;
import org.cbioportal.genome_nexus.web.config.InternalApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@InternalApi
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
@Api(tags = "isoform-override-controller", description = "Isoform Override Controller")
public class IsoformOverrideController {

    private final IsoformOverrideService isoformOverrideService;

    @Autowired
    public IsoformOverrideController(IsoformOverrideService isoformOverrideService)
    {
        this.isoformOverrideService = isoformOverrideService;
    }

    @ApiOperation(value = "Gets the isoform override information for the specified source and transcript id",
        nickname = "fetchIsoformOverrideGET")
    @RequestMapping(value = "/isoform_override/{source}/{transcriptId}",
        method = RequestMethod.GET,
        produces = "application/json")
    public IsoformOverride fetchIsoformOverrideGET(
        @ApiParam(value="Override source. For example uniprot.",
            required = true)
        @PathVariable String source,
        @ApiParam(value="Transcript id. For example ENST00000361125.",
            required = true,
            allowMultiple = true)
        @PathVariable String transcriptId) throws IsoformOverrideNotFoundException
    {
        return isoformOverrideService.getIsoformOverride(source, transcriptId);
    }

    @ApiOperation(
        value = "Gets the isoform override information for the specified source and the list of transcript ids",
        nickname = "fetchIsoformOverridePOST")
    @RequestMapping(value = "/isoform_override",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<IsoformOverride> fetchIsoformOverridePOST(
        @ApiParam(value="Override source. For example uniprot",
            required = true)
        @RequestParam String source,
        @ApiParam(value="List of transcript ids. For example [\"ENST00000361125\",\"ENST00000443649\"]. ",
            required = true,
            allowMultiple = true)
        @RequestBody List<String> transcriptIds)
    {
        return isoformOverrideService.getIsoformOverrides(source, transcriptIds);
    }

    @ApiOperation(value = "Gets the isoform override information for the specified source",
        nickname = "fetchAllIsoformOverridesGET")
    @RequestMapping(value = "/isoform_override/{source}",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<IsoformOverride> fetchAllIsoformOverridesGET(
        @ApiParam(value="Override source. For example uniprot",
            required = true)
        @PathVariable String source) throws IsoformOverrideNotFoundException
    {
        return isoformOverrideService.getIsoformOverrides(source);
    }

    @ApiOperation(value = "Gets a list of available isoform override data sources",
        nickname = "fetchIsoformOverrideSourcesGET")
    @RequestMapping(value = "/isoform_override/sources",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<String> fetchIsoformOverrideSourcesGET()
    {
        return isoformOverrideService.getOverrideSources();
    }
}
