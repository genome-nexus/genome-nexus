package org.cbioportal.genome_nexus.web.api;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.internal.SignalQueryServiceImpl;
import org.cbioportal.genome_nexus.web.config.ApiObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBList;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
        "spring.data.mongodb.uri=mongodb://127.0.0.1:27017/annotator",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration",
        "server.port=38899",
        "cache.enabled=false",
        "vep.url=http://fake/VARIANT"
    }
)
public class AnnotationApiTest {
    
    private RestTemplate restTemplate = new RestTemplate();    

    private final ApiObjectMapper apiObjectMapper = new ApiObjectMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @MockBean
    private RestTemplate mockRestTemplate;

    @MockBean
    private SignalQueryServiceImpl signalQueryServiceImpl;

    private final String BASE_URL = "http://localhost:38899";
    private final String GENOME_NEXUS_RESPONSE_DIRECTORY = "api/expected-gn-output";
    private final String VEP_MOCKS_DIRECTORY = "api/vep-mocks";
    private final String DIFFS_DIR_NAME = "api-test-diffs";

    @Before
    public void setUp() throws Exception {
        Answer<BasicDBList> getAnswer = new Answer<BasicDBList>() {
            @Override
            public BasicDBList answer(InvocationOnMock invocation) throws Throwable {
                String requestUrl = invocation.getArgument(0);
                String[] requestUrlParts = requestUrl.split("/");
                String variant = requestUrlParts[requestUrlParts.length - 1];
                return objectMapper.readValue('[' + readVEPMockResponseFile(variant) + ']', BasicDBList.class);
            }
        };
        Answer<BasicDBList> postAnswer = new Answer<BasicDBList>() {
            @Override
            public BasicDBList answer(InvocationOnMock invocation) throws Throwable {
                Map<String, LinkedHashSet<String>> requestBody = invocation.getArgument(1);
                LinkedHashSet<String> variants = requestBody.get("hgvs_notations");

                List<String> mockResponse = new ArrayList<>();
                for (String variant : variants) {
                    mockResponse.add(readVEPMockResponseFile(variant));
                }               
                return objectMapper.readValue("[" + String.join(",", mockResponse) + "]", BasicDBList.class);
            }   
        };

        Mockito.when(mockRestTemplate.getForObject(Mockito.startsWith("http://fake"), Mockito.eq(BasicDBList.class))).thenAnswer(getAnswer);
        Mockito.when(mockRestTemplate.postForObject(Mockito.startsWith("http://fake"), Mockito.any(), Mockito.eq(BasicDBList.class))).thenAnswer(postAnswer);        
    }

    @Test
    public void testAnnotateByGenomicChangeUsingGet() throws Exception {
        File expectedResponseDir = new File(new ClassPathResource(GENOME_NEXUS_RESPONSE_DIRECTORY).getURI());
        File[] expectedResponses = expectedResponseDir.listFiles();

        boolean isFailure = false;
        for(int i = 0; i < expectedResponses.length; i++) {
            String variant = convertFileToVariant(expectedResponses[i]);

            String expectedResponse = readGenomeNexusResponseFile(variant);
            String actualResponse = restTemplate.getForObject(
                BASE_URL + "/annotation/genomic/" + 
                apiObjectMapper.readValue(expectedResponse, VariantAnnotation.class).getOriginalVariantQuery() 
                + "?isoformOverrideSource=mskcc&fields=annotation_summary", 
                String.class
            );
            
            if (checkFailureAndSaveDiffs(variant, expectedResponse, actualResponse)) {
                isFailure = true;
            }
        }

        assertEquals("There are test failures. See " + '"' + DIFFS_DIR_NAME + '"' + " folder for details.", isFailure, false);
    }

    @Test
    public void testAnnotateByGenomicChangeUsingPost() throws Exception {
        File expectedResponseDir = new File(new ClassPathResource(GENOME_NEXUS_RESPONSE_DIRECTORY).getURI());
        File[] expectedResponses = expectedResponseDir.listFiles();
        List<List<File>> expectedResponsesPerRequest = new ArrayList<>();
        int chunkSize = 10;
        for (int i = 0; i < expectedResponses.length; i++) {
            if (i % chunkSize == 0) {
                expectedResponsesPerRequest.add(new ArrayList<>());
            }
            expectedResponsesPerRequest.get(expectedResponsesPerRequest.size() - 1).add(expectedResponses[i]);
        }

        for (List<File> genomeNexusResponses : expectedResponsesPerRequest) {
            List<String> expectedResponse = new ArrayList<>();
            List<GenomicLocation> actualRequest = new ArrayList<>();
            for (int i = 0; i < genomeNexusResponses.size(); i++) {
                String variant = convertFileToVariant(genomeNexusResponses.get(i));
                String expectedResponseString = readGenomeNexusResponseFile(variant);
                expectedResponse.add(expectedResponseString);

                String[] genomicLocation = apiObjectMapper.readValue(expectedResponseString, VariantAnnotation.class)
                    .getOriginalVariantQuery()
                    .split(",");
                actualRequest.add(new GenomicLocation(
                    genomicLocation[0], 
                    Integer.parseInt(genomicLocation[1]), 
                    Integer.parseInt(genomicLocation[2]), 
                    genomicLocation[3],
                    genomicLocation[4]
                ));
            }

            List<Map<String, Object>> response = restTemplate.exchange(
                BASE_URL + "/annotation/genomic?isoformOverrideSource=mskcc&fields=annotation_summary",
                HttpMethod.POST,
                new HttpEntity<>(actualRequest),
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            )
            .getBody();
            List<String> actualResponse = new ArrayList<>();
            for (Map<String, Object> responseObject : response) {
                actualResponse.add(objectMapper.writeValueAsString(responseObject));
            }
            
            assertEquals(expectedResponse, actualResponse);
        }
    }

    private boolean checkFailureAndSaveDiffs(String variant, String expected, String actual) throws IOException {
        if (expected.equals(actual)) {
            return false;
        }

        Path outputDir = Paths.get("./src/test/" + DIFFS_DIR_NAME);
        Path expectedDir = Paths.get(outputDir + "/expected");
        Path actualDir = Paths.get(outputDir + "/actual");
        Files.createDirectories(expectedDir);
        Files.createDirectories(actualDir);

        String variantFile = convertVariantToFile(variant) + ".json";
        Path expectedFile = Paths.get(expectedDir + "/" + variantFile);
        Path actualFile = Paths.get(actualDir + "/" + variantFile);

        Files.write(expectedFile, expected.getBytes());
        Files.write(actualFile, actual.getBytes());
        return true;
    }

    private String readGenomeNexusResponseFile(String hgvs) throws Exception {
        return readVariantFile(hgvs, GENOME_NEXUS_RESPONSE_DIRECTORY);
    }

    private String readVEPMockResponseFile(String hgvs) throws Exception {
        return readVariantFile(hgvs, VEP_MOCKS_DIRECTORY);
    }

    private String readVariantFile(String hgvs, String directory) throws IOException {
        String file = directory + "/" + convertVariantToFile(hgvs) + ".json";

        return new String(Files.readAllBytes(Paths.get(new ClassPathResource(file).getURI())));
    }

    private String convertFileToVariant(File file) {
        return file.getName()
            .replace("_", ":")
            .replace("-", ">")
            .replace(".json", "");
    }

    private String convertVariantToFile(String variant) {
        return variant
            .replace(":", "_")
            .replace(">", "-");
    }
}
