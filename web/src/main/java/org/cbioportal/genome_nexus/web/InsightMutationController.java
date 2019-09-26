package org.cbioportal.genome_nexus.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cbioportal.genome_nexus.model.InsightMutation;
import org.cbioportal.genome_nexus.service.InsightMutationService;
import org.cbioportal.genome_nexus.web.config.InternalApi;
import org.cbioportal.genome_nexus.web.param.InsightMutationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@InternalApi
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
@Api(tags = "insight-mutation-controller", description = "Insight Mutation Controller")
public class InsightMutationController
{
    private final InsightMutationService insightMutationService;

    @Autowired
    public InsightMutationController(InsightMutationService insightMutationService)
    {
        this.insightMutationService = insightMutationService;
    }

    @ApiOperation(value = "Retrieves MSK Insight mutations by Mutation Filter",
        nickname = "fetchInsightMutationsByMutationFilterPOST")
    @RequestMapping(value = "/insight/mutation",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<InsightMutation> fetchInsightMutationsByMutationFilterPOST(
        @ApiParam(
            value = "List of Hugo Gene Symbols. For example [\"TP53\", \"PIK3CA\", \"BRCA1\"]",
            required = true)
        @RequestBody InsightMutationFilter mutationFilter)
    {
        return this.insightMutationService.getInsightMutations(mutationFilter.getHugoSymbols());
    }

    @ApiOperation(value = "Retrieves MSK Insight mutations by Hugo Gene Symbol",
        nickname = "fetchInsightMutationsByHugoSymbolGET")
    @RequestMapping(value = "/insight/mutation",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<InsightMutation> fetchInsightMutationsByHugoSymbolGET(
        @ApiParam(
            value="Hugo Symbol. For example BRCA1",
            required = false)
        @RequestParam(required = false) String hugoGeneSymbol)
    {
        return this.insightMutationService.getInsightMutations(hugoGeneSymbol);
    }
}
