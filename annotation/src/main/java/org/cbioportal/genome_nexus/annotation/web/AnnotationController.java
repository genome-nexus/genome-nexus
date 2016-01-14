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
import org.cbioportal.genome_nexus.annotation.service.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Benjamin Gross
 */
@RestController // shorthand for @Controller, @ResponseBody
@RequestMapping(value = "/variant_annotation/")
public class AnnotationController
{
    private final VariantAnnotationService variantAnnotationService;
    private final VariantAnnotationRepository variantAnnotationRepository;

    @Autowired
    public AnnotationController(VariantAnnotationService variantAnnotationService,
                                VariantAnnotationRepository variantAnnotationRepository)
    {
        this.variantAnnotationService = variantAnnotationService;
        this.variantAnnotationRepository = variantAnnotationRepository;
    }

    @ApiOperation(value = "getVariantAnnotation",
        nickname = "getVariantAnnotation")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(name = "variants",
            value = "Comma separated list of variants. Example: 10:g.152595854G>A,10:g.152595854G>C",
            required = true,
            dataType = "string",
            paramType = "path")
    })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success",
            response = VariantAnnotation.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request")
    })
	@RequestMapping(value = "/hgvs/{variants:.+}",
        method = RequestMethod.GET,
        produces = "application/json")
	public List<VariantAnnotation> getVariantAnnotation(@PathVariable List<String> variants)
	{
		List<VariantAnnotation> variantAnnotations = new ArrayList<>();

		for (String variant: variants)
		{
			variantAnnotations.add(getVariantAnnotation(variant));
		}

		return variantAnnotations;
	}

    //@RequestMapping(value = "/hgvs/{variant:.+}", method = RequestMethod.GET)
    private VariantAnnotation getVariantAnnotation(String variant)
    {
        VariantAnnotation variantAnnotation = variantAnnotationRepository.findOne(variant);

        if (variantAnnotation == null) {
            variantAnnotation = variantAnnotationService.getAnnotation(variant);
            variantAnnotationRepository.save(variantAnnotation);
        }
        return variantAnnotation;
    }
}
