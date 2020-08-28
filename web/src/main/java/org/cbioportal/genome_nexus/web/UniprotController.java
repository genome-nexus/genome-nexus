package org.cbioportal.genome_nexus.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cbioportal.genome_nexus.model.uniprot.ProteinFeatureInfo;
import org.cbioportal.genome_nexus.service.UniprotService;
import org.cbioportal.genome_nexus.web.config.InternalApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@InternalApi
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
@Api(tags = "uniprot-controller", description = "Uniprot Controller")
public class UniprotController {

    private UniprotService uniprotService;

    @Autowired
    public void UniprotController(UniprotService uniprotService)
    {
        this.uniprotService = uniprotService;
    }

    @ApiOperation(value = "Retrieves UniProt protein sequence features by accession",
        nickname = "fetchUniprotFeaturesByAccessionGET")
    @RequestMapping(value = "/uniprot/features/{accession:.+}",
        method = RequestMethod.GET,
        produces = "application/json")
    public ProteinFeatureInfo fetchUniprotFeaturesByAccessionGET(
    @PathVariable
    @ApiParam(
        value = "UniProt accession. For example P04637",
        required = true)
    String accession,
    @RequestParam(required = false) 
    @ApiParam(
        value="Category type(s): MOLECULE_PROCESSING, TOPOLOGY, SEQUENCE_INFORMATION, STRUCTURAL, DOMAINS_AND_SITES, PTM, VARIANTS, MUTAGENESIS. Comma separated values accepted up to 20",
        required = false)
    List<String> categories,
    @RequestParam(required = false)
    @ApiParam(
        value="Feature type(s): INIT_MET, SIGNAL, PROPEP, TRANSIT, CHAIN, PEPTIDE, TOPO_DOM, TRANSMEM, DOMAIN, REPEAT, CA_BIND, ZN_FING, DNA_BIND, NP_BIND, REGION, COILED, MOTIF, COMPBIAS, ACT_SITE, METAL, BINDING, SITE, NON_STD, MOD_RES, LIPID, CARBOHYD, DISULFID, CROSSLNK, VAR_SEQ, VARIANT, MUTAGEN, UNSURE, CONFLICT, NON_CONS, NON_TER, HELIX, TURN, STRAND, INTRAMEM. Comma separated values accepted up to 20",
        required = false)
    List<String> types)
    {
       return this.uniprotService.getUniprotFeaturesByAccession(accession, categories, types);
    }  
}