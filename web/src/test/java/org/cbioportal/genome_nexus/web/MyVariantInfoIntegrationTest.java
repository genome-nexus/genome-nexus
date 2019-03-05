package org.cbioportal.genome_nexus.web;

import org.cbioportal.genome_nexus.model.my_variant_info_model.MyVariantInfo;
import org.cbioportal.genome_nexus.component.annotation.EntrezGeneXrefResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

    private MyVariantInfo fetchMyVariantInfoAnnotationGET(String variant)
    {
        return this.restTemplate.getForObject(BASE_URL + variant, MyVariantInfo.class);
    }
    private MyVariantInfo[] fetchMyVariantInfoAnnotationPOST(String[] variants)
    {
        return this.restTemplate.postForObject(BASE_URL, variants, MyVariantInfo[].class);
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

        MyVariantInfo ann0 = this.fetchMyVariantInfoAnnotationGET(variants[0]);
        // mutdb annotation should be 7
        assertEquals("7", ann0.getMutdb().getChrom());

        MyVariantInfo ann1 = this.fetchMyVariantInfoAnnotationGET(variants[1]);
        // mutdb annotation should be 7
        assertEquals("rs12190874", ann1.getDbsnp().getRsid());

        //////////////////
        // POST request //
        //////////////////

        MyVariantInfo[] anns = this.fetchMyVariantInfoAnnotationPOST(variants);

        // for each pdbId we should have one matching PdbHeader instance, except the invalid one
        assertEquals(anns.length, variants.length - 1);

        // GET and POST requests should return the same
        assertEquals(anns[0].getMutdb().getChrom(), ann0.getMutdb().getChrom());
        assertEquals(anns[1].getDbsnp().getRsid(), ann1.getDbsnp().getRsid());
    }
}
