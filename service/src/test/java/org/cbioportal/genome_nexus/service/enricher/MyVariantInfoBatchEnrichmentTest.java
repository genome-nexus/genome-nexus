package org.cbioportal.genome_nexus.service.enricher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.model.my_variant_info_model.MyVariantInfo;
import org.cbioportal.genome_nexus.service.cached.CachedMyVariantInfoFetcher;
import org.cbioportal.genome_nexus.service.config.ExternalResourceObjectMapper;
import org.cbioportal.genome_nexus.service.internal.MyVariantInfoServiceImpl;
import org.cbioportal.genome_nexus.service.remote.MyVariantInfoDataFetcher;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * A variant whose MyVariantInfo document we cannot use must be skipped on its own, leaving
 * every other variant in the batch annotated. This covers the whole fetch to enrich path,
 * where a single bad document used to make my_variant_info disappear from every variant in
 * the request while the request still returned 200.
 *
 * The document used here is rejected by the check in normalizeResponse. The other half of
 * the path, a document that passes that check and then fails to map, is covered by
 * ExternalResourceTransformerTest.
 */
public class MyVariantInfoBatchEnrichmentTest
{
    private static final String GOOD_ONE = "1:g.71873156G>T";
    private static final String BAD = "1:g.71873145T>G";
    private static final String GOOD_TWO = "1:g.71873162C>A";

    @Test
    public void oneUnusableDocumentDoesNotCostTheBatchItsAnnotation()
    {
        List<VariantAnnotation> annotations = Arrays.asList(
            annotation(GOOD_ONE), annotation(BAD), annotation(GOOD_TWO));

        enricher(response(
            mappable(GOOD_ONE), unusable(BAD), mappable(GOOD_TWO)
        )).enrich(annotations);

        assertNotNull(annotations.get(0).getMyVariantInfoAnnotation());
        assertNull(annotations.get(1).getMyVariantInfoAnnotation());
        assertNotNull(annotations.get(2).getMyVariantInfoAnnotation());

        MyVariantInfo enriched = annotations.get(0).getMyVariantInfoAnnotation().getAnnotation();
        assertEquals(GOOD_ONE, enriched.getHgvs());
        assertEquals("chr" + GOOD_ONE, enriched.getVariant());
    }

    @Test
    public void everyVariantIsAnnotatedWhenNothingFails()
    {
        List<VariantAnnotation> annotations = Arrays.asList(
            annotation(GOOD_ONE), annotation(GOOD_TWO));

        enricher(response(mappable(GOOD_ONE), mappable(GOOD_TWO))).enrich(annotations);

        for (VariantAnnotation annotation : annotations) {
            assertNotNull(annotation.getMyVariantInfoAnnotation());
        }
    }

    private MyVariantInfoAnnotationEnricher enricher(BasicDBList webServiceResponse)
    {
        ExternalResourceTransformer<MyVariantInfo> transformer =
            new ExternalResourceTransformer<>(new ExternalResourceObjectMapper());

        MyVariantInfoDataFetcher dataFetcher = Mockito.mock(MyVariantInfoDataFetcher.class);
        Mockito.when(dataFetcher.fetchRawValue(Mockito.any(Object.class))).thenReturn(webServiceResponse);

        // caching stays off, so the batch is served straight from the mocked web service
        CachedMyVariantInfoFetcher fetcher =
            new CachedMyVariantInfoFetcher(transformer, null, dataFetcher, 200);

        return new MyVariantInfoAnnotationEnricher(
            "my_variant_info", new MyVariantInfoServiceImpl(fetcher));
    }

    private VariantAnnotation annotation(String hgvsg)
    {
        VariantAnnotation annotation = new VariantAnnotation();
        annotation.setVariantId(hgvsg);

        return annotation;
    }

    private BasicDBList response(BasicDBObject... entities)
    {
        BasicDBList response = new BasicDBList();
        response.addAll(new ArrayList<>(Arrays.asList(entities)));

        return response;
    }

    private BasicDBObject mappable(String hgvsg)
    {
        BasicDBObject entity = new BasicDBObject();
        entity.put("_id", "chr" + hgvsg);
        entity.put("query", "chr" + hgvsg);

        return entity;
    }

    /** clinvar.allele_id is an Integer on the model, a nested object can never map onto it. */
    private BasicDBObject unusable(String hgvsg)
    {
        BasicDBObject entity = mappable(hgvsg);
        entity.put("clinvar", new BasicDBObject("allele_id", new BasicDBObject("nested", 1)));

        return entity;
    }
}
