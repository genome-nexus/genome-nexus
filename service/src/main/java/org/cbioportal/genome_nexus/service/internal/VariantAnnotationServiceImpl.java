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
import org.cbioportal.genome_nexus.service.*;

import org.cbioportal.genome_nexus.service.annotation.NotationConverter;
import org.cbioportal.genome_nexus.service.cached.CachedVariantAnnotationFetcher;
import org.cbioportal.genome_nexus.service.cached.CachedVariantIdAnnotationFetcher;
import org.cbioportal.genome_nexus.service.enricher.CanonicalTranscriptAnnotationEnricher;
import org.cbioportal.genome_nexus.service.enricher.HotspotAnnotationEnricher;
import org.cbioportal.genome_nexus.service.enricher.IsoformAnnotationEnricher;
import org.cbioportal.genome_nexus.service.enricher.MutationAssessorAnnotationEnricher;
import org.cbioportal.genome_nexus.service.enricher.MyVariantInfoAnnotationEnricher;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.beans.factory.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Benjamin Gross
 */
@Service
public class VariantAnnotationServiceImpl implements VariantAnnotationService
{
    private static final Log LOG = LogFactory.getLog(VariantAnnotationServiceImpl.class);

    private final CachedVariantAnnotationFetcher cachedExternalResourceFetcher;
    private final CachedVariantIdAnnotationFetcher cachedVariantIdAnnotationFetcher;
    private final NotationConverter notationConverter;
    private final IsoformOverrideService isoformOverrideService;
    private final CancerHotspotService hotspotService;
    private final MutationAssessorService mutationAssessorService;
    private final VariantAnnotationSummaryService variantAnnotationSummaryService;
    private final MyVariantInfoService myVariantInfoService;

    @Autowired
    public VariantAnnotationServiceImpl(CachedVariantAnnotationFetcher cachedExternalResourceFetcher,
                                        CachedVariantIdAnnotationFetcher cachedVariantIdAnnotationFetcher,
                                        NotationConverter notationConverter,
                                        // Lazy autowire services used for enrichment,
                                        // otherwise we are getting circular dependency issues
                                        @Lazy IsoformOverrideService isoformOverrideService,
                                        @Lazy CancerHotspotService hotspotService,
                                        @Lazy MutationAssessorService mutationAssessorService,
                                        @Lazy MyVariantInfoService myVariantInfoService,
                                        @Lazy VariantAnnotationSummaryService variantAnnotationSummaryService)
    {
        this.cachedExternalResourceFetcher = cachedExternalResourceFetcher;
        this.cachedVariantIdAnnotationFetcher = cachedVariantIdAnnotationFetcher;
        this.notationConverter = notationConverter;
        this.isoformOverrideService = isoformOverrideService;
        this.hotspotService = hotspotService;
        this.mutationAssessorService = mutationAssessorService;
        this.variantAnnotationSummaryService = variantAnnotationSummaryService;
        this.myVariantInfoService = myVariantInfoService;
    }

    @Override
    public VariantAnnotation getAnnotation(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        return this.getVariantAnnotation(notationConverter.hgvsNormalizer(variant), null);
    }

    @Override
    public List<VariantAnnotation> getAnnotations(List<String> variants)
    {
        return this.getVariantAnnotations(
            variants.stream().map(v -> notationConverter.hgvsNormalizer(v)).collect(Collectors.toList()),
            null
        );
    }

    @Override
    public VariantAnnotation getAnnotation(String variant, String isoformOverrideSource, List<String> fields)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        EnrichmentService postEnrichmentService = this.initPostEnrichmentService(isoformOverrideSource, fields);

