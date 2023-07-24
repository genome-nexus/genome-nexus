package org.cbioportal.genome_nexus.service.remote;

import org.cbioportal.genome_nexus.model.VuesJsonRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@Component
public class RevueDataFetcher {

    public static VuesJsonRecord[] getRevueData(String revueUrl) {
        RestTemplate restTemplate = new RestTemplate();
        // Fetch the JSON data
        ResponseEntity<String> response = restTemplate.getForEntity(revueUrl, String.class);
        String json = response.getBody();
        Gson gson = new Gson();
        try {;
            // Convert the JSON data to a VuesJsonRecord[] array
            VuesJsonRecord[] records = gson.fromJson(json, VuesJsonRecord[].class);
            return records;
        } catch (JsonSyntaxException e) {
            System.err.println("Error getting JSON file: " + e.getMessage());
            return null;
        }
    }

}
