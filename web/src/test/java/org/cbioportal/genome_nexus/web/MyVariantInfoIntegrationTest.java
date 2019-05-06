package org.cbioportal.genome_nexus.web;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
        "spring.data.mongodb.uri=mongodb://localhost/integration",
        "server.port=38892"
    }
)
public class MyVariantInfoIntegrationTest
{
    private final static String BASE_URL = "http://localhost:38892/my_variant_info/variant/";

    private RestTemplate restTemplate = new RestTemplate();

    private String fetchMyVariantInfoAnnotationGET(String variant)
    {
        return this.restTemplate.getForObject(BASE_URL + variant, String.class);
    }
    private String fetchMyVariantInfoAnnotationPOST(String[] variants)
    {
        return this.restTemplate.postForObject(BASE_URL, variants, String.class);
    }


    @Test
    public void testMyVariantInfoAnnotation()
    {
        String[] variants = {
            "7:g.140453136A>T",
            "rs12190874",
            "INVALID"
        };

        //////////////////
        // GET requests //
        //////////////////

        String response0 = this.fetchMyVariantInfoAnnotationGET(variants[0]);
        JsonParser springParser = JsonParserFactory.getJsonParser();
        Map<String, Object> map0 = springParser.parseMap(response0);
        HashMap<String, HashMap<String, Object>> mutdbGet0 = (HashMap) map0.get("mutdb");
        // mutdb annotation should be 7
        assertEquals("7", mutdbGet0.get("chrom"));
        HashMap<String, HashMap<String, Object>> gnomadExomeGet0 = (HashMap) map0.get("gnomadExome");
        HashMap<String, HashMap<String, Object>> alleleNumberGet0 = (HashMap) gnomadExomeGet0.get("alleleNumber");
        // total allele number should be 246028
        assertEquals(246028, alleleNumberGet0.get("an"));

        String response1 = this.fetchMyVariantInfoAnnotationGET(variants[1]);
        Map<String, Object> map1 = springParser.parseMap(response1);
        HashMap<String, HashMap<String, Object>> vcfGet1 = (HashMap) map1.get("vcf");
        // alt should be "A"
        assertEquals("A", vcfGet1.get("alt"));
        HashMap<String, HashMap<String, Object>> gnomadGenomeGet1 = (HashMap) map1.get("gnomadGenome");
        HashMap<String, HashMap<String, Object>> alleleNumberGet1 = (HashMap) gnomadGenomeGet1.get("alleleNumber");
        // total allele number should be 30914
        assertEquals(30914, alleleNumberGet1.get("an"));

        //////////////////
        // POST request //
        //////////////////

        String responses = this.fetchMyVariantInfoAnnotationPOST(variants);
        List<Object> maps = springParser.parseList(responses);
        HashMap<String, HashMap<String, Object>> mutdbPost0 = (HashMap) ((Map<String, Object>) maps.get(0)).get("mutdb");
        HashMap<String, HashMap<String, Object>> gnomadExomePost0 = (HashMap) ((Map<String, Object>) maps.get(0)).get("gnomadExome");
        HashMap<String, HashMap<String, Object>> alleleNumberPost0 = (HashMap) gnomadExomePost0.get("alleleNumber");

        HashMap<String, HashMap<String, Object>> vcfPost1 = (HashMap) ((Map<String, Object>) maps.get(1)).get("vcf");
        HashMap<String, HashMap<String, Object>> gnomadGenomePost1 = (HashMap) ((Map<String, Object>) maps.get(1)).get("gnomadGenome");
        HashMap<String, HashMap<String, Object>> alleleNumberPost1 = (HashMap) gnomadGenomePost1.get("alleleNumber");

        // for each pdbId we should have one matching PdbHeader instance, except the invalid one
        assertEquals(maps.size(), variants.length - 1);

        // GET and POST requests should return the same
        assertEquals(mutdbGet0.get("chrom"), mutdbPost0.get("chrom"));
        assertEquals(vcfGet1.get("alt"), vcfPost1.get("alt"));
        assertEquals(alleleNumberGet0.get("an"), alleleNumberPost0.get("an"));
        assertEquals(alleleNumberGet1.get("an"), alleleNumberPost1.get("an"));
    }
}
