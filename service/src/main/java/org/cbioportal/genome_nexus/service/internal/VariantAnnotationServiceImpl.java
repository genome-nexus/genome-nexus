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
import org.cbioportal.genome_nexus.persistence.VariantAnnotationRepository;
import org.cbioportal.genome_nexus.service.*;

import org.cbioportal.genome_nexus.service.annotation.NotationConverter;
import org.cbioportal.genome_nexus.service.annotation.VariantAnnotationInputFormat;
import org.cbioportal.genome_nexus.service.cached.BaseCachedExternalResourceFetcher;
import org.cbioportal.genome_nexus.service.cached.CachedVariantAnnotationFetcher;
import org.cbioportal.genome_nexus.service.cached.CachedVariantIdAnnotationFetcher;
import org.cbioportal.genome_nexus.service.cached.CachedVariantRegionAnnotationFetcher;
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
    private final CachedVariantRegionAnnotationFetcher cachedVariantRegionAnnotationFetcher;
    private final NotationConverter notationConverter;
    private final IsoformOverrideService isoformOverrideService;
    private final CancerHotspotService hotspotService;
    private final MutationAssessorService mutationAssessorService;
    private final VariantAnnotationSummaryService variantAnnotationSummaryService;
    private final MyVariantInfoService myVariantInfoService;

    @Autowired
    public VariantAnnotationServiceImpl(CachedVariantAnnotationFetcher cachedExternalResourceFetcher,
                                        CachedVariantIdAnnotationFetcher cachedVariantIdAnnotationFetcher,
                                        CachedVariantRegionAnnotationFetcher cachedVariantRegionAnnotationFetcher,
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
        this.cachedVariantRegionAnnotationFetcher = cachedVariantRegionAnnotationFetcher;
        this.notationConverter = notationConverter;
        this.isoformOverrideService = isoformOverrideService;
        this.hotspotService = hotspotService;
        this.mutationAssessorService = mutationAssessorService;
        this.variantAnnotationSummaryService = variantAnnotationSummaryService;
        this.myVariantInfoService = myVariantInfoService;
    }

    @Override
    public VariantAnnotation getAnnotation(String variant, VariantAnnotationInputFormat format)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        return this.getVariantAnnotation(notationConverter.hgvsNormalizer(variant), format, null);
    }

    @Override
    public List<VariantAnnotation> getAnnotations(List<String> variants, VariantAnnotationInputFormat format)
    {
        return this.getVariantAnnotations(
            variants,
            format,
            null
        );
    }

    @Override
    public VariantAnnotation getAnnotation(String variant, VariantAnnotationInputFormat format, String isoformOverrideSource, List<String> fields)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        EnrichmentService postEnrichmentService = this.initPostEnrichmentService(isoformOverrideSource, fields);

        return this.getVariantAnnotation(variant, format, postEnrichmentService);
    }

    @Override
    public List<VariantAnnotation> getAnnotations(List<String> variants, VariantAnnotationInputFormat format, String isoformOverrideSource, List<String> fields)
    {
        EnrichmentService postEnrichmentService = this.initPostEnrichmentService(isoformOverrideSource, fields);

        return this.getVariantAnnotations(
            variants,
            format,
            postEnrichmentService
        );
    }

    @Override
    public VariantAnnotation getAnnotation(GenomicLocation genomicLocation)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        return this.getAnnotation(this.notationConverter.genomicToEnsemblRestRegion(genomicLocation), VariantAnnotationInputFormat.REGION);
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
        return this.getAnnotations(this.notationConverter.genomicToEnsemblRestRegion(genomicLocations), VariantAnnotationInputFormat.REGION);
    }

    @Override
    public VariantAnnotation getAnnotationByGenomicLocation(String genomicLocation,
                                                            String isoformOverrideSource,
                                                            List<String> fields)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        return this.getAnnotation(this.notationConverter.genomicToEnsemblRestRegion(genomicLocation),
            VariantAnnotationInputFormat.REGION,
            isoformOverrideSource,
            fields);
    }

    @Override
    public List<VariantAnnotation> getAnnotationsByGenomicLocations(List<GenomicLocation> genomicLocations,
                                                                    String isoformOverrideSource,
                                                                    List<String> fields)
    {
        return this.getAnnotations(this.notationConverter.genomicToEnsemblRestRegion(genomicLocations),
            VariantAnnotationInputFormat.REGION,
            isoformOverrideSource,
            fields);
    }

    private BaseCachedExternalResourceFetcher<VariantAnnotation, VariantAnnotationRepository> getExternalResourceFetcher(VariantAnnotationInputFormat format) {
        BaseCachedExternalResourceFetcher<VariantAnnotation, VariantAnnotationRepository> resourceFetcher;

        switch (format) {
            case HGVS:
                resourceFetcher = this.cachedExternalResourceFetcher;
                break;
            case DBSNP:
                resourceFetcher = this.cachedVariantIdAnnotationFetcher;
                break;
            case REGION:
                resourceFetcher = this.cachedVariantRegionAnnotationFetcher;
                break;
            default:
                // this should never happen
                resourceFetcher = null;
                break;
        }

        return resourceFetcher;
    }

    private String normalizeVariant(String variant, VariantAnnotationInputFormat format) {
        String normalizedVariant;

        switch (format) {
            case HGVS:
                normalizedVariant = notationConverter.hgvsNormalizer(variant);
                break;
            default:
                normalizedVariant = variant;
                break;
        }

        return normalizedVariant;
    }

    private VariantAnnotation getVariantAnnotationExternally(String variant, VariantAnnotationInputFormat format)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        Optional<VariantAnnotation> variantAnnotation = null;

        BaseCachedExternalResourceFetcher<VariantAnnotation, VariantAnnotationRepository> resourceFetcher = getExternalResourceFetcher(format);
        String normalizedVariant = normalizeVariant(variant, format);

        try {
            // get the annotation from the web service and save it to the DB
            variantAnnotation = Optional.ofNullable(resourceFetcher.fetchAndCache(normalizedVariant));

            // include original variant value too
            variantAnnotation.ifPresent(x -> x.setVariant(normalizedVariant));
        }
        catch (HttpClientErrorException e) {
            // in case of web service error, throw an exception to indicate that there is a problem with the service.
            throw new VariantAnnotationWebServiceException(normalizedVariant, e.getResponseBodyAsString(), e.getStatusCode());
        }
        catch (ResourceMappingException e) {
            // TODO this only indicates that web service returns an incompatible response, but
            // this does not always mean that annotation is not found
            throw new VariantAnnotationNotFoundException(normalizedVariant);
        }
        catch (ResourceAccessException e) {
            throw new VariantAnnotationWebServiceException(normalizedVariant, e.getMessage());
        }

        try {
            return variantAnnotation.get();
        } catch (NoSuchElementException e) {
            throw new VariantAnnotationNotFoundException(normalizedVariant);
        }
    }

    private List<VariantAnnotation> getVariantAnnotationsExternally(List<String> variants, VariantAnnotationInputFormat format)
            throws VariantAnnotationWebServiceException {
        List<VariantAnnotation> variantAnnotations = null;

        BaseCachedExternalResourceFetcher<VariantAnnotation, VariantAnnotationRepository> resourceFetcher = getExternalResourceFetcher(format);
        List<String> normalizedVariants = variants.stream().map(v -> notationConverter.hgvsNormalizer(v)).collect(Collectors.toList());

        try {
            // get the annotations from the web service and save it to the DB
            variantAnnotations = resourceFetcher.fetchAndCache(normalizedVariants);
        } catch (HttpClientErrorException e) {
            // in case of web service error, throw an exception to indicate that there is a
            // problem with the service.
            throw new VariantAnnotationWebServiceException(normalizedVariants.toString(), e.getResponseBodyAsString(),
                    e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new VariantAnnotationWebServiceException(normalizedVariants.toString(), e.getMessage());
        } catch (ResourceMappingException e) {
            // TODO this indicates that web service returns an incompatible response
        }

        return variantAnnotations;
    }

    private VariantAnnotation getVariantAnnotation(String variant, VariantAnnotationInputFormat format)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        return getVariantAnnotationExternally(variant, format);
    }

    private List<VariantAnnotation> getVariantAnnotations(List<String> variants, VariantAnnotationInputFormat format)
            throws VariantAnnotationWebServiceException {
        return getVariantAnnotationsExternally(variants, format);
    }

    private VariantAnnotation getVariantAnnotation(String variant, VariantAnnotationInputFormat format, EnrichmentService postEnrichmentService)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        VariantAnnotation annotation = this.getVariantAnnotation(variant, format);

        if (annotation != null &&
            postEnrichmentService != null)
        {
            postEnrichmentService.enrichAnnotation(annotation);
        }

        return annotation;
    }

    private List<VariantAnnotation> getVariantAnnotations(List<String> variants,
                                                          VariantAnnotationInputFormat format,
                                                          EnrichmentService postEnrichmentService)
    {
        List<VariantAnnotation> variantAnnotations = Collections.emptyList();

        try {
            // fetch all annotations at once
            variantAnnotations = this.getVariantAnnotations(variants, format);

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
