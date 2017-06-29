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
        String inputString = toMutationAssessorString(variant);
        String jsonString = getMutationAssessorJSON(inputString);
        MutationAssessor mutationAssessorObj = new MutationAssessor();

        try
        {
            if (Transformer.mapJsonToInstance(jsonString, MutationAssessor.class).size() != 0) {
                mutationAssessorObj =
                    Transformer.mapJsonToInstance(jsonString, MutationAssessor.class).get(0);
                mutationAssessorObj.setVariant(variant);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return mutationAssessorObj;
    }


    /**
     * Transforms a given variant string into the comma-separated variant supplied to Mutation Assessor
     *      ex: 7:g.140453136A>T    -->     7,140453136,A,T
     *
     * todo: standardize input formatting and parsing
     */
    private static String toMutationAssessorString(String inputString)
    {
        // todo: check if input is in the right format
        if (inputString.length() < 3) {
            return inputString;
        }
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

        if (variants != null &&
            variants.length() > 0)
        {
            uri += variants + "&frm=json";
        }

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, String.class);
    }

}
