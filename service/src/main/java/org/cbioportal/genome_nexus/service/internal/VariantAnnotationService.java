/*
 * Copyright (c) 2015 - 2021 Memorial Sloan-Kettering Cancer Center.
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
import org.cbioportal.genome_nexus.component.annotation.HugoGeneSymbolResolver;
import org.cbioportal.genome_nexus.component.annotation.IndexBuilder;
import org.cbioportal.genome_nexus.component.annotation.NotationConverter;
import org.cbioportal.genome_nexus.component.annotation.ProteinChangeResolver;
import org.cbioportal.genome_nexus.model.*;
import org.cbioportal.genome_nexus.persistence.IndexRepository;
import org.cbioportal.genome_nexus.service.*;

import org.cbioportal.genome_nexus.service.cached.CachedVariantAnnotationFetcher;
import org.cbioportal.genome_nexus.service.cached.CachedVariantIdAnnotationFetcher;
import org.cbioportal.genome_nexus.service.enricher.*;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.cbioportal.genome_nexus.service.factory.IsoformAnnotationEnricherFactory;
import org.cbioportal.genome_nexus.util.GenomicLocationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Service
public class VariantAnnotationService
{
    private static final Log LOG = LogFactory.getLog(VariantAnnotationService.class);

    private final CachedVariantAnnotationFetcher hgvsAndGenomicLocationFetcher;
    private final CachedVariantIdAnnotationFetcher dbsnpFetcher;
    private final CancerHotspotService hotspotService;
    private final MutationAssessorService mutationAssessorService;
    private final VariantAnnotationSummaryService variantAnnotationSummaryService;
    private final MyVariantInfoService myVariantInfoService;
    private final NucleotideContextService nucleotideContextService;
    private final PostTranslationalModificationService postTranslationalModificationService;
    private final SignalMutationService signalMutationService;
    private final OncokbService oncokbService;
    private final ClinvarVariantAnnotationService clinvarVariantAnnotationService;
    private final IndexRepository indexRepository;
    private final ProteinChangeResolver proteinChangeResolver;
    private final HugoGeneSymbolResolver hugoGeneSymbolResolver;
    private final NotationConverter notationConverter;
    private final IsoformAnnotationEnricherFactory enricherFactory;
    @Value("${cache.enabled:true}")
    private boolean cacheEnabled;

    public VariantAnnotationService(
        CachedVariantAnnotationFetcher cachedVariantAnnotationFetcher,
        CachedVariantIdAnnotationFetcher cachedVariantIdAnnotationFetcher,
        @Lazy CancerHotspotService hotspotService,
        @Lazy MutationAssessorService mutationAssessorService,
        MyVariantInfoService myVariantInfoService,
        @Lazy NucleotideContextService nucleotideContextService,
        @Lazy VariantAnnotationSummaryService variantAnnotationSummaryService,
        PostTranslationalModificationService postTranslationalModificationService,
        SignalMutationService signalMutationService,
        OncokbService oncokbService,
        ClinvarVariantAnnotationService clinvarVariantAnnotationService,
        IndexRepository indexRepository,
        ProteinChangeResolver proteinChangeResolver,
        HugoGeneSymbolResolver hugoGeneSymbolResolver,
        NotationConverter notationConverter,
        IsoformAnnotationEnricherFactory enricherFactory
    ) {
        this.hgvsAndGenomicLocationFetcher = cachedVariantAnnotationFetcher;
        this.dbsnpFetcher = cachedVariantIdAnnotationFetcher;
        this.hotspotService = hotspotService;
        this.mutationAssessorService = mutationAssessorService;
        this.nucleotideContextService = nucleotideContextService;
        this.variantAnnotationSummaryService = variantAnnotationSummaryService;
        this.myVariantInfoService = myVariantInfoService;
        this.postTranslationalModificationService = postTranslationalModificationService;
        this.signalMutationService = signalMutationService;
        this.oncokbService = oncokbService;
        this.clinvarVariantAnnotationService = clinvarVariantAnnotationService;
        this.indexRepository = indexRepository;
        this.proteinChangeResolver = proteinChangeResolver;
        this.hugoGeneSymbolResolver = hugoGeneSymbolResolver;
        this.notationConverter = notationConverter;
        this.enricherFactory = enricherFactory;
    }

    public Index buildIndex(VariantAnnotation variantAnnotation) {
        IndexBuilder builder = new IndexBuilder(this.proteinChangeResolver, this.hugoGeneSymbolResolver);
        return builder.buildIndex(variantAnnotation);
    }

    public VariantAnnotation getAnnotation(String variant, VariantType variantType)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        VariantAnnotation annotation = this.fetchAnnotationExternally(variant, variantType);
        normalizeStrand(annotation);
        return annotation;
    }

    public List<VariantAnnotation> getAnnotations(List<String> variants, VariantType variantType)
    {
        try {
            List<VariantAnnotation> annotations = this.fetchAnnotationsExternally(variants, variantType);
            normalizeStrands(annotations);
            return annotations;
        } catch (VariantAnnotationWebServiceException e) {
            LOG.warn(e.getLocalizedMessage());
            return Collections.emptyList();
        }
    }

    public VariantAnnotation getAnnotation(String variant, VariantType variantType, String isoformOverrideSource, Map<String, String> token, List<AnnotationField> fields)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        VariantAnnotation annotation = this.fetchAnnotationExternally(variant, variantType);
        normalizeStrand(annotation);

        EnrichmentService postEnrichmentService = this.initPostEnrichmentService(isoformOverrideSource, fields, token);

        if (annotation != null &&
            postEnrichmentService != null)
        {
            postEnrichmentService.enrichAnnotation(annotation);
        }

        return annotation;
    }

    public List<VariantAnnotation> getAnnotations(List<String> variants, VariantType variantType, String isoformOverrideSource, Map<String, String> token, List<AnnotationField> fields)
    {
        try {
            List<VariantAnnotation> variantAnnotations = this.fetchAnnotationsExternally(variants, variantType);
            normalizeStrands(variantAnnotations);
            EnrichmentService postEnrichmentService = this.initPostEnrichmentService(isoformOverrideSource, fields, token);
            if (postEnrichmentService != null) {
                postEnrichmentService.enrichAnnotations(variantAnnotations);
            }
            return variantAnnotations;
        } catch (VariantAnnotationWebServiceException e) {
            LOG.warn(e.getLocalizedMessage());
            return Collections.emptyList();
        }
    }

    private VariantAnnotation fetchAnnotationExternally(String variant, VariantType variantType)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        Optional<VariantAnnotation> variantAnnotation = null;
        String normalizedVariant = normalizeVariant(variant, variantType);

        try {
            // get the annotation from the web service and save it to the DB

            if (variantType == VariantType.HGVS || variantType == VariantType.GENOMIC_LOCATION) {
                variantAnnotation = Optional.ofNullable(this.hgvsAndGenomicLocationFetcher.fetchAndCache(normalizedVariant));
            } else if (variantType == VariantType.DBSNP) {
                variantAnnotation = Optional.ofNullable(this.dbsnpFetcher.fetchAndCache(normalizedVariant));
            }

            // add new annotation to index db
            // if caching is enabled, add to the index DB 
            if (cacheEnabled) {
                variantAnnotation.ifPresent(annotation -> {
                    this.saveToIndexDb(annotation);
                });
            }

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
            VariantAnnotation returnValue = variantAnnotation.get();
            this.addAdditionalInformation(returnValue, variantType, variant);
            return returnValue;
        } catch (NoSuchElementException e) {
            throw new VariantAnnotationNotFoundException(normalizedVariant);
        }
    }

    private List<VariantAnnotation> fetchAnnotationsExternally(List<String> variants, VariantType variantType)
            throws VariantAnnotationWebServiceException {
        List<VariantAnnotation> variantAnnotations = null;
        List<String[]> normalizedVariantToOriginalVariant = new ArrayList<>();
        for (String variant : variants) {
            String normalizedVariant = this.normalizeVariant(variant, variantType);
            if (normalizedVariant == null) {
                continue;
            }

            normalizedVariantToOriginalVariant.add(new String[]{normalizedVariant, variant});
        }

        try {
            // get the annotations from the web service and save it to the DB

            List<String> normalizedVariants = normalizedVariantToOriginalVariant.stream().map((normalizedToOriginal) -> normalizedToOriginal[0]).collect(Collectors.toList());
            if (variantType == VariantType.HGVS || variantType == VariantType.GENOMIC_LOCATION) {
                variantAnnotations = this.hgvsAndGenomicLocationFetcher.fetchAndCache(normalizedVariants);
            } else if (variantType == VariantType.DBSNP) {
                variantAnnotations = this.dbsnpFetcher.fetchAndCache(normalizedVariants);
            }
            for (VariantAnnotation variantAnnotation : variantAnnotations) {
                // add new annotation to index 
                Optional<String[]> normalizedToOriginal = normalizedVariantToOriginalVariant
                    .stream()
                    .filter((normToOrig) -> normToOrig[0].equals(variantAnnotation.getVariant()))
                    .findFirst();
                if (normalizedToOriginal.isPresent()) {
                    if (cacheEnabled) {
                        this.saveToIndexDb(variantAnnotation);
                    }
                    String originalVariantQuery = normalizedToOriginal.get()[1];
                    // Multiple variants may normalize to same HGVS, remove so isn't used multiple times
                    normalizedVariantToOriginalVariant.remove(normalizedToOriginal.get());
                    this.addAdditionalInformation(variantAnnotation, variantType, originalVariantQuery);
                }
            }
        } catch (HttpClientErrorException e) {
            // in case of web service error, throw an exception to indicate that there is a
            // problem with the service.
            throw new VariantAnnotationWebServiceException(
                normalizedVariantToOriginalVariant.stream().map((normalizedToOriginal) -> normalizedToOriginal[0]).collect(Collectors.toList()).toString(),
                e.getResponseBodyAsString(),
                e.getStatusCode()
            );
        } catch (ResourceAccessException e) {
            throw new VariantAnnotationWebServiceException(
                normalizedVariantToOriginalVariant.stream().map((normalizedToOriginal) -> normalizedToOriginal[0]).collect(Collectors.toList()).toString(),
                e.getMessage()
            );
        } catch (ResourceMappingException e) {
            // TODO this indicates that web service returns an incompatible response
        }

        return variantAnnotations;
    }

    public void saveToIndexDb(VariantAnnotation annotation) {
        if (annotation.getHgvsg() != null) {
            Gson gson = new Gson();
            DBObject dbObject = BasicDBObject.parse(gson.toJson(this.buildIndex(annotation)));
            this.indexRepository.saveDBObject("index", annotation.getHgvsg(), dbObject);
        }
    }


    private EnrichmentService initPostEnrichmentService(String isoformOverrideSource, List<AnnotationField> fields, Map<String, String> token)
    {
        // The post enrichment service enriches the annotation after saving
        // the original annotation data to the repository. Any enrichment
        // performed by the post enrichment service is not saved
        // to the annotation repository.
        EnrichmentService postEnrichmentService = new VEPEnrichmentService();

        // always register an isoform override enricher
        // if the source is invalid we will use the default override source
        postEnrichmentService.registerEnricher(
            enricherFactory.create(isoformOverrideSource)
        );

        if (fields == null || fields.isEmpty()) {
            return postEnrichmentService;
        }

        if (fields.contains(AnnotationField.HOTSPOTS))
        {
            postEnrichmentService.registerEnricher(
                new HotspotAnnotationEnricher("cancerHotspots", hotspotService, true)
            );
        }

        if (fields.contains(AnnotationField.MUTATION_ASSESSOR))
        {
            postEnrichmentService.registerEnricher(
                new MutationAssessorEnricher("mutation_assessor", mutationAssessorService)
            );
        }

        if (fields.contains(AnnotationField.NUCLEOTIDE_CONTEXT))
        {
            postEnrichmentService.registerEnricher(
                new NucleotideContextAnnotationEnricher("nucleotide_context", nucleotideContextService)
            );
        }

        if (fields.contains(AnnotationField.MY_VARIANT_INFO))
        {
            postEnrichmentService.registerEnricher(
                new MyVariantInfoAnnotationEnricher("my_variant_info", myVariantInfoService)
            );
        }

        if (fields.contains(AnnotationField.PTMS))
        {
            postEnrichmentService.registerEnricher(
                new PostTranslationalModificationEnricher("ptm", postTranslationalModificationService)
            );
        }

        if (fields.contains(AnnotationField.SIGNAL))
        {
            postEnrichmentService.registerEnricher(
                new SignalAnnotationEnricher("signal", signalMutationService)
            );
        }

        if (fields.contains(AnnotationField.ONCOKB))
        {
            String oncokbToken = null;
            if (token != null && token.containsKey("oncokb")) {
                oncokbToken = token.get("oncokb");
            }

            postEnrichmentService.registerEnricher(
                new OncokbAnnotationEnricher(
                    "oncokb",
                    oncokbService,
                    variantAnnotationSummaryService,
                    oncokbToken
                )
            );
        }

        if (fields.contains(AnnotationField.CLINVAR))
        {
            postEnrichmentService.registerEnricher(
                new ClinvarVariantAnnotationEnricher("clinvar", clinvarVariantAnnotationService)
            );
        }

        if (fields.contains(AnnotationField.ANNOTATION_SUMMARY))
        {
            postEnrichmentService.registerEnricher(
                new CanonicalTranscriptAnnotationEnricher(
                    "annotation_summary",
                    variantAnnotationSummaryService
                )
            );
        }

        return postEnrichmentService;
    }

    private String normalizeVariant(String id, VariantType variantType)
    {
        if (variantType == VariantType.DBSNP) {
            return id;
        }
        if (variantType == VariantType.GENOMIC_LOCATION && (id = this.notationConverter.genomicToHgvs(id)) == null) {
            return null;
        } 
        return this.notationConverter.hgvsNormalizer(id);
    }

    private void addAdditionalInformation(VariantAnnotation variantAnnotation, VariantType variantType, String originalVariantQuery) {
        variantAnnotation.setOriginalVariantQuery(originalVariantQuery);
        if (variantType == VariantType.GENOMIC_LOCATION) {
            variantAnnotation.setGenomicLocationExplanation(this.notationConverter.getGenomicLocationExplanation(originalVariantQuery));
        }
    }

    private void normalizeStrand(VariantAnnotation variantAnnotation) {
        if (variantAnnotation.getStrand() == -1) {
            variantAnnotation.setAlleleString(GenomicLocationUtil.getReverseStrandAllele(variantAnnotation.getAlleleString()));
            variantAnnotation.setStrand(1);
        }
    }

    private void normalizeStrands(List<VariantAnnotation> variantAnnotations) {
        for (VariantAnnotation annotation : variantAnnotations) {
            normalizeStrand(annotation);
        }
    }
}
