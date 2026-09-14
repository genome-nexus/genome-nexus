package org.cbioportal.genome_nexus.web;

import org.cbioportal.genome_nexus.model.EnsemblCanonical;
import org.cbioportal.genome_nexus.model.EnsemblTranscript;
import org.cbioportal.genome_nexus.service.exception.NoEnsemblGeneIdForHugoSymbolException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.util.NestedServletException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/gn880",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration"
})
@AutoConfigureMockMvc
public class EnsemblSymbolLookupIntegrationTest
{
    private static final String COLLECTION = "ensembl.canonical_transcript_per_hgnc";
    private static final String TRANSCRIPT_COLLECTION = "ensembl.biomart_transcripts";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void seedMongo()
    {
        mongoTemplate.dropCollection(COLLECTION);
        mongoTemplate.dropCollection(TRANSCRIPT_COLLECTION);

        EnsemblCanonical tp53 = canonical("TP53", "ENSG_TP53", "ENST_TP53");
        tp53.setPreviousSymbols("P53");
        tp53.setSynonyms("TRP53");

        EnsemblCanonical brca1 = canonical("BRCA1", "ENSG_BRCA1", "ENST_BRCA1");
        brca1.setPreviousSymbols("");
        brca1.setSynonyms("");
        mongoTemplate.insert(Arrays.asList(tp53, brca1), COLLECTION);

        EnsemblTranscript tp53Transcript = transcript("ENST_TP53");
        EnsemblTranscript brca1Transcript = transcript("ENST_BRCA1");
        mongoTemplate.insert(Arrays.asList(tp53Transcript, brca1Transcript), TRANSCRIPT_COLLECTION);
    }

    @Test
    public void geneGetTreatsInputAsLiteral() throws Exception
    {
        mockMvc.perform(get("/ensembl/canonical-gene/hgnc/TP53"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hugoSymbol").value("TP53"));
        mockMvc.perform(get("/ensembl/canonical-gene/hgnc/tp53"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hugoSymbol").value("TP53"));

        for (String symbol : Arrays.asList("TP5.", "TP5*", ".*", "[T]P53", "TP53|BRCA1")) {
            assertGeneLookupDoesNotResolve(symbol);
        }

    }

    @Test
    public void genePostTreatsInputAsLiteralAndRetainsAliases() throws Exception
    {
        mockMvc.perform(post("/ensembl/canonical-gene/hgnc")
                .contentType("application/json")
                .content("[\"TP53\",\"tp53\",\"TP5.\",\"TP5*\",\".*\",\"[T]P53\",\"TP53|BRCA1\",\"P53\",\"TRP53\"]"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.hugoSymbol == 'TP53')]").exists())
            .andExpect(jsonPath("$.length()").value(4));
    }

    private void assertGeneLookupDoesNotResolve(String symbol) throws Exception
    {
        try {
            mockMvc.perform(get("/ensembl/canonical-gene/hgnc/" + symbol));
            throw new AssertionError("Expected no gene result for " + symbol);
        } catch (NestedServletException e) {
            assertTrue(e.getCause() instanceof NoEnsemblGeneIdForHugoSymbolException);
        }
    }

    @Test
    public void transcriptGetTreatsInputAsLiteral() throws Exception
    {
        mockMvc.perform(get("/ensembl/canonical-transcript/hgnc/TP53?isoformOverrideSource=ensembl"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.transcriptId").value("ENST_TP53"));
        mockMvc.perform(get("/ensembl/canonical-transcript/hgnc/tp53?isoformOverrideSource=ensembl"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.transcriptId").value("ENST_TP53"));

        for (String symbol : Arrays.asList("TP5.", "TP5*", ".*", "[T]P53", "TP53|BRCA1")) {
            mockMvc.perform(get("/ensembl/canonical-transcript/hgnc/" + symbol + "?isoformOverrideSource=ensembl"))
                .andExpect(status().isNotFound());
        }

    }

    @Test
    public void transcriptPostTreatsInputAsLiteralAndRetainsAliases() throws Exception
    {
        mockMvc.perform(post("/ensembl/canonical-transcript/hgnc?isoformOverrideSource=ensembl")
                .contentType("application/json")
                .content("[\"TP53\",\"tp53\",\"TP5.\",\"TP5*\",\".*\",\"[T]P53\",\"TP53|BRCA1\",\"P53\",\"TRP53\"]"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.transcriptId == 'ENST_TP53')]").exists())
            .andExpect(jsonPath("$.length()").value(4));
    }

    private EnsemblCanonical canonical(String symbol, String geneId, String transcriptId)
    {
        EnsemblCanonical canonical = new EnsemblCanonical();
        canonical.setHugoSymbol(symbol);
        canonical.setEnsemblCanonicalGeneId(geneId);
        canonical.setEnsemblCanonicalTranscriptId(transcriptId);
        canonical.setEntrezGeneId("1");
        return canonical;
    }

    private EnsemblTranscript transcript(String transcriptId)
    {
        EnsemblTranscript transcript = new EnsemblTranscript();
        transcript.setTranscriptId(transcriptId);
        transcript.setGeneId("ENSG_" + transcriptId.substring(6));
        transcript.setHugoSymbols(Arrays.asList(transcriptId.substring(6)));
        return transcript;
    }
}
