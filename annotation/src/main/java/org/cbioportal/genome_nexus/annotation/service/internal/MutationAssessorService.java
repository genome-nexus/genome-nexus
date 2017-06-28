package org.cbioportal.genome_nexus.annotation.service.internal;

import org.cbioportal.genome_nexus.annotation.domain.MutationAssessor;
import org.cbioportal.genome_nexus.annotation.util.Transformer;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

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
        String inputString = toMutationString(variant);
        MutationAssessor mutationAssessorObj;
        try
        {
            mutationAssessorObj =
                Transformer.mapJsonToInstance(getMutationAssessorJSON(inputString), MutationAssessor.class).get(0);
            mutationAssessorObj.setVariant(variant);
            return mutationAssessorObj;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // todo: handle this
        return null;
    }


    // todo: standardize input formatting
    private static String toMutationString(String inputString)
    {
        String temp;
        temp = inputString.replaceAll("\\p{Punct}[a-z]\\p{Punct}", ",");
        temp = temp.replaceAll("\\p{Punct}", ",");
        temp = temp.substring(0, temp.length()-3) +
                "," +
                temp.substring(temp.length()-3);

        return temp;
    }

    // todo: get rid of hardcoded URLs
    private String getMutationAssessorJSON(String variants)
    {
        String uri = mutationAssessorURL;

        // todo: check that variant is in the right format
        if (variants != null &&
            variants.length() > 0)
        {
            uri += variants + "&frm=json";
        }

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, String.class);
    }

}
