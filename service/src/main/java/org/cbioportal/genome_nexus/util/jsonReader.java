package org.cbioportal.genome_nexus.util;

import org.cbioportal.genome_nexus.model.VuesJsonRecord;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class JsonReader {

    public static VuesJsonRecord[] getVuesList() {

        Gson gson = new Gson();

        try (Reader reader = new FileReader("data/curiousCases/VUEs.json")) {

            // Convert JSON File to Java Object
            VuesJsonRecord[] vues = gson.fromJson(reader, VuesJsonRecord[].class);
            
            return vues;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

}