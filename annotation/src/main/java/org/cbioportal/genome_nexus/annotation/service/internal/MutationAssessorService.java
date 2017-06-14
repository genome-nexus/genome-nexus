package org.cbioportal.genome_nexus.annotation.service.internal;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
// import org.cbioportal.genome_nexus.annotation.util.Transformer;
import org.cbioportal.genome_nexus.annotation.domain.MutationAssessor;

import java.io.IOException;
import java.util.ArrayList;

public class MutationAssessorService
{

    private String mutationAssessorURL;

    // manually obtaining MutationAssessor response from hardcoded URL
    public static void main(String[] args)
    {
        ArrayList<String> variantsList = new ArrayList<>();
        variantsList.add("12,25398285,C,A");
        variantsList.add("7,140453136,A,T");
        variantsList.add("13,140453136,A,C"); // no response from Mutation Assessor website

        for (String variant : variantsList)
        {
            MutationAssessorService serviceObj = new MutationAssessorService();
            serviceObj.setMutationAssessorURL("http://mutationassessor.org/r3/?cm=var&var=");
            String returnString = serviceObj.getMutationAssessorJSON(variant);

            
            //System.err.println(returnString);

            DBObject dbObject = (DBObject) JSON.parse(returnString);

            try
            {
                // get first object of array - todo: support for lists of variants
                Object firstObj = dbObject.get("0");

                ObjectMapper objectMapper = new ObjectMapper();
                String toMap = objectMapper.writeValueAsString(firstObj);

                // System.err.println("\n\njson string to map: " + toMap);
                // String testerStr = "{\"input\" : \"test\", \"gene\" : \"test-gene\", \"F_score\" : \"test-score\"}";

                MutationAssessor mutationObj = objectMapper.readValue(toMap, MutationAssessor.class);
                System.err.println(mutationObj.getOuput());

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    public void setMutationAssessorURL(String mutationAssessorURL) { this.mutationAssessorURL = mutationAssessorURL; }

    // todo: support multiple variants
    private String getMutationAssessorJSON(String variant)
    {
        String uri = mutationAssessorURL;

        // todo: check that variant is in the right format
        if (variant != null &&
            variant.length() > 0)
        {
            uri += variant + "&frm=json";
        }

         RestTemplate restTemplate = new RestTemplate();
         return restTemplate.getForObject(uri, String.class);
    }

}
