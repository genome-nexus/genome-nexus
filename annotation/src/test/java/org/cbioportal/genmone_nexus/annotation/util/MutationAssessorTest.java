package org.cbioportal.genmone_nexus.annotation.util;

import org.cbioportal.genome_nexus.annotation.service.internal.MutationAssessorService;
import org.cbioportal.genome_nexus.annotation.domain.MutationAssessor;
import org.cbioportal.genome_nexus.annotation.util.Transformer;
import org.junit.Test;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.*;
import java.net.URL;

import static org.junit.Assert.*;

public class MutationAssessorTest
{
    /* for debugging */
    private static Logger log = Logger.getLogger(String.valueOf(MutationAssessorTest.class));

    @Test
    public void testWebService() throws IOException
    {
        MutationAssessorService service = new MutationAssessorService();
        service.setMutationAssessorURL("http://mutationassessor.org/r3/?cm=var&var=");

        String input1 = "7:g.140453136A>T";
        String urlString1 = "http://mutationassessor.org/r3/?cm=var&var=7,140453136,A,T&frm=json";
        MutationAssessor mutationObj1 = service.getMutationAssessor(input1);
        MutationAssessor mutationObj2 =
            Transformer.mapJsonToInstance(getReturnString(urlString1), MutationAssessor.class).get(0);

        // getVariant() not tested because variants set manually
        assertEquals(mutationObj1.getHugoSymbol(), mutationObj2.getHugoSymbol());
        assertEquals(mutationObj1.getFunctionalImpact(), mutationObj2.getFunctionalImpact());
        assertEquals(mutationObj1.getFunctionalImpactScore(), mutationObj2.getFunctionalImpactScore(), 0);


        String input2 = "12:g.25398285C>A";
        String urlString2 = "http://mutationassessor.org/r3/?cm=var&var=12,25398285,C,A&frm=json";
        MutationAssessor mutationObj21 = service.getMutationAssessor(input2);
        MutationAssessor mutationObj22 =
            Transformer.mapJsonToInstance(getReturnString(urlString2), MutationAssessor.class).get(0);

        assertEquals(mutationObj21.getHugoSymbol(), mutationObj22.getHugoSymbol());
        assertEquals(mutationObj21.getFunctionalImpact(), mutationObj22.getFunctionalImpact());
        assertEquals(mutationObj21.getFunctionalImpactScore(), mutationObj22.getFunctionalImpactScore(), 0);


        String input3 = "2:g.29443695G>T";
        String urlString3 = "http://mutationassessor.org/r3/?cm=var&var=2,29443695,G,T&frm=json";
        MutationAssessor mutationObj31 = service.getMutationAssessor(input3);
        MutationAssessor mutationObj32 =
            Transformer.mapJsonToInstance(getReturnString(urlString3), MutationAssessor.class).get(0);

        assertEquals(mutationObj31.getHugoSymbol(), mutationObj32.getHugoSymbol());
        assertEquals(mutationObj31.getFunctionalImpact(), mutationObj32.getFunctionalImpact());
        assertEquals(mutationObj31.getFunctionalImpactScore(), mutationObj32.getFunctionalImpactScore(), 0);


        String input4 = "7:g.55259515T>G";
        String urlString4 = "http://mutationassessor.org/r3/?cm=var&var=7,55259515,T,G&frm=json";
        MutationAssessor mutationObj41 = service.getMutationAssessor(input4);
        MutationAssessor mutationObj42 =
            Transformer.mapJsonToInstance(getReturnString(urlString4), MutationAssessor.class).get(0);

        assertEquals(mutationObj41.getHugoSymbol(), mutationObj42.getHugoSymbol());
        assertEquals(mutationObj41.getFunctionalImpact(), mutationObj42.getFunctionalImpact());
        assertEquals(mutationObj41.getFunctionalImpactScore(), mutationObj42.getFunctionalImpactScore(), 0);


        String input5 = "junkInput";
        String urlString5 = "http://mutationassessor.org/r3/?cm=var&var=junkInput&frm=json";
        MutationAssessor mutationObj51 = service.getMutationAssessor(input5);
        MutationAssessor mutationObj52 =
            Transformer.mapJsonToInstance(getReturnString(urlString5), MutationAssessor.class).get(0);

        assertEquals(mutationObj51.getHugoSymbol(), mutationObj52.getHugoSymbol());
        assertEquals(mutationObj51.getFunctionalImpact(), mutationObj52.getFunctionalImpact());
        assertEquals(mutationObj51.getFunctionalImpactScore(), mutationObj52.getFunctionalImpactScore(), 0);
    }

    private String getReturnString(String urlString) throws IOException {
        String output = "";
        URL url = new URL(urlString);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8")))
        {
            for (String line; (line = reader.readLine()) != null;)
            {
                output += line;
            }
        }
        return output;
    }

}

