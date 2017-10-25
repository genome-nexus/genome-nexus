package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Service
public class MutationAssessorService
{
    private String mutationAssessorURL;
    @Value("${mutationAssessor.url}")
    public void setMutationAssessorURL(String mutationAssessorURL)
    {
        this.mutationAssessorURL = mutationAssessorURL;
    }

    private final ExternalResourceTransformer externalResourceTransformer;

    @Autowired
    public MutationAssessorService(ExternalResourceTransformer externalResourceTransformer)
    {
        this.externalResourceTransformer = externalResourceTransformer;
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
            String jsonString = getMutationAssessorJSON(variant);
            List<MutationAssessor> list = this.externalResourceTransformer.transform(
                jsonString, MutationAssessor.class);

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

    private String getMutationAssessorJSON(String variant)
    {
        String uri = this.mutationAssessorURL;

        if (variant != null &&
            variant.length() > 0)
        {
            uri = uri.replace("VARIANT", variant);
        }

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, String.class);
    }

    private String buildRequest(VariantAnnotation annotation)
    {
        StringBuilder sb = new StringBuilder(annotation.getSeqRegionName() + ",");
        sb.append(annotation.getStart() + ",");
        sb.append(annotation.getAlleleString().replaceAll("/", ","));

        return sb.toString();
    }

}
