package org.cbioportal.genome_nexus.model;

// import java.io.FileNotFoundException;
// import java.io.FileReader;
// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Paths;

// import com.google.gson.Gson;
// import com.google.gson.GsonBuilder;
// import com.google.gson.stream.JsonReader;
// import com.mongodb.util.JSON;

public class AggregateSourceInfo {

    private GenomeNexusInfo genomeNexus;
    private VEPInfo vep;
    private AnnotationSourcesInfo annotationSourcesInfo;

    // protected static final AnnotationSourcesInfo readAnnotationSourcesInfoFromFile() {
    //     System.out.println("reading json from file");
    //     JsonReader reader;
    //     try {
    //         reader = new JsonReader(new FileReader("docs/annntationSourceVersions2.json"));
    //         AnnotationSourcesInfo annotationSourcesInfo = new Gson().fromJson(reader, AnnotationSourcesInfo.class);
    //         return annotationSourcesInfo;
    //     } catch (FileNotFoundException e) {
    //         e.printStackTrace();
    //     }
    //     return null;
    // }

    // public AggregateSourceInfo(GenomeNexusInfo genomeNexusInfo, VEPInfo vepInfo) {
    //     this.genomeNexus = genomeNexusInfo;
    //     this.vep = vepInfo;

    //     JsonReader reader;
    //     try {
    //         // reader = new JsonReader(new FileReader("docs/annntationSourceVersions2.json"));
    //         String json = new String(Files.readAllBytes(Paths.get("docs/annntationSourceVersions2.json")));
    //         System.out.println(json);
    //         // System.out.println(new Gson().fromJson(reader, AnnotationSourcesInfo.class));
    //         this.annotationSourcesInfo = new Gson().fromJson(json, AnnotationSourcesInfo.class);
    //     } catch (FileNotFoundException e) {
    //         e.printStackTrace();
    //     } catch (IOException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     }

    //     System.out.println("printing json from file");
    //     Gson gson = new GsonBuilder()
    //     .setPrettyPrinting()
    //     .create();
  
    //     String jsonString = gson.toJson(this.annotationSourcesInfo);
    //     System.out.println(jsonString);
    //     // this.annotationSourcesInfo = AggregateSourceInfo.readAnnotationSourcesInfoFromFile();
    // }

    public AggregateSourceInfo(GenomeNexusInfo genomeNexusInfo, VEPInfo vepInfo) {
        this.genomeNexus = genomeNexusInfo;
        this.vep = vepInfo;
    }

    public GenomeNexusInfo getGenomeNexus() {
        return genomeNexus;
    }

    public void setGenomeNexus(GenomeNexusInfo genomeNexusInfo) {
        this.genomeNexus = genomeNexusInfo;
    }

    public VEPInfo getVep() {
        return vep;
    }

    public void setVep(VEPInfo vepInfo) {
        this.vep = vepInfo;
    }

    public AnnotationSourcesInfo getAnnotationSourcesInfo() {
        return annotationSourcesInfo;
    }

    public void setAnnotationSourcesInfo(AnnotationSourcesInfo annotationSourcesInfo) {
        this.annotationSourcesInfo = annotationSourcesInfo;
    }
}
