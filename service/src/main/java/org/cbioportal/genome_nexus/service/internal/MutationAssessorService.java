package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.remote.MutationAssessorDataFetcher;
import org.cbioportal.genome_nexus.service.exception.JsonMappingException;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorNotFoundException;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorWebServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

@Service
public class MutationAssessorService
{
    private final MutationAssessorDataFetcher externalResourceFetcher;

    @Autowired
    public MutationAssessorService(MutationAssessorDataFetcher externalResourceFetcher)
    {
        this.externalResourceFetcher = externalResourceFetcher;
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

        try
        {

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

    private String buildRequest(VariantAnnotation annotation)
    {
        StringBuilder sb = new StringBuilder(annotation.getSeqRegionName() + ",");
        sb.append(annotation.getStart() + ",");
        sb.append(annotation.getAlleleString().replaceAll("/", ","));

        return sb.toString();
    }

}
