/*
 * Copyright (c) 2016 Memorial Sloan-Kettering Cancer Center.
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
 * This file is part of cBioPortal Genome Nexus.
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

package org.cbioportal.genome_nexus.annotation.domain.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.cbioportal.genome_nexus.annotation.domain.VariantAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;

/**
 * @author Selcuk Onur Sumer
 */
public class VariantAnnotationRepositoryImpl implements VariantAnnotationRepositoryCustom
{
    @Autowired
    private MongoTemplate mongoTemplate;

    public final String DEFAULT_COLLECTION = "vep.annotation";

    /**
     * Parses and saves the entire content of the annotation JSON object to the database.
     *
     * @param variant           variant key (used as an id)
     * @param annotationJSON    raw annotation JSON (obtained from the service)
     */
    @Override
    public void saveAnnotationJson(String variant, String annotationJSON)
    {
        // parse the given annotation JSON string to get a proper object
        DBObject dbObject = (DBObject) JSON.parse(removeArrayBrackets(annotationJSON));

        // update the _id field to the given variant
        dbObject.put("_id", variant);

        // save the object into the correct repository
        this.mongoTemplate.save(dbObject, DEFAULT_COLLECTION);
    }

    /**
     * Maps the given raw annotation JSON string onto a VariantAnnotation instance.
     *
     * @param variant           variant key
     * @param annotationJSON    raw annotation JSON string
     * @return a VariantAnnotation instance
     * @throws IOException
     */
    @Override
    public VariantAnnotation mapAnnotationJson(String variant, String annotationJSON) throws IOException
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
