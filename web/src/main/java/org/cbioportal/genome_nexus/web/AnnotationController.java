/*
 * Copyright (c) 2015-17 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.cbioportal.genome_nexus.web;

import io.swagger.annotations.*;

import org.cbioportal.genome_nexus.model.*;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.cbioportal.genome_nexus.service.*;
import org.cbioportal.genome_nexus.web.validation.*;

import org.cbioportal.genome_nexus.web.config.PublicApi;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

import java.util.*;


/**
 * @author Benjamin Gross
 */
@PublicApi
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
@Validated
public class AnnotationController
{
    private final VariantAnnotationService variantAnnotationService;

    @Autowired
    public AnnotationController(VariantAnnotationService variantAnnotationService)
    {
        this.variantAnnotationService = variantAnnotationService;
    }

    // TODO remove this endpoint after all internal dependencies are resolved
    @ApiOperation(value = "Retrieves VEP annotation for the provided list of variants",
        hidden = true,
        nickname = "getVariantAnnotation")
    @RequestMapping(value = "/hgvs/{variants:.+}",
        method = RequestMethod.GET,
        produces = "application/json")
    @Deprecated
    public List<VariantAnnotation> getVariantAnnotation(
        @PathVariable
        @ApiParam(value="Comma separated list of variants. For example X:g.66937331T>A,17:g.41242962_41242963insGA",
            required = true,
            allowMultiple = true)
        List<String> variants,
        @RequestParam(required = false)
        @ApiParam(value="Isoform override source. For example uniprot",
            required = false)
        String isoformOverrideSource,
        @RequestParam(required = false)
        @ApiParam(value="Comma separated list of fields to include (case-sensitive!). " +
            "For example: hotspots,mutation_assessor", required = false, defaultValue = "hotspots,mutation_assessor")
        List<String> fields)
    {
        return this.fetchVariantAnnotationPOST(variants, isoformOverrideSource, fields);
    }

    // TODO remove this endpoint after all internal dependencies are resolved
    @ApiOperation(value = "Retrieves VEP annotation for the provided list of variants",
        hidden = true,
        nickname = "postVariantAnnotation")
    @RequestMapping(value = "/hgvs",
        method = RequestMethod.POST,
        produces = "application/json")
    @Deprecated
    public List<VariantAnnotation> postVariantAnnotation(
        @RequestParam
        @ApiParam(value="Comma separated list of variants. For example X:g.66937331T>A,17:g.41242962_41242963insGA",
            required = true,
            allowMultiple = true)
            List<String> variants,
        @RequestParam(required = false)
        @ApiParam(value="Isoform override source. For example uniprot",
            required = false)
            String isoformOverrideSource,
        @RequestParam(required = false)
        @ApiParam(value="Comma separated list of fields to include (case-sensitive!). " +
            "For example: hotspots,mutation_assessor", required = false, defaultValue = "hotspots,mutation_assessor")
            List<String> fields)
    {
        return fetchVariantAnnotationPOST(variants, isoformOverrideSource, fields);
    }

    @ApiOperation(value = "Retrieves VEP annotation for the provided list of variants",
        nickname = "fetchVariantAnnotationPOST")
    @RequestMapping(value = "/annotation",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<VariantAnnotation> fetchVariantAnnotationPOST(
        @ApiParam(value="List of variants. For example [\"X:g.66937331T>A\",\"17:g.41242962_41242963insGA\"]",
            required = true)
        @RequestBody List<String> variants,
        @ApiParam(value="Isoform override source. For example uniprot",
            required = false)
        @RequestParam(required = false) String isoformOverrideSource,
        @ApiParam(value="Comma separated list of fields to include (case-sensitive!). " +
            "For example: hotspots,mutation_assessor", required = false, defaultValue = "hotspots,mutation_assessor")
        @RequestParam(required = false) List<String> fields)
    {
        return this.variantAnnotationService.getAnnotations(variants, isoformOverrideSource, fields);
    }

    @ApiOperation(value = "Retrieves VEP annotation for the provided variant",
        nickname = "fetchVariantAnnotationGET")
    @RequestMapping(value = "/annotation/{variant:.+}",
        method = RequestMethod.GET,
        produces = "application/json")
    public VariantAnnotation fetchVariantAnnotationGET(
        @ApiParam(value="Variant. For example 17:g.41242962_41242963insGA",
            required = true)
        @PathVariable String variant,
        @ApiParam(value="Isoform override source. For example uniprot",
            required = false)
        @RequestParam(required = false) String isoformOverrideSource,
        @ApiParam(value="Comma separated list of fields to include (case-sensitive!). " +
            "For example: hotspots,mutation_assessor", required = false, defaultValue = "hotspots,mutation_assessor")
        @RequestParam(required = false) List<String> fields)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        return this.variantAnnotationService.getAnnotation(variant, isoformOverrideSource, fields);
    }

