package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.remote.MutationAssessorDataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    {
        MutationAssessor mutationAssessorObj = new MutationAssessor();

        try
        {
            List<MutationAssessor> list = this.externalResourceFetcher.fetchInstances(variant);

            if (list.size() != 0)
            {
                mutationAssessorObj = list.get(0);
                if (mutationAssessorObj != null)
                {
                    mutationAssessorObj.setInput(hgvs);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
