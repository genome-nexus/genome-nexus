package org.cbioportal.genome_nexus.web;

import org.cbioportal.genome_nexus.model.CuriousCasesComment;
import org.cbioportal.genome_nexus.service.CuriousCasesService;
import org.cbioportal.genome_nexus.service.exception.CuriousCasesCommentNotFoundException;
import org.cbioportal.genome_nexus.service.exception.CuriousCasesCommentWebServiceException;
import org.cbioportal.genome_nexus.web.config.PublicApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@PublicApi
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
@Api(tags = "curious-cases-controller", description = "Curious Cases Controller")
public class CuriousCasesController
{
    private final CuriousCasesService curiousCasesService;

    @Autowired
    public CuriousCasesController(CuriousCasesService curiousCasesService)
    {
        this.curiousCasesService = curiousCasesService;
    }

    @ApiOperation(value = "Retrieves Curious Cases info by a variant",
        nickname = "fetchCuriousCasesCommentGET")
    @RequestMapping(value = "curious_cases/comment/{region}",
        method = RequestMethod.GET,
        produces = "application/json")
    public CuriousCasesComment fetchCuriousCasesCommentGET(
        @ApiParam(value = "region, for example something_valid",
            required = true,
            allowMultiple = false)
        @PathVariable String region) throws CuriousCasesCommentNotFoundException, CuriousCasesCommentWebServiceException
    {
        return curiousCasesService.getCuriousCasesComment(region);
    }
}