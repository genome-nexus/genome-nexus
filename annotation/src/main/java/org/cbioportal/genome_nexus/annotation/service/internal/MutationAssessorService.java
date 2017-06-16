package org.cbioportal.genome_nexus.annotation.service.internal;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.cbioportal.genome_nexus.annotation.domain.MutationAssessor;

import java.util.Arrays;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MutationAssessorService
{

    private String mutationAssessorURL;

    // manually obtaining MutationAssessor response from hardcoded URL
    public static void main(String[] args)
    {
        // todo: support for lists of variants
        String genomeNexusInput = "7:g.140453136A>T,12:g.25398285C>A,2:g.29443695G>T,7:g.55259515T>G";
        ArrayList<String> variantsList = splitInput(genomeNexusInput);

        for (String variant : variantsList)
        {
            MutationAssessorService serviceObj = new MutationAssessorService();
            serviceObj.setMutationAssessorURL("http://mutationassessor.org/r3/?cm=var&var=");

            String returnString = serviceObj.getMutationAssessorJSON(fromHGVS(variant));
            Object obj = serviceObj.getDBObject(returnString);

            try
            {
                MutationAssessor mutationObj = mapJsonToMutationAssessor(obj);
                System.err.println(mutationObj.getOuput());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void setMutationAssessorURL(String mutationAssessorURL) { this.mutationAssessorURL = mutationAssessorURL; }

    private static ArrayList<String> splitInput(String inputString) {
        return new ArrayList<>(Arrays.asList(inputString.split(",")));
    }

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

    // converts genome-nexus input string to mutation assessor input
    private static String fromHGVS(String hgvsString) {
        String temp;

        temp = hgvsString.replaceAll("\\p{Punct}[a-z]\\p{Punct}", ",");
        temp = temp.replaceAll("\\p{Punct}", ",");
        temp = temp.substring(0, temp.length()-3) +
                "," +
                temp.substring(temp.length()-3);

        return temp;
    }

    private Object getDBObject(String jsonString) {
        DBObject dbObject = (DBObject) JSON.parse(jsonString);
        if (dbObject instanceof List)
            return dbObject.get("0");
        return null;
    }

    private static MutationAssessor mapJsonToMutationAssessor(Object obj) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String toMap = objectMapper.writeValueAsString(obj);
        return objectMapper.readValue(toMap, MutationAssessor.class);
    }

}
