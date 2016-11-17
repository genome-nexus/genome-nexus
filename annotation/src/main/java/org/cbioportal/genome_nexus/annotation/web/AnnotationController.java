/*
 * Copyright (c) 2015 Memorial Sloan-Kettering Cancer Center.
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

package org.cbioportal.genome_nexus.annotation.web;

import io.swagger.annotations.*;
import org.cbioportal.genome_nexus.annotation.domain.*;
import org.cbioportal.genome_nexus.annotation.service.internal.IsoformAnnotationEnricher;
import org.cbioportal.genome_nexus.annotation.service.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Benjamin Gross
 */
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value = "/variant_annotation/")
public class AnnotationController
{
    private final VariantAnnotationService variantAnnotationService;
    private final VariantAnnotationRepository variantAnnotationRepository;
    private final IsoformOverrideService isoformOverrideService;

    // The post enrichment service enriches the annotation after saving
    // the original annotation data to the repository. Any enrichment
    // performed by the post enrichment service is not saved
    // to the annotation repository.
    private final EnrichmentService postEnrichmentService;

    @Autowired
    public AnnotationController(VariantAnnotationService variantAnnotationService,
                                VariantAnnotationRepository variantAnnotationRepository,
                                IsoformOverrideService isoformOverrideService,
                                EnrichmentService postEnrichmentService)
    {
        this.variantAnnotationService = variantAnnotationService;
        this.variantAnnotationRepository = variantAnnotationRepository;
        this.isoformOverrideService = isoformOverrideService;
        this.postEnrichmentService = postEnrichmentService;
    }

