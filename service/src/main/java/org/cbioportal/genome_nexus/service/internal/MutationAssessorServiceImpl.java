package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.model.MutationAssessorAnnotation;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.MutationAssessorService;
import org.cbioportal.genome_nexus.service.VariantAnnotationService;
import org.cbioportal.genome_nexus.service.exception.*;
import org.cbioportal.genome_nexus.service.remote.MutationAssessorDataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MutationAssessorServiceImpl implements MutationAssessorService
{
    private final MutationAssessorDataFetcher externalResourceFetcher;
    private final VariantAnnotationService variantAnnotationService;

    @Autowired
    public MutationAssessorServiceImpl(MutationAssessorDataFetcher externalResourceFetcher,
                                       VariantAnnotationService variantAnnotationService)
    {
        this.externalResourceFetcher = externalResourceFetcher;
        this.variantAnnotationService = variantAnnotationService;
    }

    public MutationAssessor getMutationAssessorFromEnrichedVariantAnnotation(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        VariantAnnotation variantAnnotation = this.getVariantAnnotation(variant);

        return this.getMutationAssessorFromEnrichedVariantAnnotation(variantAnnotation);
    }

    public List<MutationAssessor> getMutationAssessorFromEnrichedVariantAnnotation(List<String> variants)
    {
        List<MutationAssessor> mutationAssessors = new ArrayList<>();
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

    public MutationAssessor getMutationAssessor(VariantAnnotation annotation)
        throws MutationAssessorNotFoundException, MutationAssessorWebServiceException
    {
        // checks annotation is SNP
        if (annotation.getStart() == null
            || !annotation.getStart().equals(annotation.getEnd())
            || !annotation.getAlleleString().matches("[A-Z]/[A-Z]"))
        {
            return null;
        }

        return this.getMutationAssessor(buildRequest(annotation), annotation.getVariant());

    }

    public MutationAssessor getMutationAssessor(String variant, String hgvs)
        throws MutationAssessorNotFoundException, MutationAssessorWebServiceException
    {
        MutationAssessor mutationAssessorObj = null;

        try {
            List<MutationAssessor> list = this.externalResourceFetcher.fetchInstances(variant);

            if (list.size() != 0) {
                mutationAssessorObj = list.get(0);
            }

            if (mutationAssessorObj != null) {
                mutationAssessorObj.setInput(hgvs);
            }
            else {
                throw new MutationAssessorNotFoundException(variant);
            }
        } catch (JsonMappingException e) {
            throw new MutationAssessorWebServiceException(e.getMessage());
        } catch (HttpClientErrorException e) {
            throw new MutationAssessorWebServiceException(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new MutationAssessorWebServiceException(e.getMessage());
        }

        return mutationAssessorObj;
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

    private List<String> initFields()
    {
        // uses enrichment to get mutation assessor object
        List<String> fields = new ArrayList<>(1);
        fields.add("mutation_assessor");

        return fields;
    }

    private List<VariantAnnotation> getVariantAnnotations(List<String> variants)
    {
        // uses enrichment to get mutation assessor annotation
        return this.variantAnnotationService.getAnnotations(variants, null, this.initFields());
    }

    private VariantAnnotation getVariantAnnotation(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        // uses enrichment to get mutation assessor annotation
        return this.variantAnnotationService.getAnnotation(variant, null, this.initFields());
    }

    private String buildRequest(VariantAnnotation annotation)
    {
        StringBuilder sb = new StringBuilder(annotation.getSeqRegionName() + ",");
        sb.append(annotation.getStart() + ",");
        sb.append(annotation.getAlleleString().replaceAll("/", ","));

        return sb.toString();
    }

}
