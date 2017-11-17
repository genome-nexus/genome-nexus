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

package org.cbioportal.genome_nexus.service.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.*;
import org.cbioportal.genome_nexus.persistence.*;
import org.cbioportal.genome_nexus.persistence.internal.JsonCache;
import org.cbioportal.genome_nexus.persistence.internal.VariantAnnotationRepository;
import org.cbioportal.genome_nexus.service.*;

import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.cbioportal.genome_nexus.service.remote.VEPDataFetcher;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.beans.factory.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Benjamin Gross
 */
@Service
public class VEPVariantAnnotationService implements VariantAnnotationService
{
    private static final Log LOG = LogFactory.getLog(VEPVariantAnnotationService.class);

    @Autowired
    private EnrichmentService enrichmentService;
    private JsonCache jsonCache = new JsonCache();
    @Autowired
    private VariantAnnotationRepository variantAnnotationRepository;
    @Autowired
    private VEPDataFetcher externalResourceFetcher;

    // Lazy autowire services used for enrichment,
    // otherwise we are getting circular dependency issues
    @Lazy @Autowired
    private IsoformOverrideService isoformOverrideService;
    @Lazy @Autowired
    private HotspotService hotspotService;
    @Lazy @Autowired
    private MutationAssessorService mutationAssessorService;

    @Override
    public VariantAnnotation getAnnotation(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        return this.getVariantAnnotation(variant, null);
    }

    @Override
    public List<VariantAnnotation> getAnnotations(List<String> variants)
    {
        return this.getVariantAnnotations(variants, null);
    }

    @Override
    public VariantAnnotation getAnnotation(String variant, String isoformOverrideSource, List<String> fields)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        EnrichmentService postEnrichmentService = this.initPostEnrichmentService(isoformOverrideSource, fields);

        return this.getVariantAnnotation(variant, postEnrichmentService);
    }

    @Override
    public List<VariantAnnotation> getAnnotations(List<String> variants, String isoformOverrideSource, List<String> fields)
    {
        EnrichmentService postEnrichmentService = this.initPostEnrichmentService(isoformOverrideSource, fields);

        return this.getVariantAnnotations(variants, postEnrichmentService);
    }

    private VariantAnnotation getVariantAnnotation(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        boolean saveAnnotationJson = true;
        VariantAnnotation variantAnnotation = null;
        String annotationJSON = null;
        try {
            variantAnnotation = variantAnnotationRepository.findOne(variant);
        }
        catch (DataAccessResourceFailureException e) {
            LOG.warn("Failed to read from Mongo database - falling back on Ensembl server. Will not attempt to store variant in Mongo database.");
            saveAnnotationJson = false;
        }

        if (variantAnnotation == null)
        {
            // get the annotation from the web service and save it to the DB
            try {
                // get the raw annotation string from the web service
                annotationJSON = this.externalResourceFetcher.fetchStringValue(variant);

                // construct a VariantAnnotation instance to return:
                // this does not contain all the information obtained from the web service
                // only the fields mapped to the VariantAnnotation model will be returned
                variantAnnotation = this.mapAnnotationJson(variant, annotationJSON);

                // save everything to the cache as a properly parsed JSON
                if (saveAnnotationJson) {
                    jsonCache.save(variant, annotationJSON, "vep.annotation");
                }
            }
            catch (HttpClientErrorException e) {
                // in case of web service error, throw an exception to indicate that there is a problem with the service.
                throw new VariantAnnotationWebServiceException(variant, e.getResponseBodyAsString(), e.getStatusCode());
            }
            catch (ResourceMappingException e) {
                // TODO this only indicates that web service returns an incompatible response, but
                // this does not always mean that annotation is not found
                throw new VariantAnnotationNotFoundException(variant, annotationJSON);
            }
            catch (DataIntegrityViolationException e) {
                // in case of data integrity violation exception, do not bloat the logs
                // this is thrown when the annotationJSON can't be stored by mongo
                // due to the variant annotation key being too large to index
                LOG.info(e.getLocalizedMessage());
            }
            catch (ResourceAccessException e) {
                throw new VariantAnnotationWebServiceException(variant, e.getMessage());
            }
        }

        return variantAnnotation;
    }

    private VariantAnnotation getVariantAnnotation(String variant, EnrichmentService postEnrichmentService)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        VariantAnnotation annotation = this.getVariantAnnotation(variant);

        if (annotation != null &&
            postEnrichmentService != null)
        {
            postEnrichmentService.enrichAnnotation(annotation);
        }

        return annotation;
    }

    private List<VariantAnnotation> getVariantAnnotations(List<String> variants,
                                                          EnrichmentService postEnrichmentService)
    {
        List<VariantAnnotation> variantAnnotations = new ArrayList<>();

        for (String variant: variants)
        {
            try {
                VariantAnnotation annotation = this.getVariantAnnotation(variant, postEnrichmentService);
                variantAnnotations.add(annotation);
            } catch (VariantAnnotationNotFoundException e) {
                // fail silently for this annotation
            } catch (VariantAnnotationWebServiceException e) {
                e.printStackTrace();
            }
        }

        return variantAnnotations;
    }

    private EnrichmentService initPostEnrichmentService(String isoformOverrideSource, List<String> fields)
    {
        // The post enrichment service enriches the annotation after saving
        // the original annotation data to the repository. Any enrichment
        // performed by the post enrichment service is not saved
        // to the annotation repository.
        EnrichmentService postEnrichmentService = new VEPEnrichmentService();

        // only register the enricher if the service actually has data for the given source
        if (isoformOverrideService.hasData(isoformOverrideSource))
        {
            AnnotationEnricher enricher = new IsoformAnnotationEnricher(
                isoformOverrideSource, isoformOverrideService);

            postEnrichmentService.registerEnricher(isoformOverrideSource, enricher);
        }

        if (fields != null && fields.contains("hotspots"))
        {
            AnnotationEnricher enricher = new HotspotAnnotationEnricher(hotspotService, true);
            postEnrichmentService.registerEnricher("cancerHotspots", enricher);
        }
        if (fields != null && fields.contains("mutation_assessor"))
        {
            AnnotationEnricher enricher = new MutationAssessorAnnotationEnricher(mutationAssessorService);
            postEnrichmentService.registerEnricher("mutation_assessor", enricher);
        }

        return postEnrichmentService;
    }

    /**
     * Maps the given raw annotation JSON string onto a VariantAnnotation instance.
     *
     * @param variant           variant key
     * @param annotationJSON    raw annotation JSON string
     * @return a VariantAnnotation instance
     * @throws ResourceMappingException
     */
    private VariantAnnotation mapAnnotationJson(String variant, String annotationJSON) throws ResourceMappingException
    {
        // map annotation string onto VariantAnnotation instance
        List<VariantAnnotation> list = this.externalResourceFetcher.getTransformer().transform(
            annotationJSON, VariantAnnotation.class);

        // assuming annotationJSON contains only a single variant.
        // get the first one, ignore the rest...
        VariantAnnotation vepVariantAnnotation = list.get(0);

        // include original variant value too
        vepVariantAnnotation.setVariant(variant);
        //vepVariantAnnotation.setAnnotationJSON(annotationJSON);

        return vepVariantAnnotation;
    }
}
