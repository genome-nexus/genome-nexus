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

import org.cbioportal.genome_nexus.service.cached.BaseCachedExternalResourceFetcher;
import org.cbioportal.genome_nexus.service.enricher.*;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseVariantAnnotationServiceImpl implements VariantAnnotationService
{
    private static final Log LOG = LogFactory.getLog(BaseVariantAnnotationServiceImpl.class);

    private final BaseCachedExternalResourceFetcher<VariantAnnotation, VariantAnnotationRepository> resourceFetcher;
    private final IsoformOverrideService isoformOverrideService;
    private final CancerHotspotService hotspotService;
    private final MutationAssessorService mutationAssessorService;
    private final VariantAnnotationSummaryService variantAnnotationSummaryService;
    private final MyVariantInfoService myVariantInfoService;
    private final NucleotideContextService nucleotideContextService;
    private final PostTranslationalModificationService postTranslationalModificationService;
    private final SignalMutationService signalMutationService;
    private final OncokbService oncokbService;

    public BaseVariantAnnotationServiceImpl(BaseCachedExternalResourceFetcher<VariantAnnotation, VariantAnnotationRepository> resourceFetcher,
                                            IsoformOverrideService isoformOverrideService,
                                            CancerHotspotService hotspotService,
                                            MutationAssessorService mutationAssessorService,
                                            MyVariantInfoService myVariantInfoService,
                                            NucleotideContextService nucleotideContextService,
                                            VariantAnnotationSummaryService variantAnnotationSummaryService,
                                            PostTranslationalModificationService postTranslationalModificationService,
                                            SignalMutationService signalMutationService,
                                            OncokbService oncokbService)
    {
        this.resourceFetcher = resourceFetcher;
        this.isoformOverrideService = isoformOverrideService;
        this.hotspotService = hotspotService;
        this.mutationAssessorService = mutationAssessorService;
        this.nucleotideContextService = nucleotideContextService;
        this.variantAnnotationSummaryService = variantAnnotationSummaryService;
        this.myVariantInfoService = myVariantInfoService;
        this.postTranslationalModificationService = postTranslationalModificationService;
        this.signalMutationService = signalMutationService;
        this.oncokbService = oncokbService;
    }

    // Needs to be overridden to support normalizing variants
    protected String normalizeVariant(String id)
    {
        return id;
    }

    @Override
    public VariantAnnotation getAnnotation(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        return this.getVariantAnnotation(this.normalizeVariant(variant), null);
    }

    @Override
    public List<VariantAnnotation> getAnnotations(List<String> variants)
    {
        return this.getVariantAnnotations(
            variants,
            null
        );
    }

    @Override
    public VariantAnnotation getAnnotation(String variant, String isoformOverrideSource, Map<String, String> token, List<String> fields)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        EnrichmentService postEnrichmentService = this.initPostEnrichmentService(isoformOverrideSource, fields, token);

        return this.getVariantAnnotation(variant, postEnrichmentService);
    }

    @Override
    public List<VariantAnnotation> getAnnotations(List<String> variants, String isoformOverrideSource, Map<String, String> token, List<String> fields)
    {
        EnrichmentService postEnrichmentService = this.initPostEnrichmentService(isoformOverrideSource, fields, token);

        return this.getVariantAnnotations(
            variants,
            postEnrichmentService
        );
    }

    private VariantAnnotation getVariantAnnotationExternally(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        Optional<VariantAnnotation> variantAnnotation = null;

        String normalizedVariant = normalizeVariant(variant);

        try {
            // get the annotation from the web service and save it to the DB
            variantAnnotation = Optional.ofNullable(this.resourceFetcher.fetchAndCache(normalizedVariant));

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

    private List<VariantAnnotation> getVariantAnnotationsExternally(List<String> variants)
            throws VariantAnnotationWebServiceException {
        List<VariantAnnotation> variantAnnotations = null;
        Map<String, String> normVarToOrigVarQueryMap = new LinkedHashMap<>();
        variants.forEach((variant) -> {
            normVarToOrigVarQueryMap.put(this.normalizeVariant(variant), variant);
        });

        try {
            // get the annotations from the web service and save it to the DB
            variantAnnotations = this.resourceFetcher.fetchAndCache(new ArrayList(normVarToOrigVarQueryMap.keySet()));
            for (VariantAnnotation variantAnnotation : variantAnnotations) {
                variantAnnotation.setOriginalVariantQuery(normVarToOrigVarQueryMap.get(variantAnnotation.getVariant()));
            }
        } catch (HttpClientErrorException e) {
            // in case of web service error, throw an exception to indicate that there is a
            // problem with the service.
            throw new VariantAnnotationWebServiceException(normVarToOrigVarQueryMap.keySet().toString(), e.getResponseBodyAsString(),
                    e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new VariantAnnotationWebServiceException(normVarToOrigVarQueryMap.keySet().toString(), e.getMessage());
        } catch (ResourceMappingException e) {
            // TODO this indicates that web service returns an incompatible response
        }

        return variantAnnotations;
    }

    private VariantAnnotation getVariantAnnotation(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        return getVariantAnnotationExternally(variant);
    }

    private List<VariantAnnotation> getVariantAnnotations(List<String> variants)
            throws VariantAnnotationWebServiceException {
        return getVariantAnnotationsExternally(variants);
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
                postEnrichmentService.enrichAnnotations(variantAnnotations);
            }
        } catch (VariantAnnotationWebServiceException e) {
            LOG.warn(e.getLocalizedMessage(), e);
        }

        return variantAnnotations;
    }


    private EnrichmentService initPostEnrichmentService(String isoformOverrideSource, List<String> fields, Map<String, String> token)
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
                isoformOverrideSource, isoformOverrideSource, isoformOverrideService);

            postEnrichmentService.registerEnricher(enricher);
        }

        if (fields != null && fields.contains("hotspots"))
        {
            AnnotationEnricher enricher = new HotspotAnnotationEnricher(
                "cancerHotspots", hotspotService, true);
            postEnrichmentService.registerEnricher(enricher);
        }

        if (fields != null && fields.contains("mutation_assessor"))
        {
            AnnotationEnricher enricher = new MutationAssessorAnnotationEnricher(
                "mutation_assessor", mutationAssessorService);
            postEnrichmentService.registerEnricher(enricher);
        }

        if (fields != null && fields.contains("nucleotide_context"))
        {
            AnnotationEnricher enricher = new NucleotideContextAnnotationEnricher(
                "nucleotide_context", nucleotideContextService);
            postEnrichmentService.registerEnricher(enricher);
        }

        if (fields != null && fields.contains("my_variant_info"))
        {
            AnnotationEnricher enricher = new MyVariantInfoAnnotationEnricher(
                "my_variant_info", myVariantInfoService);
            postEnrichmentService.registerEnricher(enricher);
        }

        if (fields != null && fields.contains("ptms"))
        {
            AnnotationEnricher enricher = new PostTranslationalModificationEnricher(
                "ptm", postTranslationalModificationService);
            postEnrichmentService.registerEnricher(enricher);
        }

        if (fields != null && fields.contains("signal"))
        {
            AnnotationEnricher enricher = new SignalAnnotationEnricher(
                "signal", signalMutationService);
            postEnrichmentService.registerEnricher(enricher);
        }

        if (fields != null && fields.contains("oncokb"))
        {
            String oncokbToken = null;
            if (token != null && token.containsKey("oncokb")) {
                oncokbToken = token.get("oncokb");
            }
            AnnotationEnricher enricher = new OncokbAnnotationEnricher(
                "oncokb",
                oncokbService,
                variantAnnotationSummaryService,
                oncokbToken
            );
            postEnrichmentService.registerEnricher(enricher);
        }

        if (fields != null && fields.contains("annotation_summary"))
        {
            AnnotationEnricher enricher = new CanonicalTranscriptAnnotationEnricher(
                "annotation_summary", variantAnnotationSummaryService);
            postEnrichmentService.registerEnricher(enricher);
        }

        return postEnrichmentService;
    }
}
