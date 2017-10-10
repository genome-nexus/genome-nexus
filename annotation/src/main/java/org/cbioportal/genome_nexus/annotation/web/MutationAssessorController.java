package org.cbioportal.genome_nexus.annotation.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cbioportal.genome_nexus.annotation.domain.MutationAssessor;
import org.cbioportal.genome_nexus.annotation.domain.MutationAssessorAnnotation;
import org.cbioportal.genome_nexus.annotation.domain.VariantAnnotation;
import org.cbioportal.genome_nexus.annotation.service.AnnotationEnricher;
import org.cbioportal.genome_nexus.annotation.service.EnrichmentService;
import org.cbioportal.genome_nexus.annotation.service.VariantAnnotationService;
import org.cbioportal.genome_nexus.annotation.service.internal.MutationAssessorAnnotationEnricher;
import org.cbioportal.genome_nexus.annotation.service.internal.MutationAssessorService;
import org.cbioportal.genome_nexus.annotation.service.internal.VEPEnrichmentService;
import org.cbioportal.genome_nexus.annotation.web.config.InternalApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@InternalApi
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
@Api(tags = "mutation-assessor-controller", description = "Mutation Assessor Controller")
public class MutationAssessorController {

    private final MutationAssessorService mutationAssessorService;
    private final VariantAnnotationService variantAnnotationService;

    @Autowired
    public MutationAssessorController(VariantAnnotationService variantAnnotationService,
                                      MutationAssessorService mutationAssessorService)
    {
        this.variantAnnotationService = variantAnnotationService;
        this.mutationAssessorService = mutationAssessorService;
    }

    @ApiOperation(value = "Retrieves mutation assessor information for the provided list of variants",
        nickname = "fetchMutationAssessorAnnotationGET")
    @RequestMapping(value = "/mutation_assessor/{variant:.+}",
        method = RequestMethod.GET,
        produces = "application/json")
    public MutationAssessor fetchMutationAssessorAnnotationGET(
        @ApiParam(value="A variants. For example 7:g.140453136A>T",
            required = true,
            allowMultiple = true)
        @PathVariable String variant)
    {
        return this.getMutationAssessorAnnotation(variant);
    }

    @ApiOperation(value = "Retrieves mutation assessor information for the provided list of variants",
        nickname = "postMutationAssessorAnnotation")
    @RequestMapping(value = "/mutation_assessor",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<MutationAssessor> fetchMutationAssessorAnnotationPOST(
        @ApiParam(value="List of variants. For example [\"7:g.140453136A>T\",\"12:g.25398285C>A\"]",
            required = true,
            allowMultiple = true)
        @RequestBody List<String> variants)
    {
        return this.getMutationAssessorAnnotation(variants);
    }

    private MutationAssessor getMutationAssessorAnnotation(String variant)
    {
        VariantAnnotation variantAnnotation = this.getVariantAnnotation(variant);

        return this.getMutationAssessorFromEnrichedVariantAnnotation(variantAnnotation);
    }

    private List<MutationAssessor> getMutationAssessorAnnotation(List<String> variants)
    {
        List<MutationAssessor> mutationAssessors = new ArrayList<>();

        // uses enrichment to get mutation assessor object
        List<String> fields = new ArrayList<>();
        fields.add("mutation_assessor");

        List<VariantAnnotation> variantAnnotations = this.getVariantAnnotations(variants);

        for (VariantAnnotation variantAnnotation : variantAnnotations)
        {
            // gets mutation assessor annotation object from variant annotation map
            MutationAssessor mutationAssessor = this.getMutationAssessorFromEnrichedVariantAnnotation(variantAnnotation);

            if (mutationAssessor != null)
            {
                mutationAssessors.add(mutationAssessor);
            }
        }

        return mutationAssessors;
    }

    private MutationAssessor getMutationAssessorFromEnrichedVariantAnnotation(VariantAnnotation variantAnnotation)
    {
        // gets mutation assessor annotation object from variant annotation map
        Map<String, Object> map = variantAnnotation.getDynamicProps();

        MutationAssessorAnnotation mutationAssessorAnnotation
            = (MutationAssessorAnnotation) map.get("mutation_assessor");

        if (mutationAssessorAnnotation != null)
        {
            MutationAssessor obj = mutationAssessorAnnotation.getAnnotation();

            if (obj != null &&
                obj.getMappingIssue().length() == 0)
            {
                return obj;
            }
        }

        return null;
    }

    private EnrichmentService initPostEnrichmentService()
    {
        // The post enrichment service enriches the annotation after saving
        // the original annotation data to the repository. Any enrichment
        // performed by the post enrichment service is not saved
        // to the annotation repository.
        EnrichmentService postEnrichmentService = new VEPEnrichmentService();

        AnnotationEnricher enricher = new MutationAssessorAnnotationEnricher(mutationAssessorService);
        postEnrichmentService.registerEnricher("mutation_assessor", enricher);

        return postEnrichmentService;
    }

    private List<VariantAnnotation> getVariantAnnotations(List<String> variants)
    {
        EnrichmentService postEnrichmentService = this.initPostEnrichmentService();

        return Annotator.getVariantAnnotations(variants, variantAnnotationService, postEnrichmentService);
    }

    private VariantAnnotation getVariantAnnotation(String variant)
    {
        EnrichmentService postEnrichmentService = this.initPostEnrichmentService();

        return Annotator.getVariantAnnotation(variant, variantAnnotationService, postEnrichmentService);
    }
}
