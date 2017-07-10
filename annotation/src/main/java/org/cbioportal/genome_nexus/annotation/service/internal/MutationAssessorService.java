package org.cbioportal.genome_nexus.annotation.service.internal;

import org.cbioportal.genome_nexus.annotation.domain.MutationAssessor;
import org.cbioportal.genome_nexus.annotation.domain.VariantAnnotation;
import org.cbioportal.genome_nexus.annotation.util.Transformer;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Service
public class MutationAssessorService
{
    @Value("${mutationAssessor.url}")
    private String mutationAssessorURL;

    public void setMutationAssessorURL(String mutationAssessorURL)
    {
        this.mutationAssessorURL = mutationAssessorURL;
    }

    public MutationAssessor getMutationAssessor(VariantAnnotation annotation)
    {
        if (annotation.getStart() == null
            || !annotation.getStart().equals(annotation.getEnd())
            || !annotation.getAlleleString().matches("[A-Z]/[A-Z]"))
        {
            return null;
        }


        StringBuilder sb = new StringBuilder(annotation.getSeqRegionName() + ",");
        sb.append(annotation.getStart() + ",");
        sb.append(annotation.getAlleleString().replaceAll("/", ","));

        String request = sb.toString();

        return this.getMutationAssessor(request, annotation.getVariant());

    }

    public MutationAssessor getMutationAssessor(String variant, String hgvs)
    {
        MutationAssessor mutationAssessorObj = new MutationAssessor();

        try
        {
            String jsonString = getMutationAssessorJSON(variant);
            List<MutationAssessor> list = Transformer.mapJsonToInstance(jsonString, MutationAssessor.class);

            if (list.size() != 0)
            {
                mutationAssessorObj = list.get(0);
                if (mutationAssessorObj != null)
                {
                    mutationAssessorObj.setVariant(hgvs);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return mutationAssessorObj;
    }

    // todo: get rid of hardcoded URLs
    private String getMutationAssessorJSON(String variants)
    {
        String uri = mutationAssessorURL;

        if (variants != null &&
            variants.length() > 0)
        {
            uri += variants + "&frm=json";
        }

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, String.class);
    }

}
