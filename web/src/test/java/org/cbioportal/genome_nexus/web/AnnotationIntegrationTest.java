package org.cbioportal.genome_nexus.web;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
        "vep.url=http://grch37.rest.ensembl.org/vep/human/hgvs/VARIANT?content-type=application/json&xref_refseq=1&ccds=1&canonical=1&domains=1&hgvs=1&numbers=1&protein=1",
        "spring.data.mongodb.uri=mongodb://localhost/integration",
        "server.port=38893"
    }
)
public class AnnotationIntegrationTest
{
    private final static String BASE_URL = "http://localhost:38893/annotation/";

    private RestTemplate restTemplate = new RestTemplate();

    private String fetchVariantAnnotationGET(String variant)
    {
        return this.restTemplate.getForObject(BASE_URL + variant, String.class);
    }

    private String fetchVariantAnnotationPOST(String[] variants)
    {
        return this.restTemplate.postForObject(BASE_URL, variants, String.class);
    }


    @Test
    public void testAnnotation()
    {

        String[] variants = {
            "12:g.43130875C>G",
            "12:g.43130996C>G"
        };

        //////////////////
        // GET requests //
        //////////////////

        String resp = this.fetchVariantAnnotationGET(variants[0]);
        JsonParser springParser = JsonParserFactory.getJsonParser();
        Map<String, Object> map = springParser.parseMap(resp);
        ArrayList<HashMap<String, Object>> intergenicConsequences = (ArrayList) map.get("intergenic_consequences");
        HashMap<String, Object> ann0 = (HashMap)intergenicConsequences.get(0);

        // the variant allele should be "G"
        assertEquals("G", ann0.get("variantAllele"));

        //////////////////
        // POST request //
        //////////////////

        String resp2 = this.fetchVariantAnnotationPOST(variants);
        List<Object> maps = springParser.parseList(resp2);
        ArrayList<HashMap<String, Object>> intergenicConsequencesPost = (ArrayList) ((Map<String, Object>) maps.get(0)).get("intergenic_consequences");
        HashMap<String, Object> annPost0 = (HashMap)intergenicConsequencesPost.get(0);
        // the length of vatiants and map size should be same
        assertEquals(variants.length, maps.size());

        // GET and POST requests should return the same
        assertEquals(ann0.get("variantAllele"), annPost0.get("variantAllele"));
    }
}
