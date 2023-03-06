package org.cbioportal.genome_nexus.util;

import org.cbioportal.genome_nexus.model.VuesJsonRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class JsonReader {

    public static VuesJsonRecord[] getVuesList() throws IOException {

        Gson gson = new Gson();
        Resource resource = new ClassPathResource("VUEs.json");
        InputStream inputStream = resource.getInputStream();
        try {
            Reader reader = new InputStreamReader(inputStream);
            // Convert the JSON data to a VuesJsonRecord[] array
            VuesJsonRecord[] records = gson.fromJson(reader, VuesJsonRecord[].class);
            reader.close();
            inputStream.close();
            return records;
        } catch (IOException e) {
            // Handle the IOException by printing an error message and returning null
            System.err.println("Error reading JSON file: " + e.getMessage());
            return null;
        }
    }

}
