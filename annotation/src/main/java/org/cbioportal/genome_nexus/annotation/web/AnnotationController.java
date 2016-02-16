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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import io.swagger.annotations.*;
import org.cbioportal.genome_nexus.annotation.domain.*;
import org.cbioportal.genome_nexus.annotation.service.*;

import org.springframework.data.mongodb.core.MongoTemplate;
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
    private final MongoTemplate mongoTemplate;

    @Autowired
    public AnnotationController(VariantAnnotationService variantAnnotationService,
                                VariantAnnotationRepository variantAnnotationRepository,
                                MongoTemplate mongoTemplate)
    {
        this.variantAnnotationService = variantAnnotationService;
        this.variantAnnotationRepository = variantAnnotationRepository;
        this.mongoTemplate = mongoTemplate;
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
        List<String> variants)
	{
		List<VariantAnnotation> variantAnnotations = new ArrayList<>();

		for (String variant: variants)
		{
			variantAnnotations.add(getVariantAnnotation(variant));
		}

		return variantAnnotations;
	}

    @ApiOperation(value = "postVariantAnnotation",
        nickname = "postVariantAnnotation")
    @RequestMapping(value = "/hgvs",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<VariantAnnotation> postVariantAnnotation(
        @RequestBody
        @ApiParam(name="variants",
            value="Comma separated list of variants. For example X:g.66937331T>A,17:g.41242962->GA",
            required = true,
            allowMultiple = true)
        String variants)
    {
        List<VariantAnnotation> variantAnnotations = new ArrayList<>();

        // assuming the response body is comma separated list of variants
        for (String variant: variants.split(","))
        {
            variantAnnotations.add(getVariantAnnotation(variant));
        }

        return variantAnnotations;
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
                variantAnnotation = mapAnnotationJson(variant, annotationJSON);

                // save everything to the cache as a properly parsed JSON
                saveToDB(variant, annotationJSON);
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

    // TODO move this method into VariantAnnotationRepositoryImpl class if possible
	/**
     * Parses and saves the entire content of the annotation JSON object to the database.
     *
     * @param variant           variant key (used as an id)
     * @param annotationJSON    raw annotation JSON (obtained from the service)
     */
    private void saveToDB(String variant, String annotationJSON)
    {
        // parse the given annotation JSON string to get a proper object
        DBObject dbObject = (DBObject)JSON.parse(removeArrayBrackets(annotationJSON));

        // update the _id field to the given variable
        dbObject.put("_id", variant);

        // save the object into the correct repository
        this.mongoTemplate.save(dbObject, "vep.annotation");
    }

	/**
     * Maps the given raw annotation JSON string onto a VariantAnnotation instance.
     *
     * @param variant           variant key
     * @param annotationJSON    raw annotation JSON string
     * @return a VariantAnnotation instance
     * @throws IOException
     */
    private VariantAnnotation mapAnnotationJson(String variant, String annotationJSON) throws IOException
    {
        String toSerialize = removeArrayBrackets(annotationJSON);

        VariantAnnotation vepVariantAnnotation;
        ObjectMapper mapper = new ObjectMapper();

        // map annotation string onto VariantAnnotation instance
        vepVariantAnnotation = mapper.readValue(toSerialize, VariantAnnotation.class);
        // include original variant value too
        vepVariantAnnotation.setVariant(variant);
        //vepVariantAnnotation.setAnnotationJSON(annotationJSON);

        return vepVariantAnnotation;
    }

    private String removeArrayBrackets(String annotationJSON)
    {
        String stripped = annotationJSON;

        // TODO this is not safe. a better way: parse and if array then get the first element
        // if annotation JSON is actually an array, assume it is an array of size 1,
        // and convert it to a single object by removing array brackets
        if (annotationJSON.trim().startsWith("["))
        {
            int start = annotationJSON.indexOf("[");
            int end = annotationJSON.lastIndexOf("]");
            stripped = annotationJSON.substring(start+1, end);
        }

        return stripped;
    }
}
