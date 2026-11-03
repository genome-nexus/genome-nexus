package org.cbioportal.genome_nexus.service.cached;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.cbioportal.genome_nexus.model.AlphaGenome;
import org.cbioportal.genome_nexus.persistence.AlphaGenomeRepository;
import org.cbioportal.genome_nexus.service.remote.AlphaGenomeDataFetcher;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CachedAlphaGenomeFetcherTest
{
    @Mock
    private AlphaGenomeRepository repository;

    private ExternalResourceTransformer<AlphaGenome> transformer;
    private DBObject cannedResponse;

    @Before
    public void setUp()
    {
        transformer = new ExternalResourceTransformer<>(new ObjectMapper());
        cannedResponse = sidecarResponse("7:g.140453136A>T");
        when(repository.findAllById(anyCollection())).thenReturn(Collections.emptyList());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fetchesMissAndCachesByVariantId() throws Exception
    {
        CachedAlphaGenomeFetcher fetcher = fetcherReturning(cannedResponse);

        Map<String, Map<String, Object>> bodies = new LinkedHashMap<>();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("variant", "7:g.140453136A>T");
        body.put("chromosome", "chr7");
        body.put("position", 140753336);
        body.put("reference_allele", "A");
        body.put("alternate_allele", "T");
        body.put("canonical_gene_id", "ENSG00000157764");
        body.put("canonical_gene", "BRAF");
        bodies.put("7:g.140453136A>T", body);

        List<AlphaGenome> results = fetcher.fetchAndCache(bodies);

        assertEquals(1, results.size());
        assertEquals("7:g.140453136A>T", results.get(0).getVariant());
        assertEquals("chr7:140753336:A>T", results.get(0).getHg38Variant());
        assertEquals("BRAF", results.get(0).getCanonicalGene());
        assertEquals(1, results.get(0).getScores().size());
        assertEquals("SPLICE_SITES", results.get(0).getScores().get(0).getOutputType());

        ArgumentCaptor<List> saved = ArgumentCaptor.forClass(List.class);
        verify(repository).saveDBObjects(anyString(), saved.capture());
        assertEquals(1, saved.getValue().size());
        assertEquals("7:g.140453136A>T",
            ((DBObject) saved.getValue().get(0)).get("_id"));
    }

    @Test
    public void cacheHitSkipsSidecarCall() throws Exception
    {
        AlphaGenome cached = new AlphaGenome();
        cached.setVariant("7:g.140453136A>T");
        when(repository.findAllById(anyCollection()))
            .thenReturn(Collections.singletonList(cached));

        boolean[] called = {false};
        CachedAlphaGenomeFetcher fetcher = new CachedAlphaGenomeFetcher(
            transformer, repository, remoteReturning(cannedResponse, called), 1, 1);
        fetcher.cacheEnabled = true;

        Map<String, Map<String, Object>> bodies = new LinkedHashMap<>();
        bodies.put("7:g.140453136A>T", Collections.singletonMap("variant", "7:g.140453136A>T"));

        List<AlphaGenome> results = fetcher.fetchAndCache(bodies);

        assertEquals(1, results.size());
        assertEquals("7:g.140453136A>T", results.get(0).getVariant());
        assertTrue(!called[0]);
    }

    private CachedAlphaGenomeFetcher fetcherReturning(DBObject response)
    {
        CachedAlphaGenomeFetcher fetcher = new CachedAlphaGenomeFetcher(
            transformer, repository, remoteReturning(response, null), 1, 2);
        // @Value injection does not run in unit tests; enable caching explicitly.
        fetcher.cacheEnabled = true;
        return fetcher;
    }

    private AlphaGenomeDataFetcher remoteReturning(DBObject response, boolean[] called)
    {
        return new AlphaGenomeDataFetcher(transformer, "http://localhost:1/score", new RestTemplate())
        {
            @Override
            protected DBObject postForObject(String uri, Object requestBody)
            {
                if (called != null) {
                    called[0] = true;
                }
                return response;
            }
        };
    }

    private static DBObject sidecarResponse(String variant)
    {
        DBObject score = new BasicDBObject();
        score.put("output_type", "SPLICE_SITES");
        score.put("variant_scorer", "GeneMaskSplicingScorer");
        score.put("gene_id", "ENSG00000157764");
        score.put("gene_name", "BRAF");
        score.put("track_name", "donor_plus");
        score.put("raw_score", 0.08);
        score.put("quantile_score", 0.62);

        DBObject response = new BasicDBObject();
        response.put("variant", variant);
        response.put("hg38_variant", "chr7:140753336:A>T");
        response.put("scored_interval", "chr7:140229048-141277624");
        response.put("model_version", "alphagenome-v1");
        response.put("canonical_gene_id", "ENSG00000157764");
        response.put("canonical_gene", "BRAF");
        response.put("merged_splicing_score", 0.142);
        response.put("scores", Collections.singletonList(score));
        return response;
    }
}
