package org.cbioportal.genome_nexus.web;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
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

    private Map<String, Object> fetchVariantAnnotationGET(String variant)
    {
        String response = this.restTemplate.getForObject(BASE_URL + variant, String.class);
        JsonParser springParser = JsonParserFactory.getJsonParser();
        return springParser.parseMap(response);
    }

    private List<Map<String, Object>> fetchVariantAnnotationPOST(String[] variants)
    {
        String responses = this.restTemplate.postForObject(BASE_URL, variants, String.class);
        JsonParser springParser = JsonParserFactory.getJsonParser();
        return (List<Map<String, Object>>)(List<?>) springParser.parseList(responses);
    }

    @Test
    public void testAnnotation()
    {

        String[] variants = {
            "12:g.43130875C>G",
            "12:g.43130996C>G",
            "INVALID"
        };

        //////////////////
        // GET requests //
        //////////////////

        // test intergenic_consequences
        String variantAllele = ((HashMap)((ArrayList) this.fetchVariantAnnotationGET(variants[0]).get("intergenic_consequences")).get(0)).get("variantAllele").toString();
        // variantAllele should be G
        assertEquals("G", variantAllele);

        //////////////////
        // POST request //
        //////////////////

        // for each variant we should have one matching instance, except the invalid one 
        assertEquals(variants.length - 1, this.fetchVariantAnnotationPOST(variants).size());

        String variantAllele0 = ((HashMap)((ArrayList) this.fetchVariantAnnotationPOST(variants).get(0).get("intergenic_consequences")).get(0)).get("variantAllele").toString();
        // the Get and Post should have same result
        assertEquals(variantAllele, variantAllele0);

    }
}
