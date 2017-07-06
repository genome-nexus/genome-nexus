package org.cbioportal.genome_nexus.annotation.service.internal;

import org.cbioportal.genome_nexus.annotation.domain.MutationAssessor;
import org.cbioportal.genome_nexus.annotation.util.Transformer;
import org.cbioportal.genome_nexus.annotation.util.HGVS;
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

    public MutationAssessor getMutationAssessor(String variant)
    {
        MutationAssessor mutationAssessorObj = new MutationAssessor();

        try
        {
            String inputString = HGVS.getMutationAssessorString(variant);

            // string was incorrectly formatted
            if (inputString == null)
            {
                inputString = "invalid input, check input format\n";
            }

            String jsonString = getMutationAssessorJSON(inputString);
            List<MutationAssessor> list = Transformer.mapJsonToInstance(jsonString, MutationAssessor.class);

            if (list.size() != 0)
            {
                mutationAssessorObj = list.get(0);
                if (mutationAssessorObj != null)
                {
                    mutationAssessorObj.setVariant(variant);
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
