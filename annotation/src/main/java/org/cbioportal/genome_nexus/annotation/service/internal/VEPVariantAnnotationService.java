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

package org.cbioportal.genome_nexus.annotation.service.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.annotation.domain.*;
import org.cbioportal.genome_nexus.annotation.service.*;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.*;

import java.io.IOException;

/**
 * @author Benjamin Gross
 */
@Service
public class VEPVariantAnnotationService implements VariantAnnotationService
{
    private static final Log LOG = LogFactory.getLog(VEPVariantAnnotationService.class);

    private String vepURL;
    @Value("${vep.url}")
    public void setVEPURL(String vepURL) { this.vepURL = vepURL; }

    private final EnrichmentService enrichmentService;
    private final VariantAnnotationRepository variantAnnotationRepository;

    @Autowired
    public VEPVariantAnnotationService(EnrichmentService enrichmentService,
                                       VariantAnnotationRepository variantAnnotationRepository)
    {
        this.enrichmentService = enrichmentService;
        this.variantAnnotationRepository = variantAnnotationRepository;
    }

    public VariantAnnotation getAnnotation(String variant)
    {
        VariantAnnotation variantAnnotation = variantAnnotationRepository.findOne(variant);
        String annotationJSON = null;

        if (variantAnnotation == null) {

            try {
                // get the annotation from the web service and save it to the DB
                //variantAnnotation = variantAnnotationService.getAnnotation(variant);
                //variantAnnotationRepository.save(variantAnnotation);

                // get the raw annotation string from the web service
                annotationJSON = this.getRawAnnotation(variant);

                // construct a VariantAnnotation instance to return:
                // this does not contain all the information obtained from the web service
                // only the fields mapped to the VariantAnnotation model will be returned
                variantAnnotation = variantAnnotationRepository.mapAnnotationJson(variant, annotationJSON);

                // save everything to the cache as a properly parsed JSON

                try {
                    variantAnnotationRepository.saveAnnotationJson(variant, annotationJSON);
                }
                catch (DataIntegrityViolationException e) {
                    // in case of data integrity violation exception, do not bloat the logs
                    // this is thrown when the annotationJSON can't be stored by mongo
                    // due to the variant annotation key being too large to index
                    LOG.info(e.getLocalizedMessage());
                }
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

    private String getRawAnnotation(String variant)
    {
        //http://grch37.rest.ensembl.org/vep/human/hgvs/VARIANT?content-type=application/json&xref_refseq=1&ccds=1&canonical=1&domains=1&hgvs=1&numbers=1&protein=1
        String uri = vepURL.replace("VARIANT", variant);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, String.class);
    }
}
