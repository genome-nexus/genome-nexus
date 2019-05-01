package org.cbioportal.genome_nexus.web;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.List;

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

    private VariantAnnotation fetchVariantAnnotationGET(String variant)
    {
        return this.restTemplate.getForObject(BASE_URL + variant, VariantAnnotation.class);
    }

    private VariantAnnotation[] fetchVariantAnnotationPOST(List<String> variants)
    {
        return this.restTemplate.postForObject(BASE_URL, variants, VariantAnnotation[].class);
    }


    @Test
    public void testAnnotation()
    {

        String[] variant = {
            "12:g.43130875C>G",
            "12:g.43130996C>G"
        };

        //////////////////
        // GET requests //
        //////////////////

        VariantAnnotation ann0 = this.fetchVariantAnnotationGET(variant[0]);

        // The start location should be 43130875
        assertEquals(43130875, ann0.getStart().intValue());


        //////////////////
        // POST request //
        //////////////////

        VariantAnnotation[] anns = this.fetchVariantAnnotationPOST(Arrays.asList(variant));

        // GET and POST requests should return the same
        assertEquals(anns[0].getStart().intValue(), ann0.getStart().intValue());
    }
}
