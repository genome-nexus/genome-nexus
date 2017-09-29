package org.cbioportal.genome_nexus.annotation.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cbioportal.genome_nexus.annotation.domain.PfamDomain;
import org.cbioportal.genome_nexus.annotation.service.PfamDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
@Api(tags = "pfam-controller", description = "PFAM Controller")
public class PfamController
{
    private final PfamDomainService pfamDomainService;

    @Autowired
    public PfamController(PfamDomainService pfamDomainService)
    {
        this.pfamDomainService = pfamDomainService;
    }

    @ApiOperation(value = "Retrieves all PFAM domains",
        nickname = "fetchAllPfamDomainsGET")
    @RequestMapping(value = "/pfam/domain",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<PfamDomain> fetchAllPfamDomainsGET()
    {
        return pfamDomainService.getAllPfamDomains();
    }

    @ApiOperation(value = "Retrieves PFAM domains by Ensembl transcript IDs",
        nickname = "fetchPfamDomainsByTranscriptIdsPOST")
    @RequestMapping(value = "/pfam/domain/transcript",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<PfamDomain> fetchPfamDomainsByTranscriptIdsPOST(
        @ApiParam(value = "List of Ensembl transcript IDs. For example [\"ENST00000361390\",\"ENST00000361453\",\"ENST00000361624\"]",
            required = true,
            allowMultiple = true)
        @RequestBody List<String> transcriptIds)
    {
        List<PfamDomain> domains = new ArrayList<>();

        for (String transcriptId : transcriptIds)
        {
            domains.addAll(this.pfamDomainService.getPfamDomainsByTranscriptId(transcriptId));
        }

        return domains;
    }

    @ApiOperation(value = "Retrieves PFAM domains by an Ensembl transcript ID",
        nickname = "fetchPfamDomainsByTranscriptIdGET")
    @RequestMapping(value = "/pfam/domain/transcript/{transcriptId}",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<PfamDomain> fetchPfamDomainsByTranscriptIdsGET(
        @ApiParam(value = "An Ensembl transcript ID. For example ENST00000361390",
            required = true,
            allowMultiple = true)
        @PathVariable String transcriptId)
    {
        return this.pfamDomainService.getPfamDomainsByTranscriptId(transcriptId);
    }

    @ApiOperation(value = "Retrieves PFAM domains by Ensembl protein IDs",
        nickname = "fetchPfamDomainsByProteinIdsPOST")
    @RequestMapping(value = "/pfam/domain/protein",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<PfamDomain> fetchPfamDomainsByProteinIdsPOST(
        @ApiParam(value = "List of Ensembl protein IDs. For example [\"ENSP00000439985\",\"ENSP00000478460\",\"ENSP00000346196\"]",
            required = true,
            allowMultiple = true)
        @RequestBody List<String> proteinIds)
    {
        List<PfamDomain> domains = new ArrayList<>();

        for (String proteinId : proteinIds)
        {
            domains.addAll(this.pfamDomainService.getPfamDomainsByProteinId(proteinId));
        }

        return domains;
    }

    @ApiOperation(value = "Retrieves PFAM domains by an Ensembl protein ID",
        nickname = "fetchPfamDomainsByProteinIdGET")
    @RequestMapping(value = "/pfam/domain/protein/{proteinId}",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<PfamDomain> fetchPfamDomainsByProteinIdGET(
        @ApiParam(value = "An Ensembl protein ID. For example ENSP00000439985",
            required = true,
            allowMultiple = true)
        @PathVariable String proteinId)
    {
        return this.pfamDomainService.getPfamDomainsByProteinId(proteinId);
    }

    @ApiOperation(value = "Retrieves PFAM domains by Ensembl gene IDs",
        nickname = "fetchPfamDomainsByGeneIdsPOST")
    @RequestMapping(value = "/pfam/domain/gene",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<PfamDomain> fetchPfamDomainsByGeneIdsPOST(
        @ApiParam(value = "List of Ensembl gene IDs. For example [\"ENSG00000136999\",\"ENSG00000272398\",\"ENSG00000198695\"]",
            required = true,
            allowMultiple = true)
        @RequestBody List<String> geneIds)
    {
        List<PfamDomain> domains = new ArrayList<>();

        for (String geneId : geneIds)
        {
            domains.addAll(this.pfamDomainService.getPfamDomainsByGeneId(geneId));
        }

        return domains;
    }

    @ApiOperation(value = "Retrieves PFAM domains by an Ensembl gene ID",
        nickname = "fetchPfamDomainsByGeneIdGET")
    @RequestMapping(value = "/pfam/domain/gene/{geneId}",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<PfamDomain> fetchPfamDomainsByGeneIdGET(
        @ApiParam(value = "An Ensembl gene ID. For example ENSG00000136999",
            required = true,
            allowMultiple = true)
        @PathVariable String geneId)
    {
        return this.pfamDomainService.getPfamDomainsByGeneId(geneId);
    }

    @ApiOperation(value = "Retrieves PFAM domains by PFAM domain IDs",
        nickname = "fetchPfamDomainsByPfamIdsPOST")
    @RequestMapping(value = "/pfam/domain/pfam",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<PfamDomain> fetchPfamDomainsByPfamIdsPOST(
        @ApiParam(value = "List of PFAM domain IDs. For example [\"PF02827\",\"PF00093\",\"PF15276\"]",
            required = true,
            allowMultiple = true)
        @RequestBody List<String> pfamDomainIds)
    {
        List<PfamDomain> domains = new ArrayList<>();

        for (String pfamDomainId : pfamDomainIds)
        {
            domains.addAll(this.pfamDomainService.getPfamDomainsByPfamDomainId(pfamDomainId));
        }

        return domains;
    }

    @ApiOperation(value = "Retrieves PFAM domains by a PFAM domain ID",
        nickname = "fetchPfamDomainsByPfamIdGET")
    @RequestMapping(value = "/pfam/domain/pfam/{pfamDomainId}",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<PfamDomain> fetchPfamDomainsByPfamIdGET(
        @ApiParam(value = "A PFAM domain ID. For example PF02827",
            required = true,
            allowMultiple = true)
        @PathVariable String pfamDomainId)
    {
        return this.pfamDomainService.getPfamDomainsByPfamDomainId(pfamDomainId);
    }
}