    @ApiOperation(value = "getVariantAnnotation",
        nickname = "getVariantAnnotation")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success",
            response = VariantAnnotation.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request")
    })
	@RequestMapping(value = "/hgvs/{variants:.+}",
        method = RequestMethod.GET,
        produces = "application/json")
	public List<VariantAnnotation> getVariantAnnotation(
        @PathVariable
        @ApiParam(value="Comma separated list of variants. For example X:g.66937331T>A,17:g.41242962->GA",
            required = true,
            allowMultiple = true)
        List<String> variants,
        @RequestParam(required = false)
        @ApiParam(value="Isoform override source. For example uniprot",
            required = false)
        String isoformOverrideSource)
	{
		List<VariantAnnotation> variantAnnotations = new ArrayList<>();

        // only register the enricher if the service actually has data for the given source
        if (isoformOverrideService.hasData(isoformOverrideSource))
        {
            AnnotationEnricher enricher = new IsoformAnnotationEnricher(
                isoformOverrideSource, isoformOverrideService);

            postEnrichmentService.registerEnricher(isoformOverrideSource, enricher);
        }

		for (String variant: variants)
		{
            VariantAnnotation annotation = getVariantAnnotation(variant);

            if (annotation != null)
            {
                postEnrichmentService.enrichAnnotation(annotation);
                variantAnnotations.add(annotation);
            }
		}

		return variantAnnotations;
	}

    @ApiOperation(value = "postVariantAnnotation",
        nickname = "postVariantAnnotation")
    @RequestMapping(value = "/hgvs",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<VariantAnnotation> postVariantAnnotation(
        @RequestParam
        @ApiParam(value="Comma separated list of variants. For example X:g.66937331T>A,17:g.41242962->GA",
            required = true,
            allowMultiple = true)
        List<String> variants,
        @RequestParam(required = false)
        @ApiParam(value="Isoform override source. For example uniprot",
            required = false)
        String isoformOverrideSource)
    {
       return getVariantAnnotation(variants, isoformOverrideSource);
    }

    @ApiOperation(value = "getIsoformOverride",
        nickname = "getIsoformOverride")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success",
            response = IsoformOverride.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/isoform_override/{source}/{transcriptIds:.+}",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<IsoformOverride> getIsoformOverride(
        @PathVariable
        @ApiParam(value="Override source. For example uniprot.",
            required = true)
        String source,
        @PathVariable
        @ApiParam(value="Comma separated list of transcript ids. For example ENST00000361125,ENST00000443649.",
            required = true,
            allowMultiple = true)
        List<String> transcriptIds)
    {
        List<IsoformOverride> isoformOverrides = new ArrayList<>();

        for (String id: transcriptIds)
        {
            IsoformOverride override = getIsoformOverride(source, id);

            if (override != null)
            {
                isoformOverrides.add(override);
            }
        }

        return isoformOverrides;
    }

    @ApiOperation(value = "postIsoformOverride",
        nickname = "postIsoformOverride")
    @RequestMapping(value = "/isoform_override",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<IsoformOverride> postIsoformOverride(
        @RequestParam
        @ApiParam(value="Override source. For example uniprot",
            required = true)
        String source,
        @RequestParam(required = false)
        @ApiParam(value="Comma separated list of transcript ids. For example ENST00000361125,ENST00000443649. " +
                        "If no transcript id provided, all available isoform overrides returned.",
            required = false,
            allowMultiple = true)
        List<String> transcriptIds)
    {
        if (transcriptIds != null)
        {
            return getIsoformOverride(source, transcriptIds);
        }
        else
        {
            return getIsoformOverride(source);
        }
    }

    @ApiOperation(value = "getAllIsoformOverrides",
        nickname = "getAllIsoformOverrides")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success",
            response = IsoformOverride.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/isoform_override/{source}",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<IsoformOverride> getIsoformOverride(
        @PathVariable
        @ApiParam(value="Override source. For example uniprot",
            required = true)
        String source)
    {
        return isoformOverrideService.getIsoformOverrides(source);
    }

    @ApiOperation(value = "getIsoformOverrideSources",
        nickname = "getIsoformOverrideSources")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success",
            response = IsoformOverride.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/isoform_override/sources",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<String> getIsoformOverrideSources()
    {
        return isoformOverrideService.getOverrideSources();
    }

    @ApiOperation(value = "postIsoformOverrideSources",
        nickname = "postIsoformOverrideSources")
    @RequestMapping(value = "/isoform_override/sources",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<String> postIsoformOverrideSources()
    {
        return getIsoformOverrideSources();
    }

    private IsoformOverride getIsoformOverride(String source, String transcriptId)
    {
        return isoformOverrideService.getIsoformOverride(source, transcriptId);
    }

    private VariantAnnotation getVariantAnnotation(String variant)
    {
        VariantAnnotation variantAnnotation = variantAnnotationRepository.findOne(variant);
        String annotationJSON = null;

        if (variantAnnotation == null) {

            try {
                // get the annotation from the web service and save it to the DB
                //variantAnnotation = variantAnnotationService.getAnnotation(variant);
                //variantAnnotationRepository.save(variantAnnotation);

                // get the raw annotation string from the web service
                annotationJSON = variantAnnotationService.getRawAnnotation(variant);

                // construct a VariantAnnotation instance to return:
                // this does not contain all the information obtained from the web service
                // only the fields mapped to the VariantAnnotation model will be returned
                variantAnnotation = variantAnnotationRepository.mapAnnotationJson(variant, annotationJSON);

                // save everything to the cache as a properly parsed JSON
                variantAnnotationRepository.saveAnnotationJson(variant, annotationJSON);
            }
            catch (HttpClientErrorException e) {
                // in case of web service error, do not terminate the whole process.
                // just copy the response body (error message) for this variant
                variantAnnotation = new VariantAnnotation(variant, e.getResponseBodyAsString());
            }
            catch (IOException e) {
                // in case of parse error, do not terminate the whole process.
                // just send the raw annotationJSON to the client
                variantAnnotation = new VariantAnnotation(variant, annotationJSON);
            }
        }

        return variantAnnotation;
    }
}
