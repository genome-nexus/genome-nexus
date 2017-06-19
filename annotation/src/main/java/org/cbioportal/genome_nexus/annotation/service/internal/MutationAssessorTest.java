package org.cbioportal.genome_nexus.annotation.service.internal;

import org.cbioportal.genome_nexus.annotation.domain.MutationAssessor;
import org.cbioportal.genome_nexus.annotation.service.internal.MutationAssessorService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MutationAssessorTest {

//    public static void main(String[] args)
//    {
//        String genomeNexusInput = "7:g.140453136A>T,12:g.25398285C>A,2:g.29443695G>T,7:g.55259515T>G";
//        ArrayList<String> variantsList = splitInput(genomeNexusInput);
//
//        for (String variant : variantsList)
//        {
//            MutationAssessorService service = new MutationAssessorService();
//            service.setMutationAssessorURL("http://mutationassessor.org/r3/?cm=var&var=");
//
//            try {
//                MutationAssessor mutationObj = service.getMutationAssessor(variant);
//                System.err.println(mutationObj.getOuput());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private static ArrayList<String> splitInput(String inputString) {
        return new ArrayList<>(Arrays.asList(inputString.split(",")));
    }

}
