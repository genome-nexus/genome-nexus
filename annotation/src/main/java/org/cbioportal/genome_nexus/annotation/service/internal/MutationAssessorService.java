package org.cbioportal.genome_nexus.annotation.service.internal;

import org.cbioportal.genome_nexus.annotation.domain.MutationAssessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.cbioportal.genome_nexus.annotation.util.Transformer;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
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

    public MutationAssessor getMutationAssessor(String variant) throws IOException
    {
//        MutationAssessor obj = Transformer.mapJsonToInstance(getMutationAssessorJSON(variant), MutationAssessor.class).get(0);
//        obj.setVariant(variant);
//        return obj;
        return mapJsonToMutationAssessor(variant);
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

    private MutationAssessor mapJsonToMutationAssessor(String variant) throws IOException
    {
        String jsonString = getMutationAssessorJSON(toMutationString(variant));

        ObjectMapper objectMapper = new ObjectMapper();
        String toMap = objectMapper.writeValueAsString(getDBObject(jsonString));

        MutationAssessor mutationObj = objectMapper.readValue(toMap, MutationAssessor.class);
        mutationObj.setVariant(variant);    // setting the variant as original input

        return mutationObj;
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

    private static Object getDBObject(String jsonString)
    {
        DBObject dbObject = (DBObject) JSON.parse(jsonString);
        if (dbObject instanceof List)
        {
            return dbObject.get("0");
        }
        return null;
    }

}