        return this.getVariantAnnotation(notationConverter.hgvsNormalizer(variant), postEnrichmentService);
    }

    @Override
    public List<VariantAnnotation> getAnnotations(List<String> variants, String isoformOverrideSource, List<String> fields)
    {
        EnrichmentService postEnrichmentService = this.initPostEnrichmentService(isoformOverrideSource, fields);

        return this.getVariantAnnotations(
            variants.stream().map(v -> notationConverter.hgvsNormalizer(v)).collect(Collectors.toList()),
            postEnrichmentService
        );
    }

    @Override
    public VariantAnnotation getAnnotation(GenomicLocation genomicLocation)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        return this.getAnnotation(this.notationConverter.genomicToHgvs(genomicLocation));
    }

    @Override
    public VariantAnnotation getAnnotationByGenomicLocation(String genomicLocation)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        return this.getAnnotation(this.notationConverter.parseGenomicLocation(genomicLocation));
    }

    @Override
    public List<VariantAnnotation> getAnnotationsByGenomicLocations(List<GenomicLocation> genomicLocations)
    {
        return this.getAnnotations(this.notationConverter.genomicToHgvs(genomicLocations));
    }

    @Override
    public VariantAnnotation getAnnotationByGenomicLocation(String genomicLocation,
                                                            String isoformOverrideSource,
                                                            List<String> fields)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        return this.getAnnotation(this.notationConverter.genomicToHgvs(genomicLocation),
            isoformOverrideSource,
            fields);
    }

    @Override
    public List<VariantAnnotation> getAnnotationsByGenomicLocations(List<GenomicLocation> genomicLocations,
                                                                    String isoformOverrideSource,
                                                                    List<String> fields)
    {
        return this.getAnnotations(this.notationConverter.genomicToHgvs(genomicLocations),
            isoformOverrideSource,
            fields);
    }

    @Override
    public VariantAnnotation getAnnotationById(String variantId)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        return this.getVariantAnnotationById(variantId);
    }

    @Override
    public List<VariantAnnotation> getAnnotationsByIds(List<String> variantIds)
    {
        return this.getVariantAnnotationsByIds(variantIds, null);
    }

    @Override
    public VariantAnnotation getAnnotationById(String variantId, String isoformOverrideSource, List<String> fields)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        EnrichmentService postEnrichmentService = this.initPostEnrichmentService(isoformOverrideSource, fields);

        return this.getVariantAnnotationById(variantId, postEnrichmentService);
    }

    @Override
    public List<VariantAnnotation> getAnnotationsByIds(List<String> variantIds, String isoformOverrideSource, List<String> fields)
    {
        EnrichmentService postEnrichmentService = this.initPostEnrichmentService(isoformOverrideSource, fields);

        return this.getVariantAnnotations(variantIds, postEnrichmentService);
    }

    private VariantAnnotation getVariantAnnotation(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        Optional<VariantAnnotation> variantAnnotation = null;

        try {
            // get the annotation from the web service and save it to the DB
            variantAnnotation = Optional.of(cachedExternalResourceFetcher.fetchAndCache(variant));

            // include original variant value too
            variantAnnotation.ifPresent(x -> x.setVariant(variant));
        }
        catch (HttpClientErrorException e) {
            // in case of web service error, throw an exception to indicate that there is a problem with the service.
            throw new VariantAnnotationWebServiceException(variant, e.getResponseBodyAsString(), e.getStatusCode());
        }
        catch (ResourceMappingException e) {
            // TODO this only indicates that web service returns an incompatible response, but
            // this does not always mean that annotation is not found
            throw new VariantAnnotationNotFoundException(variant);
        }
        catch (ResourceAccessException e) {
            throw new VariantAnnotationWebServiceException(variant, e.getMessage());
        }

        try {
            return variantAnnotation.get();
        } catch (NoSuchElementException e) {
            throw new VariantAnnotationNotFoundException(variant);
        }
    }

    private List<VariantAnnotation> getVariantAnnotations(List<String> variants)
            throws VariantAnnotationWebServiceException {
        List<VariantAnnotation> variantAnnotations = null;

        try {
            // get the annotations from the web service and save it to the DB
            variantAnnotations = cachedExternalResourceFetcher.fetchAndCache(variants);
        } catch (HttpClientErrorException e) {
            // in case of web service error, throw an exception to indicate that there is a
            // problem with the service.
            throw new VariantAnnotationWebServiceException(variants.toString(), e.getResponseBodyAsString(),
                    e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new VariantAnnotationWebServiceException(variants.toString(), e.getMessage());
        } catch (ResourceMappingException e) {
            // TODO this indicates that web service returns an incompatible response
        }

        return variantAnnotations;
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
        List<VariantAnnotation> variantAnnotations = Collections.emptyList();

        try {
            // fetch all annotations at once
            variantAnnotations = this.getVariantAnnotations(variants);

            if (postEnrichmentService != null) {
                for (VariantAnnotation annotation: variantAnnotations) {
                    postEnrichmentService.enrichAnnotation(annotation);
                }
            }
        } catch (VariantAnnotationWebServiceException e) {
            LOG.warn(e.getLocalizedMessage());
        }

        return variantAnnotations;
    }

    private VariantAnnotation getVariantAnnotationById(String variantId)
            throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        Optional<VariantAnnotation> variantAnnotation = null;
        try {
            // get the annotation from the web service and save it to the DB
            variantAnnotation = Optional.of(cachedVariantIdAnnotationFetcher.fetchAndCache(variantId));

            // include original variant value too
            variantAnnotation.ifPresent(x -> x.setVariant(variantId));
        } catch (HttpClientErrorException e) {
            // in case of web service error, throw an exception to indicate that there is a
            // problem with the service.
            throw new VariantAnnotationWebServiceException(variantId, e.getResponseBodyAsString(), e.getStatusCode());
        } catch (ResourceMappingException e) {
            // TODO this only indicates that web service returns an incompatible response,
            // but
            // this does not always mean that annotation is not found
            throw new VariantAnnotationNotFoundException(variantId);
        } catch (ResourceAccessException e) {
            throw new VariantAnnotationWebServiceException(variantId, e.getMessage());
        }

        try {
            return variantAnnotation.get();
        } catch (NoSuchElementException e) {
            throw new VariantAnnotationNotFoundException(variantId);
        }
    }

    private List<VariantAnnotation> getVariantAnnotationsByIds(List<String> variantIds)
            throws VariantAnnotationWebServiceException
    {
        List<VariantAnnotation> variantAnnotations = null;

        try {
            // get the annotations from the web service and save it to the DB
            variantAnnotations = cachedVariantIdAnnotationFetcher.fetchAndCache(variantIds);
        } catch (HttpClientErrorException e) {
            // in case of web service error, throw an exception to indicate that there is a
            // problem with the service.
            throw new VariantAnnotationWebServiceException(variantIds.toString(), e.getResponseBodyAsString(),
                    e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new VariantAnnotationWebServiceException(variantIds.toString(), e.getMessage());
        } catch (ResourceMappingException e) {
            // TODO this indicates that web service returns an incompatible response
        }

        return variantAnnotations;
    }

    private VariantAnnotation getVariantAnnotationById(String variantId, EnrichmentService postEnrichmentService)
            throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        VariantAnnotation annotation = this.getVariantAnnotationById(variantId);

        if (annotation != null && postEnrichmentService != null) {
            postEnrichmentService.enrichAnnotation(annotation);
        }

        return annotation;
    }

    private List<VariantAnnotation> getVariantAnnotationsByIds(List<String> variantIds, EnrichmentService postEnrichmentService)
    {
        List<VariantAnnotation> variantAnnotations = Collections.emptyList();

        try {
            // fetch all annotations at once
            variantAnnotations = this.getVariantAnnotationsByIds(variantIds);

            if (postEnrichmentService != null) {
                for (VariantAnnotation annotation : variantAnnotations) {
                    postEnrichmentService.enrichAnnotation(annotation);
                }
            }
        } catch (VariantAnnotationWebServiceException e) {
            LOG.warn(e.getLocalizedMessage());
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

        if (fields != null && fields.contains("my_variant_info"))
        {
            AnnotationEnricher enricher = new MyVariantInfoAnnotationEnricher(myVariantInfoService);
            postEnrichmentService.registerEnricher("my_variant_info", enricher);
        }

        if (fields != null && fields.contains("annotation_summary"))
        {
            AnnotationEnricher enricher = new CanonicalTranscriptAnnotationEnricher(variantAnnotationSummaryService);
            postEnrichmentService.registerEnricher("annotation_summary", enricher);
        }

        return postEnrichmentService;
    }
}
