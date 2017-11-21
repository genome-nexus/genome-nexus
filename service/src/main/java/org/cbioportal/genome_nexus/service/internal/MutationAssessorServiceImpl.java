package org.cbioportal.genome_nexus.service.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.MutationAssessor;
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

@Service
public class MutationAssessorServiceImpl implements MutationAssessorService
{
    private static final Log LOG = LogFactory.getLog(MutationAssessorServiceImpl.class);

    private final MutationAssessorDataFetcher externalResourceFetcher;
    private final VariantAnnotationService variantAnnotationService;

    @Autowired
    public MutationAssessorServiceImpl(MutationAssessorDataFetcher externalResourceFetcher,
                                       VariantAnnotationService variantAnnotationService)
    {
        this.externalResourceFetcher = externalResourceFetcher;
        this.variantAnnotationService = variantAnnotationService;
    }

    public MutationAssessor getMutationAssessor(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException,
        MutationAssessorWebServiceException, MutationAssessorNotFoundException
    {
        VariantAnnotation variantAnnotation = this.variantAnnotationService.getAnnotation(variant);

        return this.getMutationAssessorByVariantAnnotation(variantAnnotation);
    }

    public List<MutationAssessor> getMutationAssessor(List<String> variants)
    {
        List<MutationAssessor> mutationAssessors = new ArrayList<>();
        List<VariantAnnotation> variantAnnotations = this.variantAnnotationService.getAnnotations(variants);

        for (VariantAnnotation variantAnnotation : variantAnnotations)
        {
            try {
                mutationAssessors.add(this.getMutationAssessorByVariantAnnotation(variantAnnotation));
            } catch (MutationAssessorWebServiceException e) {
                LOG.warn(e.getLocalizedMessage());
            } catch (MutationAssessorNotFoundException e) {
                // fail silently for this variant
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
            throw new MutationAssessorNotFoundException(annotation.getVariant());
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
        } catch (ResourceMappingException e) {
            throw new MutationAssessorWebServiceException(e.getMessage());
        } catch (HttpClientErrorException e) {
            throw new MutationAssessorWebServiceException(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new MutationAssessorWebServiceException(e.getMessage());
        }

        return mutationAssessorObj;
    }

    private MutationAssessor getMutationAssessorByVariantAnnotation(VariantAnnotation variantAnnotation)
        throws MutationAssessorWebServiceException, MutationAssessorNotFoundException
    {
        MutationAssessor mutationAssessorObj = this.getMutationAssessor(variantAnnotation);

        if (mutationAssessorObj != null &&
            mutationAssessorObj.getMappingIssue().length() == 0)
        {
            return mutationAssessorObj;
        }
        else {
            throw new MutationAssessorNotFoundException(variantAnnotation.getVariant());
        }
    }

    private String buildRequest(VariantAnnotation annotation)
    {
        StringBuilder sb = new StringBuilder(annotation.getSeqRegionName() + ",");
        sb.append(annotation.getStart() + ",");
        sb.append(annotation.getAlleleString().replaceAll("/", ","));

        return sb.toString();
    }

}
