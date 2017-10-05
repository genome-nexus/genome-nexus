package org.cbioportal.genome_nexus.annotation.web;

import io.swagger.annotations.*;
import org.cbioportal.genome_nexus.annotation.domain.GeneXref;
import org.cbioportal.genome_nexus.annotation.service.GeneXrefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
@Api(tags = "cross-reference-controller", description = "Cross Reference Controller")
public class CrossReferenceController
{
    private final GeneXrefService geneXrefService;

    @Autowired
    public CrossReferenceController(GeneXrefService geneXrefService)
    {
        this.geneXrefService = geneXrefService;
    }

    @ApiOperation(value = "Perform lookups of Ensembl identifiers and retrieve their external referenes in other databases",
        nickname = "getGeneXrefs")
    @RequestMapping(value = "/xrefs/{accession}",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<GeneXref> getGeneXrefs(
        @PathVariable
        @ApiParam(value="Ensembl gene accession. For example ENSG00000169083",
            required = true)
            String accession) {
        return geneXrefService.getGeneXrefs(accession);
    }
}