    @ApiOperation(value = "Retrieves VEP annotation for the provided list of genomic locations",
        nickname = "fetchVariantAnnotationByGenomicLocationPOST")
    @RequestMapping(value = "/annotation/genomic",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<VariantAnnotation> fetchVariantAnnotationByGenomicLocationPOST(
        @ApiParam(value="List of Genomic Locations",
            required = true)
        @RequestBody List<GenomicLocation> genomicLocations,
        @ApiParam(value="Isoform override source. For example uniprot",
            required = false)
        @RequestParam(required = false) String isoformOverrideSource,
        @ApiParam(value="Comma separated list of fields to include (case-sensitive!). " +
            "For example: hotspots,mutation_assessor", required = false, defaultValue = "hotspots,mutation_assessor")
        @RequestParam(required = false) List<String> fields)
    {
        return this.variantAnnotationService.getAnnotationsByGenomicLocations(
            genomicLocations, isoformOverrideSource, fields);
    }

    @ApiOperation(value = "Retrieves VEP annotation for the provided genomic location",
        nickname = "fetchVariantAnnotationByGenomicLocationGET")
    @RequestMapping(value = "/annotation/genomic/{genomicLocation:.+}",
        method = RequestMethod.GET,
        produces = "application/json")
    public VariantAnnotation fetchVariantAnnotationByGenomicLocationGET(
        @ApiParam(value="A genomic location. For example 7,140453136,140453136,A,T",
            required = true)
        @PathVariable @ValidGenomicLocation String genomicLocation,
        @ApiParam(value="Isoform override source. For example uniprot",
            required = false)
        @RequestParam(required = false) String isoformOverrideSource,
        @ApiParam(value="Comma separated list of fields to include (case-sensitive!). " +
            "For example: hotspots,mutation_assessor", required = false, defaultValue = "hotspots,mutation_assessor")
        @RequestParam(required = false) List<String> fields)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        return this.variantAnnotationService.getAnnotationByGenomicLocation(genomicLocation, isoformOverrideSource, fields);
    }

    @ApiOperation(value = "Retrieves VEP annotation for the provided list of dbSNP ids",
        nickname = "fetchVariantAnnotationByIdPOST")
    @RequestMapping(value = "/annotation/dbsnp/",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<VariantAnnotation> fetchVariantbyDbSnpIdAnnotationPOST(
        @ApiParam(value="List of variant IDs. For example [\"rs116035550\"]",
            required = true)
        @RequestBody List<String> variantIds,
        @ApiParam(value="Isoform override source. For example uniprot",
            required = false)
            @RequestParam(required = false) String isoformOverrideSource,
        @ApiParam(value="Comma separated list of fields to include (case-sensitive!). " +
            "For example: annotation_summary", required = false, defaultValue = "annotation_summary")
        @RequestParam(required = false) List<String> fields)
    {
        return this.variantAnnotationService.getAnnotationsByIds(variantIds, isoformOverrideSource, fields);
    }

    @ApiOperation(value = "Retrieves VEP annotation for the give dbSNP id",
        nickname = "fetchVariantAnnotationByIdGET")
    @RequestMapping(value = "/annotation/dbsnp/{variantId:.+}",
        method = RequestMethod.GET,
        produces = "application/json")
    public VariantAnnotation fetchVariantAnnotationByDbSnpIdGET(
        @ApiParam(value="dbSNP id. For example rs116035550.",
            required = true)
        @PathVariable String variantId,
        @ApiParam(value="Isoform override source. For example uniprot",
            required = false)
        @RequestParam(required = false) String isoformOverrideSource,
        @ApiParam(value="Comma separated list of fields to include (case-sensitive!). " +
            "For example: annotation_summary", required = false, defaultValue = "annotation_summary")
        @RequestParam(required = false) List<String> fields)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        return this.variantAnnotationService.getAnnotationById(variantId, isoformOverrideSource, fields);
    }

}

