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
    private String mutationAssessorURL;
    @Value("${mutationAssessor.url}")
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

    // todo: get rid of hardcoded URLs and check that variant is in the right format
    private String getMutationAssessorJSON(String variant)
    {
        String uri = mutationAssessorURL;

        if (variant != null &&
            variant.length() > 0)
        {
            uri += variant + "&frm=json";
        }

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, String.class);
    }

    // todo: standardize input formatting
    private String toMutationString(String inputString)
    {
        String temp;
        temp = inputString.replaceAll("\\p{Punct}[a-z]\\p{Punct}", ",");
        temp = temp.replaceAll("\\p{Punct}", ",");
        temp = temp.substring(0, temp.length()-3) +
            "," +
            temp.substring(temp.length()-3);

        return temp;
    }

}
