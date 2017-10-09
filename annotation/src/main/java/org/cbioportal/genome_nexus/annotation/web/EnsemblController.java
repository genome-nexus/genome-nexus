package org.cbioportal.genome_nexus.annotation.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cbioportal.genome_nexus.annotation.domain.EnsemblTranscript;
import org.cbioportal.genome_nexus.annotation.service.EnsemblService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
@Api(tags = "ensembl-controller", description = "Ensembl Controller")
public class EnsemblController
{
    private final EnsemblService ensemblService;

    @Autowired
    public EnsemblController(EnsemblService ensemblService)
    {
        this.ensemblService = ensemblService;
    }

    @ApiOperation(value = "Retrieves all Ensembl Transcripts",
        nickname = "fetchAllEnsemblTranscriptsGET")
    @RequestMapping(value = "/ensembl/transcript",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<EnsemblTranscript> fetchAllEnsemblTranscriptsGET()
    {
        return ensemblService.getAllEnsemblTranscripts();
    }

    @ApiOperation(value = "Retrieves Ensembl Transcripts by Ensembl transcript IDs",
        nickname = "fetchEnsemblTranscriptsByTranscriptIdsPOST")
    @RequestMapping(value = "/ensembl/transcript/id",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<EnsemblTranscript> fetchEnsemblTranscriptsByTranscriptIdsPOST(
        @ApiParam(value = "List of Ensembl transcript IDs. For example [\"ENST00000361390\",\"ENST00000361453\",\"ENST00000361624\"]",
            required = true,
            allowMultiple = true)
        @RequestBody List<String> transcriptIds)
    {
        List<EnsemblTranscript> transcripts = new ArrayList<>();

        for (String transcriptId : transcriptIds)
        {
            transcripts.addAll(this.ensemblService.getEnsemblTranscriptsByTranscriptId(transcriptId));
        }

        return transcripts;
    }

    @ApiOperation(value = "Retrieves Transcripts by an Ensembl transcript ID",
        nickname = "fetchEnsemblTranscriptsByTranscriptIdGET")
    @RequestMapping(value = "/ensembl/transcript/id/{transcriptId}",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<EnsemblTranscript> fetchEnsemblTranscriptsByTranscriptIdsGET(
        @ApiParam(value = "An Ensembl transcript ID. For example ENST00000361390",
            required = true,
            allowMultiple = true)
        @PathVariable String transcriptId)
    {
        return this.ensemblService.getEnsemblTranscriptsByTranscriptId(transcriptId);
    }

    @ApiOperation(value = "Retrieves Ensembl transcripts by Ensembl protein IDs",
        nickname = "fetchEnsemblTranscriptsByProteinIdsPOST")
    @RequestMapping(value = "/ensembl/transcript/protein",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<EnsemblTranscript> fetchEnsemblTranscriptsByProteinIdsPOST(
        @ApiParam(value = "List of Ensembl protein IDs. For example [\"ENSP00000439985\",\"ENSP00000478460\",\"ENSP00000346196\"]",
            required = true,
            allowMultiple = true)
        @RequestBody List<String> proteinIds)
    {
        List<EnsemblTranscript> transcripts = new ArrayList<>();

        for (String proteinId : proteinIds)
        {
            transcripts.addAll(this.ensemblService.getEnsemblTranscriptsByProteinId(proteinId));
        }

        return transcripts;
    }

    @ApiOperation(value = "Retrieves Ensembl transcripts by an Ensembl protein ID",
        nickname = "fetchEnsemblTranscriptsByProteinIdGET")
    @RequestMapping(value = "/ensembl/transcript/protein/{proteinId}",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<EnsemblTranscript> fetchEnsemblTranscriptsByProteinIdGET(
        @ApiParam(value = "An Ensembl protein ID. For example ENSP00000439985",
            required = true,
            allowMultiple = true)
        @PathVariable String proteinId)
    {
        return this.ensemblService.getEnsemblTranscriptsByProteinId(proteinId);
    }

    @ApiOperation(value = "Retrieves Ensembl transcripts by Ensembl gene IDs",
        nickname = "fetchEnsemblTranscriptsByGeneIdsPOST")
    @RequestMapping(value = "/ensembl/transcript/gene",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<EnsemblTranscript> fetchEnsemblTranscriptsByGeneIdsPOST(
        @ApiParam(value = "List of Ensembl gene IDs. For example [\"ENSG00000136999\",\"ENSG00000272398\",\"ENSG00000198695\"]",
            required = true,
            allowMultiple = true)
        @RequestBody List<String> geneIds)
    {
        List<EnsemblTranscript> transcripts = new ArrayList<>();

        for (String geneId : geneIds)
        {
            transcripts.addAll(this.ensemblService.getEnsemblTranscriptsByGeneId(geneId));
        }

        return transcripts;
    }

    @ApiOperation(value = "Retrieves Ensembl transcripts by an Ensembl gene ID",
        nickname = "fetchEnsemblTranscriptsByGeneIdGET")
    @RequestMapping(value = "/ensembl/transcript/gene/{geneId}",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<EnsemblTranscript> fetchEnsemblTranscriptsByGeneIdGET(
        @ApiParam(value = "An Ensembl gene ID. For example ENSG00000136999",
            required = true,
            allowMultiple = true)
        @PathVariable String geneId)
    {
        return this.ensemblService.getEnsemblTranscriptsByGeneId(geneId);
    }

    @ApiOperation(value = "Retrieves Ensembl transcripts by Hugo Symbols",
        nickname = "fetchEnsemblTranscriptsByHugoSymbolsPOST")
    @RequestMapping(value = "/ensembl/transcript/hgnc",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<EnsemblTranscript> fetchEnsemblTranscriptsByHugoSymbolPOST(
        @ApiParam(value = "List of Hugo Symbols. For example [\"TP53\",\"PIK3CA\",\"BRCA1\"]",
            required = true,
            allowMultiple = true)
        @RequestBody List<String> hugoSymbols,
        @RequestParam(required = false)
        @ApiParam(value="Isoform override source. For example uniprot",
            required = false)
            String isoformOverrideSource)
    {
        List<EnsemblTranscript> transcripts = new ArrayList<>();

        for (String hugoSymbol : hugoSymbols)
        {
            transcripts.addAll(this.ensemblService.getEnsemblTranscriptsByHugoSymbol(hugoSymbol, isoformOverrideSource));
        }

        return transcripts;
    }

    @ApiOperation(value = "Retrieves Ensembl transcripts by an Ensembl gene ID",
        nickname = "fetchEnsemblTranscriptsByHugoSymbolGET")
    @RequestMapping(value = "/ensembl/transcript/hgnc/{hugoSymbol}",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<EnsemblTranscript> fetchEnsemblTranscriptsByHugoSymbolGET(
        @ApiParam(value = "A Hugo Symbol. For example TP53",
            required = true,
            allowMultiple = true)
        @PathVariable String hugoSymbol,
        @RequestParam(required = false)
        @ApiParam(value="Isoform override source. For example uniprot",
            defaultValue="uniprot",
            required = false)
            String isoformOverrideSource)
    {
        return this.ensemblService.getEnsemblTranscriptsByHugoSymbol(hugoSymbol, isoformOverrideSource);
    }
}

