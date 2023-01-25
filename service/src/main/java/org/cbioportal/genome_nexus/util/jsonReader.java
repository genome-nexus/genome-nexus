package org.cbioportal.genome_nexus.util;

import org.cbioportal.genome_nexus.model.VUEs;


import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class jsonReader {

    public static VUEs [] getVuesList() {

        Gson gson = new Gson();

        try (Reader reader = new FileReader("data/curiousCases/VUEs.json")) {

            // Convert JSON File to Java Object
            VUEs [] vues = gson.fromJson(reader, VUEs[].class);
			
			// print vues 
            System.out.println(vues);

            return vues;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

}
